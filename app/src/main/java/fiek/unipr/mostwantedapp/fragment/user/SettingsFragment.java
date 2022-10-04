package fiek.unipr.mostwantedapp.fragment.user;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import fiek.unipr.mostwantedapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);


        //we can use everywhere
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext() /* Activity context */);
        String name = sharedPreferences.getString("emri i key", "vlera default");

    }
}