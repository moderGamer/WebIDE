package com.doctorsteep.ide.web.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.MainActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.data.ManagerData;

import java.io.File;
import java.util.ArrayList;

public class ProjectUtils {
	
	public static String pathProjects = Environment.getExternalStorageDirectory() + File.separator + ManagerData.MAIN_PATH + File.separator + "projects";

	private static AlertDialog alertDialog;
	
	public static void createNewProject(final Context context) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilder.setTitle("Create new project");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_create_project, null);
		dialogBuilder.setView(dialogView);
		
		final EditText editText = (EditText) dialogView.findViewById(R.id.editName);
		Button btnCreate = (Button) dialogView.findViewById(R.id.btnCreate);
		
		btnCreate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(editText.getText().toString().trim().isEmpty()) {
						editText.setError("Please enter name");
					} else {
						if(createNewDirectory(pathProjects + File.separator + editText.getText().toString())) {
							if (FileUtils.createFile(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.INDEX_FILE)) {
								// FileUtils.writeFile(context, CodeExample.HTML_MAIN, editText.getText().toString() + File.separator + ManagerData.INDEX_FILE);
							} if(createNewDirectory(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.ASSETS_NAME)) {
								if(createNewDirectory(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.ASSETS_NAME + File.separator + ManagerData.CSS_NAME)) {
									FileUtils.createFile(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.ASSETS_NAME + File.separator + ManagerData.CSS_NAME + File.separator + ManagerData.INDEX_CSS_FILE);
								} if(createNewDirectory(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.ASSETS_NAME + File.separator + ManagerData.JS_NAME)) {
									FileUtils.createFile(pathProjects + File.separator + editText.getText().toString() + File.separator + ManagerData.ASSETS_NAME + File.separator + ManagerData.JS_NAME + File.separator + ManagerData.INDEX_JS_FILE);
								}
							}

							Toast.makeText(context, "Project created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
							MainActivity.setLoadListProjects(((Activity)context));
							editText.setText("");
							alertDialog.dismiss();
						} else {
							Toast.makeText(context, "Error project created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		
		alertDialog = dialogBuilder.create();
		alertDialog.show();
	}
	
	public static void showProjectMenu(final Context context, View view, final String patch) {
        PopupMenu popupMenu = new PopupMenu(((Activity)context), view);
        popupMenu.inflate(R.menu.project_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.act_details:
							try {
								projectDetails(context, patch);
							} catch (Exception e) {}
							return true;
						case R.id.act_delete:
							deleteProject(context, patch);
							return true;
						default:
							return false;
					}
				}
			});
        popupMenu.show();
    }
	
	public static void projectDetails(Context context, String patch) throws Exception {
		String type = "Unknown";
		if(new File(patch + File.separator + ManagerData.INDEX_FILE).exists()) {
			type = "Project";
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		builder.setTitle("Details")
			.setMessage(
			"Name: " + new File(patch).getName() +
			"\nType: " + type +
			"\nSize: " + DFUtils.sizeFormat(DFUtils.folderSize(new File(patch))) +
			"\nLast update: " + DFUtils.dateDF(patch)
			)
			.setCancelable(true)
			.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void deleteProject(final Context context, final String patch) {
		AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		builder.setTitle("Delete")
			.setMessage("Are you sure you want to delete this project \"" + new File(patch).getName() + "\"?")
			.setCancelable(true)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(deleteDirectory(new File(patch))) {
						Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
						MainActivity.setLoadListProjects(context);
						dialog.cancel();
					} else {
						Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDirectory(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	public static void openProject(Context context, String path) {
		if(new File(path + File.separator + ManagerData.INDEX_FILE).isFile()) {
			((Activity)context).startActivity(new Intent(((Activity)context), EditorActivity.class).putExtra("path", path + File.separator + ManagerData.INDEX_FILE).putExtra("type", "project"));
		} else {
			Toast.makeText(context, "Could not open project", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static ArrayList<String> listProjects() throws Exception{
        ArrayList<String> projectsList = new ArrayList<String>();  
        for (File project : new File(pathProjects).listFiles()) {
            if (project.isDirectory()) {
                projectsList.add(project.getName());
            }} return projectsList;
    }
	
	public static ArrayList<String> listFiles(String patch) throws Exception{
        ArrayList<String> filesList = new ArrayList<String>();  
        for (File project : new File(patch).listFiles()) {
        	filesList.add(project.getAbsolutePath());
		} return filesList;
    }
	
	public static boolean createNewDirectory(String name) {
		boolean check = false;
		
		try {
			File directory = new File(name);
			if (!directory.exists()) {
				directory.mkdirs(); 
				check = true;
			}
		} catch(Exception e) {}
		return check;
	}
}
