package com.doctorsteep.ide.web;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.doctorsteep.ide.web.adapter.ProjectsAdapter;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.data.ManagerData;
import com.doctorsteep.ide.web.ei.AddonsEiActivity;
import com.doctorsteep.ide.web.ei.CreateAddonEiActivity;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.ProjectUtils;
import java.io.File;
import com.doctorsteep.ide.web.data.CheckPermission;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
	
	private FloatingActionButton fab;
	private LinearLayout linearMain;
	private Toolbar mToolbar;
	private int permissionStorage;
	private static TextView textNot;
	private static RecyclerView mRecyclerView;
	private static RecyclerView.Adapter adapter;
	private static RecyclerView.LayoutManager layoutManager;

	private boolean openSettings = false;
	
	private static final int REQUEST_CODE_PERMISSION = 15;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Data.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		fab = findViewById(R.id.fab);
		linearMain = findViewById(R.id.mainLinearLayout);
		textNot = findViewById(R.id.textNot);
		mRecyclerView = findViewById(R.id.recycler_view);
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

//		if (!Data.isVerifyGP(MainActivity.this)) {
//			Data.alertVerifyGP(MainActivity.this);
//		}
		
		fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					ProjectUtils.createNewProject(MainActivity.this);
				}
			});

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.GONE);
                textNot.setVisibility(View.VISIBLE);
            }
        });

		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0 || dy < 0 && fab.isShown()) {
					fab.hide();
				}
			}

			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					fab.show();
				}
				super.onScrollStateChanged(recyclerView, newState);
			}
		});
    }
	@Override
	protected void onStart(){
		super.onStart();
		if(permissionStorage != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//				Intent in = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//				in.addFlags(
//						Intent.FLAG_GRANT_READ_URI_PERMISSION |
//								Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
//								Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
//								Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
//				startActivityForResult(in, REQUEST_CODE_PERMISSION);
				Data.setGrantPermissionsSettings(MainActivity.this);
				return;
			}
			ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
		} else {
			setCreateDirectories();
			setLoadListProjects(this);
		}
		
		if(Data.settingChange) {
			Data.settingChange = false;
			this.recreate();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case REQUEST_CODE_PERMISSION:
				setCreateDirectories();
				setLoadListProjects(this);
				break;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
		//DrawableUtils.setTintDrawable(menu.findItem(R.id.action_new_project).getIcon(), getResources().getColor(R.color.iconColorPrimary));
        return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_addons_ei) { //Дополненич длч EI
            startActivity(new Intent(this, AddonsEiActivity.class));
        } if(item.getItemId() == R.id.action_settings) { //Настройки
            startActivity(new Intent(this, SettingsActivity.class));
        } if(item.getItemId() == R.id.action_file_manager) { //Файловый менеджер
            FileManagerProject.fileManagerProject(this, Environment.getExternalStorageDirectory().getAbsolutePath(), 0);
        }
        return true;
    }
	private void setCreateDirectories() {
		Data.LogCat(this, true);
		ProjectUtils.createNewDirectory(Environment.getExternalStorageDirectory() + File.separator + ManagerData.MAIN_PATH);
		ProjectUtils.createNewDirectory(Environment.getExternalStorageDirectory() + File.separator + ManagerData.MAIN_PATH + File.separator + ManagerData.PROJECTS_NAME);
		ProjectUtils.createNewDirectory(Environment.getExternalStorageDirectory() + File.separator + ManagerData.MAIN_PATH + File.separator + ManagerData.LOG_NAME);
		if(FileUtils.createFile(Environment.getExternalStorageDirectory() + File.separator + ManagerData.MAIN_PATH + File.separator + ManagerData.NOMEDIA_FILE)) {

		}
	}
	public static void setLoadListProjects(final Context contex) {
		try {
		    if (ProjectUtils.listProjects().size() > 0) {
                layoutManager = new LinearLayoutManager(((Activity)contex));
                mRecyclerView.setLayoutManager(layoutManager);
                adapter = new ProjectsAdapter(ProjectUtils.listProjects(), ((Activity)contex));
                mRecyclerView.setAdapter(adapter);
                ((Activity)contex).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        textNot.setVisibility(View.GONE);
                    }
                });
            } else {
                ((Activity)contex).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setVisibility(View.GONE);
                        textNot.setVisibility(View.VISIBLE);
                    }
                });
            }
		} catch (Exception e){}
	}
}
