package com.doctorsteep.ide.web.data.file;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.data.FileManagerProject;

public class OpenCustomFileActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private Uri url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getIntent().getData();

        dialog = new ProgressDialog(this, Data.setThemeDialog(this));
        dialog.setCancelable(false);
        dialog.setTitle("Please wait...");
        dialog.setMessage(url.toString());
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.setMessage("Opening...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (url != null) {
                    dialog.cancel();
                    finish();
                    FileManagerProject.openFileDefault(OpenCustomFileActivity.this, Data.getRealPathFromURI(getApplicationContext(), url));
                }
            }
        }, 1000);
    }
}
