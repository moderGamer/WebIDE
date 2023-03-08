package com.doctorsteep.ide.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import com.doctorsteep.ide.editor.data.ThemeEditor;
import com.doctorsteep.ide.editor.data.UndoRedo;

@SuppressLint("AppCompatCustomView")
public class CodeEditor extends AppCompatAutoCompleteTextView {

    public CodeEditor(Context context) {
        super(context);
        init();
    }
    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CodeEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setTheme(ThemeEditor.LIGHT);
        this.setTextSize((int)getContext().getResources().getDimension(R.dimen.textSize));
        this.setGravity(Gravity.LEFT|Gravity.TOP);
        this.setPadding(
                (int)getContext().getResources().getDimension(R.dimen.textPaddingLeft),
                (int)getContext().getResources().getDimension(R.dimen.textPaddingTop),
                (int)getContext().getResources().getDimension(R.dimen.textPaddingRight),
                (int)getContext().getResources().getDimension(R.dimen.textPaddingBottom)
        );

        new UndoRedo(this);
    }

    public void undo() {
        new UndoRedo(this).undo();
    }
    public void redo() {
        new UndoRedo(this).redo();
    }


    // THEME FOR EDITOR
    public void setTheme(int theme) {
        switch (theme) {
            case 0:
                dark();
                break;
            case 1:
                light();
                break;
            default:
                light();
                break;
        }
    }
    private void dark() {
        this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.editorBackgroundDark));
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorDark));
        this.setHintTextColor(ContextCompat.getColor(getContext(), R.color.textColorHintDark));
    }
    private void light() {
        this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.editorBackground));
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        this.setHintTextColor(ContextCompat.getColor(getContext(), R.color.textColorHint));
    }
}
