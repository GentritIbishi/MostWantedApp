package fiek.unipr.mostwantedapp.fragment.admin.register.user;

import static fiek.unipr.mostwantedapp.utils.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.GRADE_A;
import static fiek.unipr.mostwantedapp.utils.Constants.GRADE_E;
import static fiek.unipr.mostwantedapp.utils.Constants.INFORMER_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.TOTAL_PAID_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.SEMI_ADMIN_ROLE;

import android.content.Context;
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

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.SecurityHelper;
import fiek.unipr.mostwantedapp.utils.SpinnerAdapter;
import fiek.unipr.mostwantedapp.models.User;

public class RegisterUserFragment extends Fragment {

    private Context mContext;
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
        mContext = getContext();
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
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        et_gender_users.setAdapter(gender_adapter);

        et_role_autocomplete = register_users_view.findViewById(R.id.et_role_autocomplete);
        ArrayAdapter<String> role_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        et_role_autocomplete.setAdapter(role_adapter);

        admin_bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        admin_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mContext.getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        admin_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    admin_etNumPersonalLayout.setError(mContext.getText(R.string.no_more_than_ten_digits));
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
        if(role.equals(ADMIN_ROLE) || role.equals(SEMI_ADMIN_ROLE)){
            grade = GRADE_A;
        }else if(role.equals(INFORMER_ROLE)){
            grade = GRADE_E;
        }else if(role.isEmpty()){
            grade = "";
        }
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
        }else if(!email.matches("^[a-z0-9](\\.?[a-z0-9_-])*@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
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

            if(CheckInternet.isConnected(mContext)){
                String finalGrade = grade;

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userID = authResult.getUser().getUid();
                        try {
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
                                    DateHelper.getDateTime(),
                                    finalGrade,
                                    password,
                                    urlOfProfile,
                                    BALANCE_DEFAULT,
                                    TOTAL_PAID_DEFAULT,
                                    isEmailVerified);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        admin_ri_progressBar.setVisibility(View.INVISIBLE);
                        admin_bt_Register.setEnabled(true);
                        Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(mContext, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
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
                              Double balance,
                              Double totalPaid,
                              Boolean isEmailVerified) throws Exception {
        if(CheckInternet.isConnected(mContext)){
            SecurityHelper securityHelper = new SecurityHelper();
            String hashPassword = securityHelper.encrypt(password);
            documentReference = firebaseFirestore.collection(USERS).document(userID);
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
                    hashPassword,
                    urlOfProfile,
                    balance,
                    totalPaid,
                    isEmailVerified
            );
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(mContext, mContext.getText(R.string.this_user_with_this) + " " + fullName + " " + mContext.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(mContext, mContext.getText(R.string.error_no_internet_connection_check_wifi_or_mobile_data), Toast.LENGTH_SHORT).show();
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
        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}