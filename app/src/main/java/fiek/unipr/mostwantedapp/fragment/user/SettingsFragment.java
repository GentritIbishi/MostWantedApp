package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.APPEARANCE_MODE_PREFERENCE;
import static fiek.unipr.mostwantedapp.utils.Constants.DARK_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.LIGHT_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYMENT_INFORMATION;
import static fiek.unipr.mostwantedapp.utils.Constants.SYSTEM_MODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.PaymentInformation;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.UIMessage;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String userID, full_name_payment_information, address_payment_information, bank_account_payment_information, account_number_payment_information,
            paypal_email_payment_information, payment_method;
    private EditTextPreference et_full_name_payment_information,
            et_address_payment_information, et_bank_account_payment_information,
            et_account_number_payment_information,
            et_paypal_email_payment_information;
    private ListPreference list_payment_method;
    private SwitchPreferenceCompat allowNotification, dark_mode, light_mode, system_mode;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_root_preference, rootKey);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();
        mContext = getContext();

        assert mContext != null;
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        full_name_payment_information = sharedPreferences.getString("full_name_payment_information", "");
        address_payment_information = sharedPreferences.getString("address_payment_information","");
        bank_account_payment_information = sharedPreferences.getString("bank_account_payment_information", "");
        account_number_payment_information = sharedPreferences.getString("account_number_payment_information","");
        paypal_email_payment_information = sharedPreferences.getString("paypal_email_payment_information","");
        payment_method = sharedPreferences.getString("payment_method","");

        save(full_name_payment_information, address_payment_information, bank_account_payment_information,
                account_number_payment_information, paypal_email_payment_information, payment_method);

        et_full_name_payment_information = getPreferenceManager().findPreference("full_name_payment_information");
        et_address_payment_information = getPreferenceManager().findPreference("address_payment_information");
        et_bank_account_payment_information = getPreferenceManager().findPreference("bank_account_payment_information");
        et_account_number_payment_information = getPreferenceManager().findPreference("account_number_payment_information");
        et_paypal_email_payment_information = getPreferenceManager().findPreference("paypal_email_payment_information");
        list_payment_method = getPreferenceManager().findPreference("payment_method");
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

        et_full_name_payment_information.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(value, address_payment_information, bank_account_payment_information,
                        account_number_payment_information, paypal_email_payment_information, payment_method);
                return true;
            }
        });

        et_address_payment_information.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(full_name_payment_information, value, bank_account_payment_information,
                        account_number_payment_information, paypal_email_payment_information, payment_method);
                return true;
            }
        });

        et_bank_account_payment_information.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(full_name_payment_information, address_payment_information, value,
                        account_number_payment_information, paypal_email_payment_information, payment_method);
                return true;
            }
        });

        et_account_number_payment_information.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(full_name_payment_information, address_payment_information, bank_account_payment_information,
                        value, paypal_email_payment_information, payment_method);
                return true;
            }
        });

        et_paypal_email_payment_information.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(full_name_payment_information, address_payment_information, bank_account_payment_information,
                        account_number_payment_information, value, payment_method);
                return true;
            }
        });

        list_payment_method.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                save(full_name_payment_information, address_payment_information, bank_account_payment_information,
                        account_number_payment_information, paypal_email_payment_information, value);
                return true;
            }
        });
    }

    private void save(String full_name_payment_information, String address_payment_information, String bank_account_payment_information,
                      String account_number_payment_information, String paypal_email_payment_information, String payment_method)
    {
        PaymentInformation paymentInformation = new PaymentInformation(
                userID,
                full_name_payment_information,
                address_payment_information,
                bank_account_payment_information,
                account_number_payment_information,
                paypal_email_payment_information,
                payment_method,
                DateHelper.getDateTime()
        );
        firebaseFirestore.collection(PAYMENT_INFORMATION)
                .document(userID)
                .set(paymentInformation, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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