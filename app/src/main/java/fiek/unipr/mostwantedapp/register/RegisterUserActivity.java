package fiek.unipr.mostwantedapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.helpers.SetImageActivity;
import fiek.unipr.mostwantedapp.models.User;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText etName, etLastName, etParentName, etPhone, etAddress, etNumPersonal,
            etEmail, etPassword, etConfirmPassword;
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
        etNumPersonal = findViewById(R.id.etNumPersonal);
        etEmail = findViewById(R.id.etEmailToRecovery);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ri_progressBar = findViewById(R.id.ri_progressBar);

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
                String numPersonal = etNumPersonal.getText().toString().trim();
                String parentName = etParentName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();
                String confirm_password = etConfirmPassword.getText().toString();
                String grade = "E";
                String role = "Informer";
                Integer balance = 0;
                Uri photoURL = null;

                if(TextUtils.isEmpty(fullName)){
                    etName.setError(getText(R.string.error_fullname_required));
                    etName.requestFocus();
                } else if(TextUtils.isEmpty(numPersonal)){
                    etNumPersonal.setError(getText(R.string.error_number_personal_required));
                    etNumPersonal.requestFocus();
                }else if(numPersonal.length()>10){
                    etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
                    etNumPersonal.requestFocus();
                }else if(numPersonal.length()<10){
                    etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
                    etNumPersonal.requestFocus();
                }else if(TextUtils.isEmpty(phone)){
                    etPhone.setError(getText(R.string.error_phone_required));
                    etPhone.requestFocus();
                }else if(TextUtils.isEmpty(address)){
                    etAddress.setError(getText(R.string.error_address_required));
                    etAddress.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    etEmail.setError(getText(R.string.error_email_required));
                    etEmail.requestFocus();
                }else if(!email.matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
                    etEmail.setError(getText(R.string.error_validate_email));
                    etEmail.requestFocus();
                }else if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
                    etPassword.setError(getText(R.string.error_password_weak));
                    etPassword.requestFocus();
                } else if(TextUtils.isEmpty(password)){
                    etPassword.setError(getText(R.string.error_password_required));
                    etPassword.requestFocus();
                }else if(TextUtils.isEmpty(confirm_password)){
                    etConfirmPassword.setError(getText(R.string.error_confirm_password_required));
                    etConfirmPassword.requestFocus();
                }else if(!password.matches(confirm_password)){
                    etPassword.setError(getText(R.string.error_password_not_same));
                    etPassword.requestFocus();
                }else {

                    ri_progressBar.setVisibility(View.VISIBLE);
                    bt_Register.setEnabled(false);

                    if(checkConnection()){
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String userID = authResult.getUser().getUid();
                                String register_date_time = getTimeDate();
                                registerUser(balance, userID, name, lastName, fullName, address, email, parentName, role, phone, numPersonal, register_date_time, grade, password, photoURL);
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterUserActivity.this, R.string.verification_email_sent_to + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterUserActivity.this, R.string.failed_to_send_verification_email, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ri_progressBar.setVisibility(View.INVISIBLE);
                                bt_Register.setEnabled(true);
                                Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(RegisterUserActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        tv_alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to Login
                Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser(Integer balance, String userID, String name, String lastname, String fullName, String address, String email, String parentName, String role, String phone, String personal_number, String register_date_time, String grade, String password, Uri photoURL) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(userID);
            User user = new User(balance, userID, name, lastname, fullName, address, email, parentName, role, phone, personal_number, register_date_time, grade, password, null, false);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    ri_progressBar.setVisibility(View.INVISIBLE);
                    bt_Register.setEnabled(true);
                    Toast.makeText(RegisterUserActivity.this, RegisterUserActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterUserActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                    setEmptyFields();
                    goToSetProfilePicture();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ri_progressBar.setVisibility(View.INVISIBLE);
                    bt_Register.setEnabled(true);
                    Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
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
        etNumPersonal.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void goToSetProfilePicture() {
        Intent intent = new Intent(RegisterUserActivity.this, SetImageActivity.class);
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
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}