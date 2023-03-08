package com.doctorsteep.ide.web.data;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.MainActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.FileManagerAdapter;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import com.doctorsteep.ide.web.utils.ProjectUtils;
import com.doctorsteep.ide.web.utils.SyntaxUtils;
import java.io.File;
import com.doctorsteep.ide.web.utils.DFUtils;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FileManagerProject {

	public static String patchUpdate = "";
	public static String patchStorage = "";
	public static String copyPath = "";
	private static String hidden = "";
	public static int valueP = 0;
	public static TextView textPatch;
	public static Button btnCancel, btnCreate, btnMain;
	public static LinearLayout linBack;
	public static LinearLayout linOpenProject;
	public static RecyclerView mRecyclerView;
	private static RecyclerView.Adapter adapter;
	private static RecyclerView.LayoutManager layoutManager;

	private static AlertDialog alertDialog;
	private static AlertDialog alertDialogFile;
	private static AlertDialog alertDialogDir;
	private static AlertDialog alertDialogRenameFile;
	
	public static void fileManagerProject(final Context context, final String patch, final int value) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		//dialogBuilder.setTitle("File manager");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_manager_project, null);
		dialogBuilder.setView(dialogView);

		btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
		btnCreate = (Button) dialogView.findViewById(R.id.btnCreate);
		btnMain = (Button) dialogView.findViewById(R.id.btnMain);
		textPatch = (TextView) dialogView.findViewById(R.id.patch_id);
		mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.recycler_view);
		linBack = (LinearLayout) dialogView.findViewById(R.id.linBack);
		linOpenProject = (LinearLayout) dialogView.findViewById(R.id.linOpenProject);
		
		setListLoad(context, patch);
		patchStorage = patch;
		
		valueP = value;
		
		((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					linOpenProject.setVisibility(View.GONE);
				}
			});
		
		if(value == 0) {
			((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						btnMain.setVisibility(View.GONE);
						btnMain.setClickable(false);
						btnMain.setEnabled(false);
					}
				});
		}
		
		if(!copyPath.equals("")) {
			btnCreate.setText("Paste");
			btnCancel.setText("Cancel copy");
		}
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(copyPath.equals("")) {
						alertDialog.cancel();
					} else {
						copyPath = "";
						btnCreate.setText("Create");
						btnCancel.setText("Cancel");
					}
				}
			});
		btnCreate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(copyPath.equals("")) {
						showCreateMenu(context, p1, patchUpdate);
					} else {
						try {
							FileUtils.copyFile(new File(copyPath), new File(patchUpdate));
							copyPath = "";
							btnCreate.setText("Create");
							btnCancel.setText("Cancel");
							Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
							setListLoad(context, patchUpdate);
						} catch (Exception e) {
							Toast.makeText(context, "Error paste - " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		btnMain.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(new File(EditorActivity.patchProject) != null) {
						FileManagerProject.setListLoad(context, EditorActivity.patchProject);
					}
				}
			});
		linBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					/*if(textPatch.getText().toString().contains(EditorActivity.patchProject)) {
						Toast.makeText(context, "Stoped!", Toast.LENGTH_SHORT).show();
					} else {
						
					}*/
					if(new File(textPatch.getText().toString()).getParentFile() != null) {
						FileManagerProject.setListLoad(context, new File(textPatch.getText().toString()).getParentFile().toString());
					}
				}
			});
		linOpenProject.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					ProjectUtils.openProject(context, new File(patchUpdate).getAbsolutePath());
					alertDialog.dismiss();
				}
			});

		alertDialog = dialogBuilder.create();
		alertDialog.show();
	}
	
	public static void setListLoad(Context context, String patch) {
		try {
			layoutManager = new LinearLayoutManager(((Activity)context));
			mRecyclerView.setLayoutManager(layoutManager);
			adapter = new FileManagerAdapter(ProjectUtils.listFiles(patch), ((Activity)context));
			mRecyclerView.setAdapter(adapter);
			textPatch.setText(patch);
			patchUpdate = patch;
			EditorActivity.patchProjectUpdate = patch;
		} catch (Exception e){}
	}
	
	public static void showCreateMenu(final Context context, View view, final String patch) {
        PopupMenu popupMenu = new PopupMenu(((Activity)context), view);
        popupMenu.inflate(R.menu.create_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.act_file:
							createNewFile(context, patch);
							return true;
						case R.id.act_directory:
							createNewDir(context, patch);
							return true;
						default:
							return false;
					}
				}
			});
        popupMenu.show();
    }
	
	
	public static void createNewFile(final Context context, final String patch) {
		AlertDialog.Builder dialogBuilderFile = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderFile.setTitle("Create new file");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_create_file, null);
		dialogBuilderFile.setView(dialogView);

		final EditText editText = dialogView.findViewById(R.id.editName);
		Button btnCreate = dialogView.findViewById(R.id.btnCreate);
		final CheckBox checkHidden = dialogView.findViewById(R.id.checkHidden);
		
		if(hidden.equals(".")) {
			checkHidden.setChecked(true);
		} else {
			checkHidden.setChecked(false);
		}
		
		btnCreate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(editText.getText().toString().trim().isEmpty()) {
						editText.setError("Please enter name file");
					} else {
						if(checkHidden.isChecked()) {
							hidden = ".";
						}
						try {
							if (new File(patch + File.separator + editText.getText().toString()).createNewFile()) {
								Toast.makeText(context, "File created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
								setListLoad(((Activity)context), patch);
								editText.setText("");
								alertDialogFile.dismiss();
								hidden = "";
							} else {
								Toast.makeText(context, "Error file created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {}
					}
				}
			});

		alertDialogFile = dialogBuilderFile.create();
		alertDialogFile.show();
	}
	
	public static void createNewDir(final Context context, final String patch) {
		AlertDialog.Builder dialogBuilderDir = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderDir.setTitle("Create new directory");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_create_directory, null);
		dialogBuilderDir.setView(dialogView);

		final EditText editText = dialogView.findViewById(R.id.editName);
		Button btnCreate = dialogView.findViewById(R.id.btnCreate);
		final CheckBox checkHidden = dialogView.findViewById(R.id.checkHidden);

		if(hidden.equals(".")) {
			checkHidden.setChecked(true);
		} else {
			checkHidden.setChecked(false);
		}
		
		btnCreate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(editText.getText().toString().trim().isEmpty()) {
						editText.setError("Please enter name directory");
					} else {
						if(checkHidden.isChecked()) {
							hidden = ".";
						}
						try {
							if (ProjectUtils.createNewDirectory(patch + File.separator + hidden + editText.getText().toString())) {
								Toast.makeText(context, "Directory created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
								setListLoad(((Activity)context), patch);
								editText.setText("");
								alertDialogDir.dismiss();
								hidden = "";
							} else {
								Toast.makeText(context, "Error directory created - " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
						}
					}
				}
			});

		alertDialogDir = dialogBuilderDir.create();
		alertDialogDir.show();
	}
	
	public static void rename(final Context context, final String patch) {
		AlertDialog.Builder dialogBuilderRenameFile = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderRenameFile.setTitle("Rename");
		dialogBuilderRenameFile.setMessage(new File(patch).getAbsolutePath());
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_rename_file, null);
		dialogBuilderRenameFile.setView(dialogView);

		final EditText editText = (EditText) dialogView.findViewById(R.id.editName);
		Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
		editText.setText(new File(patch).getName());

		btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(editText.getText().toString().trim().isEmpty()) {
						editText.setError("Please enter name");
					} else {
						if(FileUtils.rename(patch, new File(patch).getParentFile() + File.separator + editText.getText().toString())) {
							Toast.makeText(context, "Renamed!", Toast.LENGTH_SHORT).show();
							setListLoad(context, EditorActivity.patchProjectUpdate);
							alertDialogRenameFile.cancel();
						} else {
							Toast.makeText(context, "Error renamed", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					alertDialogRenameFile.dismiss();
				}
			});
			
			

		alertDialogRenameFile = dialogBuilderRenameFile.create();
		alertDialogRenameFile.show();
	}
	
	public static void showFileDirMenu(final Context context, View view, final String patch) {
        PopupMenu popupMenu = new PopupMenu(((Activity)context), view);
        popupMenu.inflate(R.menu.file_dir_menu);
		
		if(popupMenu.getMenu().getItem(0) != null) {
			if(new File(patch).isFile()) {
				popupMenu.getMenu().getItem(0).setTitle("Copy file");
			} if(new File(patch).isDirectory()) {
				popupMenu.getMenu().getItem(0).setTitle("Copy directory");
			}
			
			if(!copyPath.equals("")) {
				popupMenu.getMenu().getItem(0).setEnabled(false);
			}
		}

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.act_delete:
							deleteFD(context, patch);
							return true;
						case R.id.act_rename:
							if(new File(patch).isDirectory()) {
								if(EditorActivity.patchProject.startsWith(patch)) {
									Toast.makeText(context, "Error renamed! Renaming is allowed inside the project!", Toast.LENGTH_SHORT).show();
								} else {
									rename(context, patch);
								}
							} if(new File(patch).isFile()) {
								if(EditorActivity.patchFile.equals(patch)) {
									Toast.makeText(context, "Error renamed! Please close opening file", Toast.LENGTH_SHORT).show();
								} else {
									rename(context, patch);
								}
							}
							return true;
						case R.id.act_copy_path:
							((ClipboardManager)((Activity)context).getSystemService(((Activity)context).CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", patch));
							Toast.makeText(context, "Path clipboard!", Toast.LENGTH_SHORT).show();
							return true;
						case R.id.act_copy:
							if(new File(patch).isFile()) {
								//Toast.makeText(context, "File copied!", Toast.LENGTH_SHORT).show();
							} if(new File(patch).isDirectory()) {
								//Toast.makeText(context, "Directory copied!", Toast.LENGTH_SHORT).show();
							}
							copyPath = patch;
							btnCreate.setText("Paste");
							btnCancel.setText("Cancel copy");
							return true;
						default:
							return false;
					}
				}
			});
        popupMenu.show();
    }
	
	
	public static void openFile(Context context, String name, String path) {
		try {
			if(name.endsWith(".php")
					|| name.endsWith(".htm")
					|| name.endsWith(".html")
					|| name.endsWith(".json")
					|| name.endsWith(".css")
					|| name.endsWith(".scss")
					|| name.endsWith(".js")
					|| name.endsWith(".txt")
					|| name.endsWith(".aif")
					|| name.endsWith(".xml")
					|| name.endsWith(".java")
					|| name.endsWith(".javascript")
					|| name.endsWith(".logcat"))
			{
				EditorActivity.codeEditor.type = LangSyntax.NONE;
				EditorActivity.setUpdateSymbol(SyntaxUtils.ALL_symbol(), context);
				if(name.endsWith(".html")
						|| name.endsWith(".htm"))
				{
					EditorActivity.codeEditor.type = LangSyntax.HTML;
					EditorActivity.setUpdateSymbol(SyntaxUtils.HTML_symbol(), context);
				} if(name.endsWith(".php")) {
					EditorActivity.codeEditor.type = LangSyntax.PHP;
					EditorActivity.setUpdateSymbol(SyntaxUtils.PHP_symbol(), context);
				} if(name.endsWith(".css")
						|| name.endsWith("scss"))
				{
					EditorActivity.codeEditor.type = LangSyntax.CSS;
					EditorActivity.setUpdateSymbol(SyntaxUtils.CSS_symbol(), context);
				} if(name.endsWith(".js")
						|| name.endsWith(".javascript"))
				{
					EditorActivity.codeEditor.type = LangSyntax.JAVASCRIPT;
					EditorActivity.setUpdateSymbol(SyntaxUtils.JS_symbol(), context);
				}
				EditorActivity.setOpenFile(context, path);
				EditorActivity.setAddHistoryFiles(path, ((Activity)context));
			} else {
				Toast.makeText(context, "Not support type opening file", Toast.LENGTH_SHORT).show();
			}
		} catch(Exception e) {
			Toast.makeText(context, "Not support type opening file - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		/*try {
			if(valueP == 0) {
				DFUtils.openFile(context, path, DFUtils.getMimeType(new File(path)));
			} else {
				if(DFUtils.getMimeType(new File(path)).contains("text/")) {
					
				} else {
					Toast.makeText(context, "Not support type opening file \"" + DFUtils.getMimeType(new File(path)) + "\"", Toast.LENGTH_SHORT).show();
				}
			}
		} catch(Exception e) {
			Toast.makeText(context, "Error opening file - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}*/
	}

	public static void openFileDefault(Context context, String path) {
		try {
			if(path.endsWith(".php")
					|| path.endsWith(".htm")
					|| path.endsWith(".html")
					|| path.endsWith(".json")
					|| path.endsWith(".css")
					|| path.endsWith(".scss")
					|| path.endsWith(".js")
					|| path.endsWith(".txt")
					|| path.endsWith(".aif")
					|| path.endsWith(".xml")
					|| path.endsWith(".java")
					|| path.endsWith(".javascript")
					|| path.endsWith(".logcat"))
			{
				((Activity)context).startActivity(new Intent(((Activity)context), EditorActivity.class).putExtra("path", path).putExtra("type", "file"));
			} else {
				Toast.makeText(context, "Not support type opening file", Toast.LENGTH_SHORT).show();
			}
		} catch(Exception e) {
			Toast.makeText(context, "Not support type opening file - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		/*try {
			if(valueP == 0) {
				DFUtils.openFile(context, path, DFUtils.getMimeType(new File(path)));
			} else {
				if(DFUtils.getMimeType(new File(path)).contains("text/")) {

				} else {
					Toast.makeText(context, "Not support type opening file \"" + DFUtils.getMimeType(new File(path)) + "\"", Toast.LENGTH_SHORT).show();
				}
			}
		} catch(Exception e) {
			Toast.makeText(context, "Error opening file - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}*/
	}
	
	
	public static void deleteFD(final Context context, final String patch) {
		AlertDialog.Builder builderFD = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		builderFD.setTitle("Delete")
			.setMessage("Are you sure you want to delete \"" + new File(patch).getName() + "\"?")
			.setCancelable(true)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(new File(patch).isDirectory()) {
						if(ProjectUtils.deleteDirectory(new File(patch))) {
							Toast.makeText(context, "Directory deleted!", Toast.LENGTH_SHORT).show();
							setListLoad(context, patchUpdate);
						} else {
							Toast.makeText(context, "Fail deleted!", Toast.LENGTH_SHORT).show();
						}
					} if(new File(patch).isFile()) {
						if(new File(patch).delete()) {
							Toast.makeText(context, "File deleted!", Toast.LENGTH_SHORT).show();
							setListLoad(context, patchUpdate);
						} else {
							Toast.makeText(context, "Fail deleted!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		AlertDialog alertFD = builderFD.create();
		alertFD.show();
	}
}
