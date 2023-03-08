package com.doctorsteep.ide.web.ei;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.MainActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.AddonsEiAdapter;
import com.doctorsteep.ide.web.adapter.ProjectsAdapter;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.ei.utils.AddonUtils;
import com.doctorsteep.ide.web.utils.ProjectUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddonsEiActivity extends AppCompatActivity {

    private int permissionStorage;

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private static TextView textNot;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;

    private static final int REQUEST_CODE_PERMISSION = 15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Data.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addons_ei);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        textNot = findViewById(R.id.textNot);
        mRecyclerView = findViewById(R.id.recycler_view);

        permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Addons for EI");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                startActivity(new Intent(getApplicationContext(), CreateAddonEiActivity.class));
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
    protected void onStart() {
        super.onStart();
        if(permissionStorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				Data.setGrantPermissionsSettings(AddonsEiActivity.this);
                return;
            }
            ActivityCompat.requestPermissions(AddonsEiActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            setLoadListAddons(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE_PERMISSION:
                setLoadListAddons(this);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void setLoadListAddons(final Context contex) {
        try {
            if (AddonUtils.listAddons().size() > 0) {
                layoutManager = new LinearLayoutManager(((Activity)contex));
                mRecyclerView.setLayoutManager(layoutManager);
                adapter = new AddonsEiAdapter(AddonUtils.listAddons(), ((Activity)contex));
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
