package com.doctorsteep.ide.web.input;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import com.doctorsteep.ide.web.R;

public class KeyboardIDE extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

	private KeyboardView keyboardView;
	private Keyboard keyboard;
	private Constants.KEYS_TYPE currentLocale;
    private Constants.KEYS_TYPE previousLocale;
	private boolean capsOn = true;
	
    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.key_view, null);
		currentLocale = Constants.KEYS_TYPE.ENGLISH;
        keyboard = getKeyboard(currentLocale);
        keyboard.setShifted(capsOn);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
		
        return keyboardView;
    }
    private Keyboard getKeyboard(Constants.KEYS_TYPE locale) {
        switch (locale) {
            case RUSSIAN:
                return new Keyboard(this, R.xml.pad_en);
            case ENGLISH:
                return new Keyboard(this, R.xml.pad_en);
            case SYMBOLS:
                return new Keyboard(this, R.xml.pad_en);
            default:
                return new Keyboard(this, R.xml.pad_en);
        }
    }
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
		
        if (ic == null) return;
		
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
			case Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
			case Keyboard.KEYCODE_ALT:
                handleSymbolsSwitch();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                handleLanguageSwitch();
                break;
            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
        }
    }
    @Override
    public void onPress(int primaryCode) {
		
	}
    @Override
    public void onRelease(int primaryCode) {
		
	}
    @Override
    public void onText(CharSequence text) {
		
	}
    @Override
    public void swipeLeft() {
		
	}
    @Override
    public void swipeRight() {
		
	}
    @Override
    public void swipeDown() {
		
	}
    @Override
    public void swipeUp() {
		
	}
	
	private void handleSymbolsSwitch() {
        if (currentLocale != Constants.KEYS_TYPE.SYMBOLS) {
            keyboard = getKeyboard(Constants.KEYS_TYPE.SYMBOLS);
            previousLocale = currentLocale;
            currentLocale = Constants.KEYS_TYPE.SYMBOLS;
        } else {
            keyboard = getKeyboard(previousLocale);
            currentLocale = previousLocale;
            keyboard.setShifted(capsOn);
        }
        keyboardView.setKeyboard(keyboard);
        keyboardView.invalidateAllKeys();
    }

    private void handleShift() {
        capsOn = !capsOn;
        keyboard.setShifted(capsOn);
       	keyboardView.invalidateAllKeys();
    }

    private void handleLanguageSwitch() {
        if (currentLocale == Constants.KEYS_TYPE.RUSSIAN) {
            currentLocale = Constants.KEYS_TYPE.ENGLISH;
            keyboard = getKeyboard(Constants.KEYS_TYPE.ENGLISH);
        } else {
            currentLocale = Constants.KEYS_TYPE.RUSSIAN;
            keyboard = getKeyboard(Constants.KEYS_TYPE.RUSSIAN);
        }

        keyboardView.setKeyboard(keyboard);
        keyboard.setShifted(capsOn);
        keyboardView.invalidateAllKeys();
    }
}
