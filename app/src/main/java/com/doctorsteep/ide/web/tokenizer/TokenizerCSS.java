package com.doctorsteep.ide.web.tokenizer;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import com.doctorsteep.ide.web.settings.EditCodeSettings;
import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.language.LanguageCSS;

public class TokenizerCSS implements Tokenizer {

	@Override
	public CharSequence terminateToken(CharSequence text) {
		int i = text.length();

		try {
			if (LanguageCSS.cssProperty().contains(text.toString())) {
				text = text + ": ";
			}
		} catch(Exception e) {}
		while (i > 0 && text.charAt(i - 1) == ' ') { i--; }
		if (text instanceof Spanned) {
			SpannableString sp = new SpannableString(text);
			TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
			return sp;
		} else {
			return text;
		}
	}

	@Override
	public int findTokenStart(CharSequence text, int cursor) {
		int i = cursor;
		while (i > 0 && text.charAt(i - 1) != ' ') {
			i--;
		}

		while (i < cursor && text.charAt(i) == ' ') {
			i++;
		}

		return i;
	}

	@Override
	public int findTokenEnd(CharSequence text, int cursor) {
		int i = cursor;
		int len = text.length();
		while (i < len) {
			if (text.charAt(i) == ' ') {
				return i;
			} else {
				i++;
			}
		}
		return len;
	}

}
