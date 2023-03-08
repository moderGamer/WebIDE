package com.doctorsteep.ide.web.settings;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;

import com.doctorsteep.ide.web.EditorActivity;

public class EditCodeSettings {
	
	public static final String TAB_SIZE = "    ";


	public static boolean isDarkColor(int color) {
		double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
		if (darkness < 0.5) {
			return false;
		} else {
			return true;
		}
	}
}
