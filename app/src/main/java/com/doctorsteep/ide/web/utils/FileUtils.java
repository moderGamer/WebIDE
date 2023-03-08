package com.doctorsteep.ide.web.utils;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.widget.Toast;

public class FileUtils {
	
	public static boolean createFile(String patch) {
		try {
			return new File(patch).createNewFile();
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void writeFile(Context context, String text, String patch) {
		if(new File(patch).exists()) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(patch);
				byte[] buffer = text.getBytes();
				fos.write(buffer, 0, buffer.length);
				fos.close();
				Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {}
			}
		} else {
			Toast.makeText(context, "Error saved! No file", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static String readFile(Context context, String patch) {
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(patch)));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			br.close();
		}
		catch (IOException e) {}
		return text.toString();
	}
	
	public static boolean rename(String from, String to) {
		boolean check = false;
		File dir = Environment.getExternalStorageDirectory();
		if(dir.exists()){
			File f = new File(from);
			File t = new File(to);
			if(f.exists())
				check = f.renameTo(t);
		}
		return check;
	}
	
	//19-
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(dst);
			try {
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}
	
	//19+
	public static void copyFile(File src, File dst) throws IOException {
		try (InputStream in = new FileInputStream(src)) {
			try (OutputStream out = new FileOutputStream(dst)) {
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		}
	}
}
