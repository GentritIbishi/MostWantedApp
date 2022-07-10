package fiek.unipr.mostwantedapp.fragment.user;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import fiek.unipr.mostwantedapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}