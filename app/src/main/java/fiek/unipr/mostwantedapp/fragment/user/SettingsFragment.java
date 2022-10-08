package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.PAYMENT_INFORMATION;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonListFragment;
import fiek.unipr.mostwantedapp.models.PaymentInformation;
import fiek.unipr.mostwantedapp.utils.DateHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());

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

}