package com.doctorsteep.ide.web.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.content.ContextCompat;

public class CheckPermission {
	
	public static boolean checkStorage(Context context) {
		return ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
	}
	
	public static boolean isExternalStorageWritable() {
		return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
	}
	
	public static boolean isExternalStorageReadable() {
		return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY;
	}
}
