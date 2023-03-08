package com.doctorsteep.ide.web.data;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.doctorsteep.ide.web.R;
import java.io.File;
import com.doctorsteep.ide.web.widget.CodeEditor;

public class EditorCodeData {
	
	private static AlertDialog alertDialogReplace;
	private static AlertDialog alertDialogGoToLine;
	
	public static void replaceText(Context context, final CodeEditor editor) {
		AlertDialog.Builder dialogBuilderReplace = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderReplace.setTitle("Replace");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_replace_text, null);
		dialogBuilderReplace.setView(dialogView);

		final EditText edit1 = (EditText) dialogView.findViewById(R.id.edit1);
		final EditText edit2 = (EditText) dialogView.findViewById(R.id.edit2);
		Button btnReplace = (Button) dialogView.findViewById(R.id.btnReplace);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
		
		btnReplace.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(edit1.getText().toString().trim().isEmpty()) {
						edit1.setError("Please enter value");
					} else {
						editor.setReplaceText(edit1.getText().toString(), edit2.getText().toString());
						alertDialogReplace.dismiss();
					}
				}
			});
		btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					alertDialogReplace.cancel();
				}
			});

		alertDialogReplace = dialogBuilderReplace.create();
		alertDialogReplace.show();
	}

	public static void goToLine(Context context, final CodeEditor editor) {
		AlertDialog.Builder dialogBuilderGoToLine = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderGoToLine.setTitle("Go to line");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_go_to_line, null);
		dialogBuilderGoToLine.setView(dialogView);

		final EditText edit1 = (EditText) dialogView.findViewById(R.id.edit1);
		Button btnGo = (Button) dialogView.findViewById(R.id.btnGo);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

		edit1.post(new Runnable() {
			@Override
			public void run() {
				if (editor.getTrueLineCount() == 1) {
					edit1.setHint("Oops... this one line!");
					edit1.setError("Oops... this one line!");
					edit1.setEnabled(false);
					return;
				}
				if (editor.getTrueLineCount() == 2) {
					edit1.setHint("1 or 2");
					return;
				}
				if (editor.getTrueLineCount() >= 3) {
					edit1.setHint("1.." + editor.getTrueLineCount());
					return;
				}
			}
		});

		btnGo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1) {
				if(edit1.getText().toString().trim().isEmpty()) {
					edit1.setError("Please enter value");
				} else {
					editor.goToLine(Integer.valueOf(edit1.getText().toString()));
					alertDialogGoToLine.dismiss();
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1) {
				alertDialogGoToLine.cancel();
			}
		});

		alertDialogGoToLine = dialogBuilderGoToLine.create();
		alertDialogGoToLine.show();
	}
}
