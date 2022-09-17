package fiek.unipr.mostwantedapp;

import static fiek.unipr.mostwantedapp.helpers.Constants.DATE_TIME;
import static fiek.unipr.mostwantedapp.helpers.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.models.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etLastName, etParentName, etPhone, etAddress, etPersonalNumber,
            etEmailToInformer, etPasswordToInformer, etConfirmPassword;
    private MaterialAutoCompleteTextView et_gender_user;
    private TextInputLayout etPersonalNumberLayout;
    private Button bt_Register;
    private TextView tv_alreadyHaveAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private ProgressBar ri_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_informer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etParentName = findViewById(R.id.etParentName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPersonalNumber = findViewById(R.id.etNumPersonal);
        etPersonalNumberLayout = findViewById(R.id.etPersonalNumberLayout);
        etEmailToInformer = findViewById(R.id.etEmailToInformer);
        etPasswordToInformer = findViewById(R.id.etPasswordToInformer);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ri_progressBar = findViewById(R.id.ri_progressBar);

        et_gender_user = findViewById(R.id.et_gender_user);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        et_gender_user.setAdapter(gender_adapter);

        bt_Register = findViewById(R.id.bt_Register);
        tv_alreadyHaveAccount = findViewById(R.id.tv_alreadyHaveAccount);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String fullName = name + " "+lastName;
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String personal_number = etPersonalNumber.getText().toString().trim();
                String gender = et_gender_user.getText().toString().trim();
                String parentName = etParentName.getText().toString().trim();
                String email = etEmailToInformer.getText().toString().trim();
                String password = etPasswordToInformer.getText().toString();
                String confirm_password = etConfirmPassword.getText().toString();
                String grade = "E";
                String role = "Informer";
                String balance = "0 EURO";
                String coins = "0 COINS";
                String urlOfProfile = null;
                Boolean isEmailVerified = false;

                if(TextUtils.isEmpty(fullName)){
                    etName.setError(getText(R.string.error_fullname_required));
                    etName.requestFocus();
                } else if(TextUtils.isEmpty(personal_number)){
                    etPersonalNumber.setError(getText(R.string.error_number_personal_required));
                    etPersonalNumber.requestFocus();
                }else if(personal_number.length()>10){
                    etPersonalNumber.setError(getText(R.string.error_number_personal_is_ten_digit));
                    etPersonalNumber.requestFocus();
                }else if(personal_number.length()<10){
                    etPersonalNumber.setError(getText(R.string.error_number_personal_less_than_ten_digits));
                    etPersonalNumber.requestFocus();
                }else if(TextUtils.isEmpty(phone)){
                    etPhone.setError(getText(R.string.error_phone_required));
                    etPhone.requestFocus();
                }else if(TextUtils.isEmpty(address)){
                    etAddress.setError(getText(R.string.error_address_required));
                    etAddress.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    etEmailToInformer.setError(getText(R.string.error_email_required));
                    etEmailToInformer.requestFocus();
                }else if(!email.matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
                    etEmailToInformer.setError(getText(R.string.error_validate_email));
                    etEmailToInformer.requestFocus();
                }else if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
                    etPasswordToInformer.setError(getText(R.string.error_password_weak));
                    etPasswordToInformer.requestFocus();
                } else if(TextUtils.isEmpty(password)){
                    etPasswordToInformer.setError(getText(R.string.error_password_required));
                    etPasswordToInformer.requestFocus();
                }else if(TextUtils.isEmpty(confirm_password)){
                    etConfirmPassword.setError(getText(R.string.error_confirm_password_required));
                    etConfirmPassword.requestFocus();
                }else if(!password.matches(confirm_password)){
                    etPasswordToInformer.setError(getText(R.string.error_password_not_same));
                    etPasswordToInformer.requestFocus();
                }else if (TextUtils.isEmpty(gender)) {
                    et_gender_user.setError(getText(R.string.error_gender_required));
                    et_gender_user.requestFocus();
                    return;
                }else {

                    ri_progressBar.setVisibility(View.VISIBLE);
                    bt_Register.setEnabled(false);

                    if(checkConnection()){
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String userID = authResult.getUser().getUid();
                                String register_date_time = getTimeDate();
                                registerUser(
                                        userID,
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
                                        register_date_time,
                                        grade,
                                        password,
                                        urlOfProfile,
                                        balance,
                                        coins,
                                        isEmailVerified
                                );
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, R.string.verification_email_sent_to + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, R.string.failed_to_send_verification_email, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ri_progressBar.setVisibility(View.INVISIBLE);
                                bt_Register.setEnabled(true);
                                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        tv_alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to Login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        etPersonalNumberLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, getApplicationContext().getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        etPersonalNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    etPersonalNumberLayout.setError(getApplicationContext().getText(R.string.no_more_than_ten_digits));
                }else if(charSequence.length() < 10) {
                    etPersonalNumberLayout.setError(null);
                }else if(charSequence.length() == 10){
                    etPersonalNumberLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                    password,
                    urlOfProfile,
                    balance,
                    coins,
                    isEmailVerified
            );
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    ri_progressBar.setVisibility(View.INVISIBLE);
                    bt_Register.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, getApplicationContext().getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                    setEmptyFields();
                    goToSetProfilePicture();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ri_progressBar.setVisibility(View.INVISIBLE);
                    bt_Register.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }

    }

    public void setEmptyFields() {
        etName.setText("");
        etPhone.setText("");
        etAddress.setText("");
        etPersonalNumber.setText("");
        etEmailToInformer.setText("");
        etPasswordToInformer.setText("");
        etConfirmPassword.setText("");
        et_gender_user.setText("");
    }

    private void goToSetProfilePicture() {
        Intent intent = new Intent(RegisterActivity.this, SetProfileUserActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkConnection() {
        //Check Internet Connection
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(this)){
            return false;
        }else {
            return true;
        }
    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}