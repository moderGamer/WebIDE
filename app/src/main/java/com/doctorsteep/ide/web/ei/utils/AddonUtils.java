package com.doctorsteep.ide.web.ei.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.ei.EIData;
import com.doctorsteep.ide.web.model.AddonsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AddonUtils {

    public static boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void openAddon(Context context, String path) throws Exception {
        if (new File(path).isDirectory()) {
            if (new File(path + File.separator + "manifest.json").isFile()) {
                final String manifest = read(new File(path + File.separator + "manifest.json"));
                if (isJSONValid(manifest)) {
                    JSONObject obj = new JSONObject(manifest);
                    if (obj.getString("main_source").endsWith(".js") || obj.getString("main_source").endsWith(".javascript")) {
                        if (new File(path + File.separator + obj.getString("main_source")).isFile()) {
                            ((Activity)context).startActivity(new Intent(((Activity)context), EditorActivity.class).putExtra("path", path + File.separator + obj.getString("main_source")).putExtra("type", "addon"));
                        } else {
                            Toast.makeText(context, "Item \"main_source\" not exists file!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Item \"main_source\" not exists .js or .javascript file!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error reading/syntax manifest file!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No manifest file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Could not open addon", Toast.LENGTH_SHORT).show();
        }
    }

    public static String read(File path) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
            return text.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<AddonsModel> listAddons() throws Exception{
        List<AddonsModel> addonsList = new ArrayList<>();
        for (File addons : new File(Environment.getExternalStorageDirectory() + File.separator + EIData.HOME_PATH).listFiles()) {
            if (addons.isDirectory()) {
                if (new File(addons.getAbsolutePath() + File.separator + "manifest.json").exists()) {
                    final String manifest = read(new File(addons.getAbsolutePath() + File.separator + "manifest.json"));
                    if (isJSONValid(manifest)) {
                        JSONObject obj = new JSONObject(manifest);
                        addonsList.add(new AddonsModel(obj.getString("id"), obj.getString("name"), obj.getString("description"), addons.getAbsolutePath(), obj.getString("icon"), obj.getString("main_source"), obj.getString("version"), obj.getInt("version_code"),  manifest, addons.lastModified()));
                    }
                }
            }} return addonsList;
    }
}
