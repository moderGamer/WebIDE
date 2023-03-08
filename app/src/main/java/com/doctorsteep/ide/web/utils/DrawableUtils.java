package com.doctorsteep.ide.web.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;

public class DrawableUtils {
	
	public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
		drawable.clearColorFilter();
		drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		drawable.invalidateSelf();
		Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
		DrawableCompat.setTint(wrapDrawable, color);
		return wrapDrawable;
	}
}
