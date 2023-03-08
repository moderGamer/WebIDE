package com.doctorsteep.ide.web;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
	
	private Toolbar toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Data.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("Settings");
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.contentSettings, new SettingsFragment()).commit();
		}
	}
}
