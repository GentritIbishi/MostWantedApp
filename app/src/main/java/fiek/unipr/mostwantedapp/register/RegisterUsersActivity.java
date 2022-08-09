package fiek.unipr.mostwantedapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.helpers.SpinnerAdapter;
import fiek.unipr.mostwantedapp.models.User;

public class RegisterUsersActivity extends AppCompatActivity {

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
    private MaterialAutoCompleteTextView et_role_autocomplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_users);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        admin_etName = findViewById(R.id.admin_etName);
        admin_etLastName = findViewById(R.id.admin_etLastName);
        admin_etParentName = findViewById(R.id.admin_etParentName);
        admin_etPhone = findViewById(R.id.admin_etPhone);
        admin_etAddress = findViewById(R.id.admin_etAddress);
        admin_etNumPersonal = findViewById(R.id.admin_etNumPersonal);
        admin_etNumPersonalLayout = findViewById(R.id.admin_etNumPersonalLayout);
        admin_etEmailToUser = findViewById(R.id.admin_etEmailToUser);
        admin_etPasswordToUser = findViewById(R.id.admin_etPasswordToUser);
        admin_etConfirmPassword = findViewById(R.id.admin_etConfirmPassword);

        admin_ri_progressBar = findViewById(R.id.admin_ri_progressBar);

        admin_bt_Register = findViewById(R.id.admin_bt_Register);

        et_role_autocomplete = findViewById(R.id.et_role_autocomplete);
        ArrayAdapter<String> role_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        et_role_autocomplete.setAdapter(role_adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        admin_bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = admin_etName.getText().toString().trim();
                String lastName = admin_etLastName.getText().toString().trim();
                String fullName = name + " "+lastName;
                String phone = admin_etPhone.getText().toString().trim();
                String address = admin_etAddress.getText().toString().trim();
                String numPersonal = admin_etNumPersonal.getText().toString().trim();
                String parentName = admin_etParentName.getText().toString().trim();
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
                Integer balance = 0;
                Uri photoURL = null;

                if(TextUtils.isEmpty(fullName)){
                    admin_etName.setError(getText(R.string.error_fullname_required));
                    admin_etName.requestFocus();
                } else if(TextUtils.isEmpty(numPersonal)){
                    admin_etNumPersonal.setError(getText(R.string.error_number_personal_required));
                    admin_etNumPersonal.requestFocus();
                }else if(numPersonal.length()>10){
                    admin_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
                    admin_etNumPersonal.requestFocus();
                }else if(numPersonal.length()<10){
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
                }else {

                    admin_ri_progressBar.setVisibility(View.VISIBLE);
                    admin_bt_Register.setEnabled(false);

                    if(checkConnection()){
                        String finalGrade = grade;
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String userID = authResult.getUser().getUid();
                                String register_date_time = getTimeDate();
                                registerUser(balance, userID, name, lastName, fullName, address, email, parentName, role, phone, numPersonal, register_date_time, finalGrade, password, photoURL);
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterUsersActivity.this, RegisterUsersActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterUsersActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                        Toast.makeText(RegisterUsersActivity.this, RegisterUsersActivity.this.getText(R.string.login_info), Toast.LENGTH_LONG).show();
                                        Toast.makeText(RegisterUsersActivity.this, R.string.verification_email_sent_to + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterUsersActivity.this, R.string.failed_to_send_verification_email, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                admin_ri_progressBar.setVisibility(View.INVISIBLE);
                                admin_bt_Register.setEnabled(true);
                                Toast.makeText(RegisterUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(RegisterUsersActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        admin_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterUsersActivity.this, getApplicationContext().getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        admin_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    admin_etNumPersonalLayout.setError(getApplicationContext().getText(R.string.no_more_than_ten_digits));
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

    }

    private void registerUser(Integer balance, String userID, String name, String lastname, String fullName, String address, String email, String parentName, String role, String phone, String personal_number, String register_date_time, String grade, String password, Uri photoURL) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(userID);
            User user = new User(balance, userID, name, lastname, fullName, address, email, parentName, role, phone, personal_number, register_date_time, grade, password, null, false);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    admin_ri_progressBar.setVisibility(View.INVISIBLE);
                    admin_bt_Register.setEnabled(true);
                    Toast.makeText(RegisterUsersActivity.this, RegisterUsersActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterUsersActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                    setEmptyFields();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    admin_ri_progressBar.setVisibility(View.INVISIBLE);
                    admin_bt_Register.setEnabled(true);
                    Toast.makeText(RegisterUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }

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

    public void setEmptyFields() {
        admin_etName.setText("");
        admin_etPhone.setText("");
        admin_etAddress.setText("");
        admin_etNumPersonal.setText("");
        admin_etEmailToUser.setText("");
        admin_etPasswordToUser.setText("");
        admin_etConfirmPassword.setText("");
    }

}