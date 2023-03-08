package com.doctorsteep.ide.web.data;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.TagAdapter;
import java.util.ArrayList;
import com.doctorsteep.ide.web.language.LanguageHTML;

public class TagManager {
	
	public static String urlTagInfo = "http://htmlbook.ru/html/";
	
	public static RecyclerView mRecyclerView;
	private static RecyclerView.Adapter adapter;
	private static RecyclerView.LayoutManager layoutManager;
	
	public static AlertDialog alertDialogTag;
	
	public static void openTagList(Context context) {
		AlertDialog.Builder dialogBuilderTag = new AlertDialog.Builder(((Activity)context), Data.setThemeDialog(context));
		dialogBuilderTag.setTitle("Tags");
		dialogBuilderTag.setMessage("Here you can find out all about the tags that are supported...");
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_tag_manager, null);
		dialogBuilderTag.setView(dialogView);

		mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.recycler_view);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

		try {
			layoutManager = new LinearLayoutManager(((Activity)context));
			mRecyclerView.setLayoutManager(layoutManager);
			adapter = new TagAdapter(LanguageHTML.htmlTag(), ((Activity)context));
			mRecyclerView.setAdapter(adapter);
		} catch (Exception e){}
		
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
