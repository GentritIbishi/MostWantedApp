package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.APPEARANCE_MODE_PREFERENCE;
import static fiek.unipr.mostwantedapp.utils.Constants.DARK_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.LIGHT_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.SYSTEM_MODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.UIMessage;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SwitchPreferenceCompat allowNotification, dark_mode, light_mode, system_mode;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.admin_root_preference, rootKey);
        mContext = getContext();

        assert mContext != null;
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        allowNotification = getPreferenceManager().findPreference("allowNotification");
        dark_mode = getPreferenceManager().findPreference("dark_mode");
        light_mode = getPreferenceManager().findPreference("light_mode");
        system_mode = getPreferenceManager().findPreference("system_mode");

        allowNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //handle preference on switch compat
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean isChecked = Boolean.parseBoolean(newValue.toString());

                editor.putBoolean("allowNotification", isChecked);
                editor.apply();

                return true;
            }
        });

        system_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Boolean isChecked = Boolean.parseBoolean(newValue.toString());

                if(isChecked)
                {
                    dark_mode.setChecked(false);
                    light_mode.setChecked(false);
                    setModePreference(mContext, SYSTEM_MODE);
                    //detect night mode or light mode
                    int nightModeFlags =
                            getContext().getResources().getConfiguration().uiMode &
                                    Configuration.UI_MODE_NIGHT_MASK;
                    switch (nightModeFlags) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;

                        case Configuration.UI_MODE_NIGHT_NO:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                    }
                }else {
                    if(!dark_mode.isChecked() && !light_mode.isChecked())
                    {
                        system_mode.setChecked(true);
                        UIMessage.showMessage(mContext,
                                mContext.getText(R.string.please_select_one_mode),
                                mContext.getText(R.string.you_need_to_enable_one_mode_before_you_change));
                        return false;
                    }
                }
                return true;
            }
        });

        dark_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Boolean isChecked = Boolean.parseBoolean(newValue.toString());

                if(isChecked)
                {
                    setModePreference(mContext, DARK_MODE);
                    system_mode.setChecked(false);
                    light_mode.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    system_mode.setChecked(true);
                }

                return true;
            }
        });

        light_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Boolean isChecked = Boolean.parseBoolean(newValue.toString());

                if(isChecked)
                {
                    setModePreference(mContext, LIGHT_MODE);
                    system_mode.setChecked(false);
                    dark_mode.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else {
                    system_mode.setChecked(true);
                }

                return true;
            }
        });

    }

    private void setModePreference(Context context, String mode) {
        //handle preference on switch compat
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APPEARANCE_MODE_PREFERENCE, mode);
        editor.apply();
    }

}