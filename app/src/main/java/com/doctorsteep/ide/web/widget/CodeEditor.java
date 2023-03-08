package com.doctorsteep.ide.web.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.menu.CodeEditorMenu;
import com.doctorsteep.ide.web.settings.EditCodeSettings;
import com.doctorsteep.ide.web.utils.LangSyntax;
import com.doctorsteep.ide.web.utils.SyntaxUtils;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends AppCompatMultiAutoCompleteTextView {

	private Paint mPaint;
	
	public interface OnTextChangedListener {
		void onTextChanged(String text);
	}

	private static final Pattern PATTERN_TRAILING_WHITE_SPACE = Pattern.compile(
		"[\\t ]+$",
		Pattern.MULTILINE);
	private static final Pattern PATTERN_INSERT_UNIFORM = Pattern.compile(
		"^([ \t]*uniform.+)$",
		Pattern.MULTILINE);
	private static final Pattern PATTERN_ENDIF = Pattern.compile(
		"(#endif)\\b");

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final Handler updateHandler = new Handler();
	private final Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			Editable e = getText();

			if (onTextChangedListener != null) {
				onTextChangedListener.onTextChanged(
					removeNonAscii(e.toString()));
			}

			highlightWithoutChange(e);
		}
	};
	

	private OnTextChangedListener onTextChangedListener;
	private int updateDelay = 1000;
	private int errorLine = 0;
	private boolean dirty = false;
	private boolean modified = true;
	private int colorError;
	private int colorNumber;
	private int colorKeyword;
	private int colorBuiltin;
	private int colorComment;
	private int colorString;
	private int tabWidthInCharacters = 0;
	private int tabWidth = 0;
	
	private int mGutterWidth;
	private int mCharHeight = 0;
	private int h;
	
	private int HIGHLIGHTER_LINE = getResources().getColor(R.color.line);
    private Rect lineBounds;
    private Paint highlightPaint;
	private int lineNumber;
    private boolean lineHighlightEnabled = true;
	
	final static float STEP = 200;
    private float mRatio = 1.0f;
	private float limitRation = 20.0f;
    private int mBaseDist;
    private float mBaseRatio;
    private float fontsize = 12;

    private Thread timerThread;

	int topVisibleLine;
	int bottomVisibleLine;
	int lineStart;
	int lineEnd;

	// private Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
	// Matcher mSelected = colorPattern.matcher(getText().toString());
	// boolean isColorSelected = mSelected.matches();

	public static String removeNonAscii(String text) {
		return text.replaceAll("[^\\x0A\\x09\\x20-\\x7E]", "");
	}

	public CodeEditor(Context context) {
		super(context);
		init(context);
	}

	public CodeEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setOnTextChangedListener(OnTextChangedListener listener) {
		onTextChangedListener = listener;
	}

	public void setUpdateDelay(int ms) {
		updateDelay = ms;
	}

	public void setTabWidth(int characters) {
		if (tabWidthInCharacters == characters) {
			return;
		}

		tabWidthInCharacters = characters;
		tabWidth = Math.round(getPaint().measureText("m") * characters);
	}

	public boolean hasErrorLine() {
		return errorLine > 0;
	}

	public void setErrorLine(int line) {
		errorLine = line;
	}

	public void updateHighlighting() {
		highlightWithoutChange(getText());
	}

	public boolean isModified() {
		return dirty;
	} 

	public void setTextHighlighted(CharSequence text) {
		if (text == null) {
			text = "";
		}

		cancelUpdate();

		errorLine = 0;
		dirty = false;

		modified = false;
		String src = removeNonAscii(text.toString());
		setText(highlight(new SpannableStringBuilder(src)));
		modified = true;

		if (onTextChangedListener != null) {
			onTextChangedListener.onTextChanged(src);
		}
	}

	public String getCleanText() {
		return PATTERN_TRAILING_WHITE_SPACE
			.matcher(getText())
			.replaceAll("");
	}

	public void insertTab() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		getText().replace(Math.min(start, end), Math.max(start, end), EditCodeSettings.TAB_SIZE, 0, 1);
	}
	
	public void addTextCursorPosition(String text) {
		getEditableText().insert(getSelectionStart(), text);
	}

	public void addUniform(String statement) {
		if (statement == null) {
			return;
		}

		Editable e = getText();
		removeUniform(e, statement);

		Matcher m = PATTERN_INSERT_UNIFORM.matcher(e);
		int start = -1;

		while (m.find()) {
			start = m.end();
		}

		if (start > -1) {
			// add line break before statement because it's
			// inserted before the last line-break
			statement = "\n" + statement;
		} else {
			// add a line break after statement if there's no
			// uniform already
			statement += "\n";

			// add an empty line between the last #endif
			// and the now following uniform
			if ((start = endIndexOfLastEndIf(e)) > -1) {
				statement = "\n" + statement;
			}

			// move index past line break or to the start
			// of the text when no #endif was found
			++start;
		}

		e.insert(start, statement);
	}

	private void removeUniform(Editable e, String statement) {
		if (statement == null) {
			return;
		}

		String regex = "^(" + statement.replace(" ", "[ \\t]+");
		int p = regex.indexOf(";");
		if (p > -1) {
			regex = regex.substring(0, p);
		}
		regex += ".*\\n)$";

		Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(e);
		if (m.find()) {
			e.delete(m.start(), m.end());
		}
	}

	private int endIndexOfLastEndIf(Editable e) {
		Matcher m = PATTERN_ENDIF.matcher(e);
		int idx = -1;

		while (m.find()) {
			idx = m.end();
		}

		return idx;
	}

	private void init(Context context) {
		topVisibleLine = getScrollY() / getLineHeight();
		bottomVisibleLine = topVisibleLine + getHeight() / getLineHeight() + 1;
		if (getLayout() != null) {
			lineStart = getLayout().getLineStart(topVisibleLine);
			lineEnd = getLayout().getLineEnd(bottomVisibleLine);
		}

		lineBounds = new Rect();
        highlightPaint = new Paint();
		mPaint = new Paint();
		highlightPaint.setColor(HIGHLIGHTER_LINE);
		mPaint.setColor(getResources().getColor(R.color.colorTextLineNum));
		setHorizontallyScrolling(true);
		setLineHighlightEnabled(true);
		setTypeface(Typeface.MONOSPACE);
		setTextSize(fontsize);
		setBackgroundColor(getResources().getColor(android.R.color.transparent));
		setGravity(Gravity.TOP|Gravity.LEFT);
		setThreshold(1);
		// setMaxLines(1500);
		setElevation(0f);
		// setOnCreateContextMenuListener(new CodeEditorMenu());
		setCursorColor(ContextCompat.getColor(context, R.color.syntax_cursor));
		setDropDownBackgroundResource(R.drawable.bg_syntax);
		setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		setFilters(new InputFilter[]{new InputFilter() {
						   @Override
						   public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
							   if (modified &&
								   end - start == 1 &&
								   start < source.length() &&
								   dstart < dest.length()) {
								   char c = source.charAt(start);

								   if (c == '\n') {
									   return autoIndent(source, dest, dstart, dend);
								   }
							   }

							   return source;
						   }
					   }});

		addTextChangedListener(new TextWatcher() {
				private int start = 0;
				private int count = 0;

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					this.start = start;
					this.count = count;

					onPopupChangePosition();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					
				}

				@Override
				public void afterTextChanged(Editable e) {
					cancelUpdate();
					convertTabs(e, start, count);

					if (!modified) {
						return;
					}

					dirty = true;
					updateHandler.postDelayed(updateRunnable, updateDelay);
				}
			});

		setSyntaxColors(context);
		// setUpdateDelay(CodeEditorApp.preferences.getUpdateDelay());
		// setTabWidth(CodeEditorApp.preferences.getTabWidth());
	}

	private void setSyntaxColors(Context context) {
		colorError = ContextCompat.getColor(context, R.color.syntax_error);
		colorNumber = ContextCompat.getColor(context, R.color.syntax_number);
		colorKeyword = ContextCompat.getColor(context, R.color.syntax_keyword);
		colorBuiltin = ContextCompat.getColor(context, R.color.syntax_builtin);
		colorComment = ContextCompat.getColor(context, R.color.syntax_comment);
		colorString = ContextCompat.getColor(context, R.color.syntax_string);
	}

	private void cancelUpdate() {
		updateHandler.removeCallbacks(updateRunnable);
	}

	private void highlightWithoutChange(Editable e) {
		modified = false;
		highlight(e);
		modified = true;
	}
	
	public String type = LangSyntax.HTML;
	
	private Editable highlight(Editable e) {
		try {
			int length = e.length();

			// don't use e.clearSpans() because it will
			// remove too much
			clearSpans(e, length);

			if (length == 0) {
				return e;
			}

			setColorPattern(e);
			
			//HTML
			if(type.equals(LangSyntax.HTML)) {
				setSyntaxHTML(e);
			}
			
			//CSS
			if(type.equals(LangSyntax.CSS)) {
				setSyntaxCSS(e);
			}
			
			//JS
			if(type.equals(LangSyntax.JAVASCRIPT)) {
				setSyntaxJS(e);
			}
		} catch (IllegalStateException ex) {
			// raised by Matcher.start()/.end() when
			// no successful match has been made what
			// shouldn't ever happen because of find()
		}

		return e;
	}

	private static void clearSpans(Editable e, int length) {{
			ForegroundColorSpan[] spans = e.getSpans(
				0,
				length,
				ForegroundColorSpan.class);

			for (int i = spans.length; i-- > 0; ) {
				e.removeSpan(spans[i]);
			}
		}
		{
			BackgroundColorSpan[] spans = e.getSpans(0, length, BackgroundColorSpan.class);

			for (int i = spans.length; i-- > 0; ) {
				e.removeSpan(spans[i]);
			}
		}
	}

	private CharSequence autoIndent(
		CharSequence source,
		Spanned dest,
		int dstart,
		int dend) {
		String indent = "";
		int istart = dstart - 1;

		// find start of this line
		boolean dataBefore = false;
		int pt = 0;

		for (; istart > -1; --istart) {
			char c = dest.charAt(istart);

			if (c == '\n') {
				break;
			}

			if (c != ' ' && c != '\t') {
				if (!dataBefore) {
					// indent always after those characters
					if (c == '{' ||
						c == '+' ||
						c == '-' ||
						c == '*' ||
						c == '/' ||
						c == '%' ||
						c == '^' ||
						c == '=' ||
						c == '<') {
						--pt;
					}

					dataBefore = true;
				}

				// parenthesis counter
				if (c == '(') {
					--pt;
				} else if (c == ')') {
					++pt;
				}
			}
		}

		// copy indent of this line into the next
		if (istart > -1) {
			char charAtCursor = dest.charAt(dstart);
			int iend;

			for (iend = ++istart; iend < dend; ++iend) {
				char c = dest.charAt(iend);

				// auto expand comments
				if (charAtCursor != '\n' &&
					c == '/' &&
					iend + 1 < dend &&
					dest.charAt(iend) == c) {
					iend += 2;
					break;
				}

				if (c != ' ' && c != '\t') {
					break;
				}
			}

			indent += dest.subSequence(istart, iend);
		}

		// add new indent
		if (pt < 0) {
			indent += "";
		}

		// append white space of previous line and new indent
		return source + indent;
	}

	private void convertTabs(Editable e, int start, int count) {
		if (tabWidth < 1) {
			return;
		}
		String s = e.toString();
		for (int stop = start + count; (start = s.indexOf("", start)) > -1 && start < stop; ++start) {
			e.setSpan(new TabWidthSpan(tabWidth), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private static class TabWidthSpan extends ReplacementSpan {
		private int width;

		private TabWidthSpan(int width) {
			this.width = width;
		}

		@Override
		public int getSize(
			@NonNull Paint paint,
			CharSequence text,
			int start,
			int end,
			Paint.FontMetricsInt fm) {
			return width;
		}

		@Override
		public void draw(
			@NonNull Canvas canvas,
			CharSequence text,
			int start,
			int end,
			float x,
			int top,
			int y,
			int bottom,
			@NonNull Paint paint) {
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
        if (event.getPointerCount() == 2) {
            int action = event.getAction();
            int pureaction = action & MotionEvent.ACTION_MASK;
            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                mBaseDist = getDistance(event);
                mBaseRatio = mRatio;
            } else {
                float delta = (getDistance(event) - mBaseDist) / STEP;
                float multi = (float) Math.pow(2, delta);
                mRatio = Math.min(limitRation, Math.max(0.1f, mBaseRatio * multi));
                setTextSize(mRatio + fontsize);
            }
        }
        return true;
    }

    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }
	
	public boolean onTouch(View v, MotionEvent event) {
		/*Matcher mSelected = SyntaxUtils.COLOR_HEX.matcher(codeEditor.getText());
		boolean isColorSelected = mSelected.matches();
		if (isColorSelected) {
			Toast.makeText(getApplicationContext(), "Color selected", Toast.LENGTH_SHORT).show();
		}*/

        return true;
    }
	
	public void setCursorColor(@ColorInt int color) {
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);

            int drawableResId = field.getInt(this);

            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
			
            Object editor = field.get(this);
            Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);

            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            Drawable[] drawables = {drawable, drawable};
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception e) {}
    }

	public int getCurrentCursorLine() {
		int selectionStart = Selection.getSelectionStart(this.getText());
		Layout layout = this.getLayout();
		if (!(selectionStart == -1)) {
			return layout.getLineForOffset(selectionStart) + 1;
		}
		return -1;
	}
	
	@Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateHighlighting();
        setDropDownWidth(w * 1/2);
        setDropDownHeight(h * 1/2);
    }
	
	/*@Override
    public void showDropDown() {
        if (!isPopupShowing()) {
            if (hasFocus()) {
                super.showDropDown();
            }
        }
    }*/
	
	@Override

    public int getCompoundPaddingLeft() {
        int symbol_offset = (int)(getTextSize() / 1.5);
        return Integer.toString(getLineCount()).length() * symbol_offset + symbol_offset;
    }

	@Override
	protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
		super.onScrollChanged(horiz, vert, oldHoriz, oldVert);

		topVisibleLine = getScrollY() / getLineHeight();
		bottomVisibleLine = topVisibleLine + getHeight() / getLineHeight() + 1;
		if (getLayout() != null) {
			lineStart = getLayout().getLineStart(topVisibleLine);
			lineEnd = getLayout().getLineEnd(bottomVisibleLine);
		}

		updateHighlighting();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);
		} catch (Exception e) {
			
		}
		
		if (lineHighlightEnabled) {
            lineNumber = getLayout().getLineForOffset(getSelectionStart());
            getLineBounds(lineNumber, lineBounds);

            canvas.drawRect(lineBounds, highlightPaint);
        }
		
		int count = getLineCount();
		for(int i = 0; i < count; i++){
			int baseline = getLineBounds(i, null);
			String num = Integer.toString(i + 1);
			canvas.drawText(num, 10 + getScrollX(), baseline, mPaint);
		}
		
		// drawing vertical divider
        int x = getScrollX() + getCompoundPaddingLeft();
        int y = Math.max(getLineHeight() * getLineCount(), getHeight());
        canvas.drawLine(x, 0, x, y, mPaint);
	}
	
	@Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mPaint.setTextSize(getTextSize());
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mPaint.setTextSize(getTextSize());
    }
	
	@Override
    public void showDropDown() {
        if (!isPopupShowing()) {
            if (hasFocus()) {
                super.showDropDown();
            }
        }
    }


    public void setReplaceText(String with, String to) {
		String replace = "";
		replace = this.getText().toString().replaceAll(with, to);
		this.setText(replace);
	}

	
	
	
	
	public void setLineHighlightEnabled(boolean enabled) {
        lineHighlightEnabled = enabled;
        invalidate();
    }
	public void setLineHighlightColor(int color) {
        highlightPaint.setColor(color);
        if (lineHighlightEnabled) {
            invalidate();
        }
    }
	public boolean isLineHighlightEnabled() {
        return lineHighlightEnabled;
    }
	public int getLineHighlightColor() {
        return highlightPaint.getColor();
    }


    private void onPopupChangePosition() {
		Layout layout = this.getLayout();

        if (layout != null) {
			int line = layout.getLineForOffset(getSelectionStart() + 10);

			float x = layout.getPrimaryHorizontal(getSelectionStart());
			float y = layout.getLineBaseline(line);

			int offsetHorizontal = (int) x + mGutterWidth;
			setDropDownHorizontalOffset(offsetHorizontal);

			int offsetVertical = (int) y - getScrollY();
			int offset = offsetVertical + getDropDownHeight();
			if (offset < getVisibleHeight()) {
				setDropDownVerticalOffset(offsetVertical);
			} else {
				setDropDownVerticalOffset(offsetVertical - getDropDownHeight());
			}
		}
    }

    private int getVisibleHeight() {
		Rect rectVH = new Rect();
		getWindowVisibleDisplayFrame(rectVH);
		return rectVH.bottom - rectVH.top;
	}
	
	public void selectLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        setSelection(start, end);
    }
	
	public void duplicateLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        getEditableText().insert(end, "\n" + getText().subSequence(start, end).toString());
    }
	
	public void deleteLine() {
        int start = Math.min(getSelectionStart(), getSelectionEnd());
        int end = Math.max(getSelectionStart(), getSelectionEnd());
        if (end > start) {
            end--;
        }
        while (end < getText().length() && getText().charAt(end) != '\n') {
            end++;
        }
        while (start > 0 && getText().charAt(start - 1) != '\n') {
            start--;
        }
        getEditableText().delete(start, end);
    }

    public void goToLine(int line) {

	}

	public int getTrueLineCount() {
		int count;
		String text = getText().toString();
		StringReader sr = new StringReader(text);
		LineNumberReader lnr = new LineNumberReader(sr);
		try {
			lnr.skip(Long.MAX_VALUE);
			count = lnr.getLineNumber() + 1;
		} catch (Exception e) {
			count = 0;
		}
		sr.close();
		return count;
	}

	public void goCommentSelectedLine(String typeComment) {
		if (type.equals(LangSyntax.PHP) && typeComment.equals("php")) {
			selectLine();
			getText().insert(getSelectionStart(), "#");
			return;
		}
		if (type.equals(LangSyntax.HTML) || type.equals(LangSyntax.PHP)) {
			selectLine();
			getText().insert(getSelectionStart(), "<!--");
			getText().insert(getSelectionEnd(), "-->");
			return;
		}
		if (type.equals(LangSyntax.CSS)) {
			selectLine();
			getText().insert(getSelectionStart(), "/*");
			getText().insert(getSelectionEnd(), "*/");
			return;
		}
		if (type.equals(LangSyntax.JAVASCRIPT)) {
			if (getSelectionStart() == getSelectionEnd()) {
				selectLine();
				getText().insert(getSelectionStart(), "//");
				return;
			} else {
				selectLine();
				getText().insert(getSelectionStart(), "/*");
				getText().insert(getSelectionEnd(), "*/");
				return;
			}
		}
	}

	protected int getHeightVisible() {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }
	
	
	
	
	
	
	
	//Syntax utils
    public void setSyntaxPHP(Editable e) { //----------------PHP

    }
	public void setSyntaxHTML(Editable e) { //----------------HTML
		for (Matcher m = SyntaxUtils.HTML_START_ELEMENT.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_END_ELEMENT.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_START_TAG.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorBuiltin), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_END_TAG.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorBuiltin), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_STRING_PATTERN.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorError), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_COMMENTS.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_COMMENTS_TWO.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		for (Matcher m = SyntaxUtils.HTML_COMMENTS_XML.matcher(e); m.find(); ) {
			boolean isInText = m.start() >= 0 && m.end() <= e.length();
			boolean isValid = m.start() <= m.end();
			// boolean isVisible = m.start() >= lineStart - lineEnd || m.start() <= lineEnd && m.end() >= lineStart;
			boolean isVisible = m.start() <= lineEnd && m.end() >= lineStart;
			if (isInText && isValid && isVisible) {
				e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
	public void setSyntaxCSS(Editable e) { //-----------------CSS
		for (Matcher m = SyntaxUtils.CSS_KEYWORDS_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorNumber), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.CSS_START_TAG.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.CSS_END_TAG.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.CSS_STRING_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorError), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.CSS_COMMENTS_MULTI_LINE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	public void setSyntaxJS(Editable e) { //-----------------JS
		for (Matcher m = SyntaxUtils.JS_KEYWORDS_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorNumber), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_KEYWORDS_PATTERN_TWO.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_KEYWORDS_PATTERN_THREE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_KEYWORDS_PATTERN_FOUR.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorNumber), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_BUILTIN_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorError), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_SYMBOL_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorString), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_SYMBOL_PATTERN_TWO.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorNumber), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_SYMBOL_PATTERN_THREE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_STRING_PATTERN.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorError), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_STRING_PATTERN_TWO.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorError), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_COMMENTS_CONSOLE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_COMMENTS_SINGLE_LINE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		for (Matcher m = SyntaxUtils.JS_COMMENTS_MULTI_LINE.matcher(e); m.find(); ) {
			e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	public void setColorPattern(Editable e) {
		for (Matcher m = SyntaxUtils.COLOR_HEX.matcher(e); m.find(); ) {
			try {
				SpannableStringBuilder str = new SpannableStringBuilder(getText());
				String colorBack = str.subSequence(m.start(), m.end()).toString();
				// Log.e("SPAN", m.start() + " - " + m.end());
				// Log.e("SPAN STRING", str.subSequence(m.start(), m.end()).toString());
				e.setSpan(new StyleSpan(Typeface.NORMAL), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				e.setSpan(new BackgroundColorSpan(Color.parseColor(colorBack)), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // e.setSpan(new UnderlineSpan(), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (EditCodeSettings.isDarkColor(Color.parseColor(colorBack))) {
					e.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					e.setSpan(new ForegroundColorSpan(Color.parseColor("#FF000000")), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				// e.setSpan(new ForegroundColorSpan(Color.parseColor(str.subSequence(m.start(), m.end()).toString())), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} catch (Exception ex) {}
		}
	}
}
