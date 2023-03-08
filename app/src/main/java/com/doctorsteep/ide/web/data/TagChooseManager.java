package com.doctorsteep.ide.web.data;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.TagChooseAdapter;
import com.doctorsteep.ide.web.language.LanguageHTML;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TagChooseManager {
	
	public static TextView tMessage;
	
	public static RecyclerView mRecyclerView;
	private static RecyclerView.Adapter adapter;
	private static RecyclerView.LayoutManager layoutManager;

	private static AlertDialog alertDialogTag;

	public static void attrChoose(Context context, String tag) {
		AlertDialog.Builder dialogBuilderTag = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderTag.setTitle("Choosed tag " + "\"" + tag + "\"");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_add_tag, null);
		dialogBuilderTag.setView(dialogView);

		mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.recycler_view);
		tMessage = (TextView) dialogView.findViewById(R.id.message_id);
		Button btnAdd = (Button) dialogView.findViewById(R.id.btnAdd);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

		try {
			layoutManager = new LinearLayoutManager(((Activity)context));
			mRecyclerView.setLayoutManager(layoutManager);
			adapter = new TagChooseAdapter(LanguageHTML.htmlAttr(), ((Activity)context));
			mRecyclerView.setAdapter(adapter);
		} catch (Exception e){}

		btnAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					
				}
			});
		btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					alertDialogTag.cancel();
				}
			});

		alertDialogTag = dialogBuilderTag.create();
		alertDialogTag.show();
	}
}
