package com.doctorsteep.ide.web.data;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.doctorsteep.ide.web.R;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.doctorsteep.ide.web.BrowserActivity;

public class Data {
	
	public static boolean settingChange = false;
	private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnmm";
	
	public static void setTheme(Context context) {
		SharedPreferences sharedData = PreferenceManager.getDefaultSharedPreferences(context);
		switch (sharedData.getString("keyTheme", "0")) {
			case "0": // System
				break;
			case "1": // Light
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
				break;
			case "2": // Night
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
				break;
		}
	}
	
	public static int setThemeDialog(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int theme = R.style.AlertDialog;
		return theme;
	}

	public static int getRandomNum(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static String getRandomString(int size) {
		final Random random = new Random();
		final StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; ++i) {
			sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
		}
		return sb.toString();
	}

	public static void openUrl(Context context, String url, String title) {
		try {
			context.startActivity(new Intent(context, BrowserActivity.class).putExtra("url", url).putExtra("title", title));
		} catch(Exception e) {
			Toast.makeText(context, "Failed! - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public static void customFontAll(Context _con, String _defaultFontNameToOverride, String _customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(_con.getAssets(), _customFontFileNameInAssets);
			final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(_defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            //Log.e("Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
	}
	
	public static void LogCat(Context context, boolean bool) {
        try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String dateFormat = new SimpleDateFormat("dd.MM.yy_HH:mm" ).format(Long.valueOf(System.currentTimeMillis()));

			CharSequence name = context.getApplicationInfo().loadLabel(context.getPackageManager());
            File file = new File(createFolder(name), context.getPackageName().concat("_version." + pinfo.versionName).concat("_date." + dateFormat).concat(".logcat"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("logcat -d -f ");
            stringBuilder.append(file.getAbsolutePath());
            Runtime.getRuntime().exec(stringBuilder.toString());
            if (bool) {
                //Toast.makeText(context, "Log создан в: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
			e.printStackTrace();
        }
    }

	public static String createFolder(CharSequence app_name) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory() + File.separator); 
 	    stringBuilder.append(app_name + "/");
        String stringBuilder2 = stringBuilder.toString();
        File file = new File(stringBuilder2);
        if (!file.exists()) {
            file.mkdir();
        }
        return stringBuilder2;
    }

	public static boolean deleteDF(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDF(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void setGrantPermissionsSettings(final Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Permission");
		builder.setMessage("Pass to settings and allow torage access for the app");
		builder.setCancelable(false);
		builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToSettings(context);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public static void goToSettings(Context context) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static boolean isVerifyGP(Context context) {
		List<String> valid = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
		final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
		return installer != null && valid.contains(installer);
	}
	public static void alertVerifyGP(final Context context) {
		if (!isVerifyGP(context)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)context));
			builder.setTitle("Google Play");
			builder.setMessage("The application is not installed from Google Play!");
			builder.setCancelable(false);
			builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((Activity)context).finishAffinity();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		}
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int colun_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(colun_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
