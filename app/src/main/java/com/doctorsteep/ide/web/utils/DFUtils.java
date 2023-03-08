package com.doctorsteep.ide.web.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class DFUtils {
	
	public static String dateDF(String patch) throws Exception {
		File file = new File(patch);
		Date lastModDate = new Date(file.lastModified());
		String date = DateFormat.getDateInstance(DateFormat.LONG).format(lastModDate);
		return date;
	}
	
	public static String dateCreateDF(String patch) {
		File file = new File(patch);
		return "";
	}
	
	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}
	
	public static String sizeFormat(long bytes) {
		String s = bytes < 0 ? "-" : "";
		long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		return b < 1000L ? bytes + "B"
            : b < 999_950L ? String.format("%s%.1f" + "KB", s, b / 1e3)
            : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "MB", s, b / 1e3)
            : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "GB", s, b / 1e3)
            : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "TB", s, b / 1e3)
            : (b /= 1000) < 999_950L ? String.format("%s%.1f" + "PB", s, b / 1e3)
            : String.format("%s%.1f" + "EB", s, b / 1e6);
	}
	
	public static void openFile(Context context, String path, String type) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + path), type);
			context.startActivity(intent);
		} catch(Exception e) {
			Toast.makeText(context, "Failed open file", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static String getMimeType(File file) {
		String type = "text/*";
		final String url = file.toString();
		final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != "") {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
		} if (type == "") {
			type = "text/*";
		}
		return type;
	}
}
