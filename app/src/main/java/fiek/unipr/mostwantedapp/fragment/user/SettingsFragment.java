package fiek.unipr.mostwantedapp.fragment.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import fiek.unipr.mostwantedapp.R;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);


        //we can use everywhere
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext() /* Activity context */);
        String name = sharedPreferences.getString("emri i key", "vlera default");

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.e("preference", "Pending Preference value is: " + newValue);
        //kina mi marr preferencat edhe sa here ka update me i bo save ne db
        return true;
    }

}