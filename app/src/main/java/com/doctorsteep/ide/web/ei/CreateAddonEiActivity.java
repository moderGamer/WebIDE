package com.doctorsteep.ide.web.ei;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.data.ManagerData;
import com.doctorsteep.ide.web.utils.DFUtils;
import com.doctorsteep.ide.web.utils.FileUtils;

import java.io.File;

public class CreateAddonEiActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText editId, editName, editDescription, editIcon, editMainSource, editVersion, editVersionCode;
    private ImageView imagePathIcon, imagePathSource;
    private Button buttonCreate;

    int idRand = 0;
    String nameRand = "NaN";

    private String manifest = "{\n" +
            "  \"id\": \"%id%\",\n" +
            "  \"name\": \"%name%\",\n" +
            "  \"description\": \"%description%\",\n" +
            "  \"icon\": \"%icon%\",\n" +
            "  \"main_source\": \"%source%\",\n" +
            "  \"version\": \"%version%\",\n" +
            "  \"version_code\": %version_code%\n" +
            "}";
    private String manifest_result = manifest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Data.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_addon_ei);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create addon");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        idRand = Data.getRandomNum(1, 99999999);
        nameRand = Data.getRandomString(10);

        editId = findViewById(R.id.editId);
        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        editIcon = findViewById(R.id.editIcon);
        editMainSource = findViewById(R.id.editMainSource);
        editVersion = findViewById(R.id.editVersion);
        editVersionCode = findViewById(R.id.editVersionCode);
        imagePathIcon = findViewById(R.id.imagePathIcon);
        imagePathSource = findViewById(R.id.imagePathSource);
        buttonCreate = findViewById(R.id.buttonCreate);

        createAddon();

        editMainSource.post(new Runnable() {
            @Override
            public void run() {
                editMainSource.setText(File.separator + ManagerData.JS_NAME + File.separator + ManagerData.INDEX_JS_FILE);
                editMainSource.setEnabled(false);
            }
        });
        editId.post(new Runnable() {
            @Override
            public void run() {
                editId.setText(idRand + "");
            }
        });
        editName.post(new Runnable() {
            @Override
            public void run() {
                editName.setText("My addon");
            }
        });
        editVersion.post(new Runnable() {
            @Override
            public void run() {
                editVersion.setText("1.0.0");
            }
        });
        editVersionCode.post(new Runnable() {
            @Override
            public void run() {
                editVersionCode.setText("1");
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createButtonAddon();
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagePathSource.setAlpha(0.5f);
                imagePathSource.setClickable(false);
                imagePathSource.setEnabled(false);
            }
        });

        imagePathIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAddonEiActivity.this, "Choose image and copy path", Toast.LENGTH_SHORT).show();
                FileManagerProject.fileManagerProject(CreateAddonEiActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath(), 0);
            }
        });
        imagePathSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManagerProject.fileManagerProject(CreateAddonEiActivity.this, new File(Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.JS_NAME).getAbsolutePath(), 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        removeAddon();
        super.onDestroy();
    }

    private void createAddon() {
        Data.createFolder(EIData.HOME_PATH);
        Data.createFolder(EIData.HOME_PATH + File.separator + nameRand);
        Data.createFolder(EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.JS_NAME);
        Data.createFolder(EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.ICONS_NAME);
        FileUtils.createFile(Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.JS_NAME + File.separator + ManagerData.INDEX_JS_FILE);
    }
    private void removeAddon() {
        if (!new File(Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.INDEX_MANIFEST_FILE).isFile()) {
            Data.deleteDF(new File(Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH + File.separator + nameRand));
        }
    }
    private void createButtonAddon() {
        if (editId.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "ID not exists!", Toast.LENGTH_SHORT).show();
            editId.post(new Runnable() {
                @Override
                public void run() {
                    editId.setError("ID not exists!");
                }
            });
            return;
        }
        if (editName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Name not exists!", Toast.LENGTH_SHORT).show();
            editName.post(new Runnable() {
                @Override
                public void run() {
                    editName.setError("Name not exists!");
                }
            });
            return;
        }
        if (editMainSource.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Main source not exists!", Toast.LENGTH_SHORT).show();
            editMainSource.post(new Runnable() {
                @Override
                public void run() {
                    editMainSource.setError("Main source not exists!");
                }
            });
            return;
        }
        if (editVersion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Version not exists!", Toast.LENGTH_SHORT).show();
            editVersion.post(new Runnable() {
                @Override
                public void run() {
                    editVersion.setError("Version not exists!");
                }
            });
            return;
        }
        if (editVersionCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Version code not exists!", Toast.LENGTH_SHORT).show();
            editVersionCode.post(new Runnable() {
                @Override
                public void run() {
                    editVersionCode.setError("Version code not exists!");
                }
            });
            return;
        }

        manifest_result = manifest.replace("%id%", editId.getText())
                .replace("%name%", editName.getText())
                .replace("%description%", editDescription.getText())
                .replace("%icon%", editIcon.getText())
                .replace("%source%", editMainSource.getText())
                .replace("%version%", editVersion.getText())
                .replace("%version_code%", editVersionCode.getText());

        String pathM = Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH + File.separator + nameRand + File.separator + ManagerData.INDEX_MANIFEST_FILE;

        if (FileUtils.createFile(pathM)) {
            FileUtils.writeFile(this, manifest_result, pathM);
            Toast.makeText(this, "New addon created!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            Toast.makeText(this, "Failed create default file: \"" + ManagerData.INDEX_MANIFEST_FILE + "\"", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
