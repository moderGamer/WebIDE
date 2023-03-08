package com.doctorsteep.ide.web.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.data.Data;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference appScreen = findPreference("appScreen");
		Preference editorScreen = findPreference("editorScreen");
		Preference webScreen = findPreference("webScreen");

		Preference keyTheme = findPreference("keyTheme");
		

    }
}
