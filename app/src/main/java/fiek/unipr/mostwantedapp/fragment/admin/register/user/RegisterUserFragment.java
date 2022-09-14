package fiek.unipr.mostwantedapp.fragment.admin.register.user;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.register.person.SetProfilePersonFragment;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.helpers.SpinnerAdapter;
import fiek.unipr.mostwantedapp.models.User;

public class RegisterUserFragment extends Fragment {

    private View register_users_view;
    private TextInputEditText admin_etName, admin_etLastName, admin_etParentName, admin_etPhone, admin_etAddress, admin_etNumPersonal,
            admin_etEmailToUser, admin_etPasswordToUser, admin_etConfirmPassword;
    private TextInputLayout admin_etNumPersonalLayout;
    private Button admin_bt_Register;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private ProgressBar admin_ri_progressBar;
    private SpinnerAdapter spAdapter;
    private MaterialAutoCompleteTextView et_role_autocomplete, et_gender_users;

    public RegisterUserFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        register_users_view = inflater.inflate(R.layout.fragment_register_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        admin_etName = register_users_view.findViewById(R.id.admin_etName);
        admin_etLastName = register_users_view.findViewById(R.id.admin_etLastName);
        admin_etParentName = register_users_view.findViewById(R.id.admin_etParentName);
        admin_etPhone = register_users_view.findViewById(R.id.admin_etPhone);
        admin_etAddress = register_users_view.findViewById(R.id.admin_etAddress);
        admin_etNumPersonal = register_users_view.findViewById(R.id.admin_etNumPersonal);
        admin_etNumPersonalLayout = register_users_view.findViewById(R.id.admin_etNumPersonalLayout);
        admin_etEmailToUser = register_users_view.findViewById(R.id.admin_etEmailToUser);
        admin_etPasswordToUser = register_users_view.findViewById(R.id.admin_etPasswordToUser);
        admin_etConfirmPassword = register_users_view.findViewById(R.id.admin_etConfirmPassword);
        admin_ri_progressBar = register_users_view.findViewById(R.id.admin_ri_progressBar);
        admin_bt_Register = register_users_view.findViewById(R.id.admin_bt_Register);

        et_gender_users = register_users_view.findViewById(R.id.et_gender_users);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        et_gender_users.setAdapter(gender_adapter);

        et_role_autocomplete = register_users_view.findViewById(R.id.et_role_autocomplete);
        ArrayAdapter<String> role_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        et_role_autocomplete.setAdapter(role_adapter);

        admin_bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        admin_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getContext().getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        admin_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    admin_etNumPersonalLayout.setError(getContext().getText(R.string.no_more_than_ten_digits));
                }else if(charSequence.length() < 10) {
                    admin_etNumPersonalLayout.setError(null);
                }else if(charSequence.length() == 10){
                    admin_etNumPersonalLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return register_users_view;
    }

    private void register() {
        String name = admin_etName.getText().toString().trim();
        String lastName = admin_etLastName.getText().toString().trim();
        String fullName = name + " "+lastName;
        String phone = admin_etPhone.getText().toString().trim();
        String address = admin_etAddress.getText().toString().trim();
        String personal_number = admin_etNumPersonal.getText().toString().trim();
        String parentName = admin_etParentName.getText().toString().trim();
        String gender = et_gender_users.getText().toString().trim();
        String email = admin_etEmailToUser.getText().toString().trim();
        String password = admin_etPasswordToUser.getText().toString();
        String confirm_password = admin_etConfirmPassword.getText().toString();
        String role = et_role_autocomplete.getText().toString();
        String grade = "";
        if(role.equals("Admin") || role.equals("User")){
            grade = "A";
        }else if(role.equals("Informer")){
            grade = "E";
        }else if(role.isEmpty()){
            grade = "";
        }
        String balance = "0 EURO";
        String coins = "0 COINS";
        String urlOfProfile = "";
        Boolean isEmailVerified = false;

        if(TextUtils.isEmpty(fullName)){
            admin_etName.setError(getText(R.string.error_fullname_required));
            admin_etName.requestFocus();
        } else if(TextUtils.isEmpty(personal_number)){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_required));
            admin_etNumPersonal.requestFocus();
        }else if(personal_number.length()>10){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
            admin_etNumPersonal.requestFocus();
        }else if(personal_number.length()<10){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
            admin_etNumPersonal.requestFocus();
        }else if(TextUtils.isEmpty(phone)){
            admin_etPhone.setError(getText(R.string.error_phone_required));
            admin_etPhone.requestFocus();
        }else if(TextUtils.isEmpty(address)){
            admin_etAddress.setError(getText(R.string.error_address_required));
            admin_etAddress.requestFocus();
        }else if(TextUtils.isEmpty(email)){
            admin_etEmailToUser.setError(getText(R.string.error_email_required));
            admin_etEmailToUser.requestFocus();
        }else if(!email.matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
            admin_etEmailToUser.setError(getText(R.string.error_validate_email));
            admin_etEmailToUser.requestFocus();
        }else if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
            admin_etPasswordToUser.setError(getText(R.string.error_password_weak));
            admin_etPasswordToUser.requestFocus();
        } else if(TextUtils.isEmpty(password)){
            admin_etPasswordToUser.setError(getText(R.string.error_password_required));
            admin_etPasswordToUser.requestFocus();
        }else if(TextUtils.isEmpty(confirm_password)){
            admin_etConfirmPassword.setError(getText(R.string.error_confirm_password_required));
            admin_etConfirmPassword.requestFocus();
        }else if(!password.matches(confirm_password)){
            admin_etPasswordToUser.setError(getText(R.string.error_password_not_same));
            admin_etPasswordToUser.requestFocus();
        }else if (TextUtils.isEmpty(gender)) {
            et_gender_users.setError(getText(R.string.error_gender_required));
            et_gender_users.requestFocus();
            return;
        }else {

            admin_ri_progressBar.setVisibility(View.VISIBLE);
            admin_bt_Register.setEnabled(false);

            if(checkConnection()){
                String finalGrade = grade;

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userID = authResult.getUser().getUid();
                        registerUser(userID,
                                name,
                                lastName,
                                fullName,
                                address,
                                email,
                                parentName,
                                gender,
                                role,
                                phone,
                                personal_number,
                                getTimeDate(),
                                finalGrade,
                                password,
                                urlOfProfile,
                                balance,
                                coins,
                                isEmailVerified);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        admin_ri_progressBar.setVisibility(View.INVISIBLE);
                        admin_bt_Register.setEnabled(true);
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void registerUser(String userID,
                              String name,
                              String lastname,
                              String fullName,
                              String address,
                              String email,
                              String parentName,
                              String gender,
                              String role,
                              String phone,
                              String personal_number,
                              String register_date_time,
                              String grade,
                              String password,
                              String urlOfProfile,
                              String balance,
                              String coins,
                              Boolean isEmailVerified) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(userID);
            User user = new User(
                    userID,
                    name,
                    lastname,
                    fullName,
                    address,
                    email,
                    parentName,
                    gender,
                    role,
                    phone,
                    personal_number,
                    register_date_time,
                    grade,
                    password,
                    urlOfProfile,
                    balance,
                    coins,
                    isEmailVerified
            );
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), getContext().getText(R.string.this_user_with_this) + " " + fullName + " " + getContext().getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                    Bundle viewBundle = new Bundle();
                    viewBundle.putString("userID", userID);
                    viewBundle.putString("fullName", fullName);
                    viewBundle.putString("role", role);
                    SetProfileUserFragment setProfileUserFragment = new SetProfileUserFragment();
                    setProfileUserFragment.setArguments(viewBundle);
                    loadFragment(setProfileUserFragment);
                    admin_ri_progressBar.setVisibility(View.INVISIBLE);
                    admin_bt_Register.setEnabled(true);
                    setEmptyFields();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    admin_ri_progressBar.setVisibility(View.INVISIBLE);
                    admin_bt_Register.setEnabled(true);
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(getContext(), getContext().getText(R.string.error_no_internet_connection_check_wifi_or_mobile_data), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(getContext())){
            return false;
        }else {
            return true;
        }
    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    public void setEmptyFields() {
        admin_etName.setText("");
        admin_etLastName.setText("");
        admin_etParentName.setText("");
        admin_etAddress.setText("");
        admin_etNumPersonal.setText("");
        admin_etEmailToUser.setText("");
        admin_etPasswordToUser.setText("");
        admin_etConfirmPassword.setText("");
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}