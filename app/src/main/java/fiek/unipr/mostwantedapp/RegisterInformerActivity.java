package fiek.unipr.mostwantedapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fiek.unipr.mostwantedapp.models.Informer;
import fiek.unipr.mostwantedapp.models.User;
import fiek.unipr.mostwantedapp.register.RegisterUser;

public class RegisterInformerActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etAddress, etNumPersonal,
            etEmail, etPassword, etConfirmPassword;
    private Button bt_Register;
    private TextView tv_alreadyHaveAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private String collection = "informers_by_default_sign_in";
    private String role = "informer";
    private ProgressBar ri_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_informer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etNumPersonal = findViewById(R.id.etNumPersonal);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
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
                // Ketu do behet regjistrimi i raportuesit

                String fullName = etFullName.getText().toString().trim();
                String numPersonal = etNumPersonal.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();
                String confirm_password = etConfirmPassword.getText().toString();
                String parentName = null;
                String grade = "E";
                Integer balance = 0;
                String googleID = null;
                Uri photoURL = null;

                if(TextUtils.isEmpty(fullName)){
                    etFullName.setError(getText(R.string.error_fullname_required));
                    etFullName.requestFocus();
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

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterInformerActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userID = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firebaseFirestore.collection(collection).document(userID);
                                Informer informer = new Informer(balance, userID, null, null, fullName, address, email, parentName, grade, googleID, phone, numPersonal, role,  photoURL);
                                documentReference.set(informer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ri_progressBar.setVisibility(View.INVISIBLE);
                                        bt_Register.setEnabled(true);
                                        Toast.makeText(RegisterInformerActivity.this, RegisterInformerActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterInformerActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                        setEmptyFields();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ri_progressBar.setVisibility(View.INVISIBLE);
                                        bt_Register.setEnabled(true);
                                        Toast.makeText(RegisterInformerActivity.this, R.string.person_failed_to_register, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                ri_progressBar.setVisibility(View.INVISIBLE);
                                bt_Register.setEnabled(true);
                                Toast.makeText(RegisterInformerActivity.this, R.string.user_failed_to_register, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

        tv_alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to Login
                Intent intent = new Intent(RegisterInformerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void setEmptyFields() {
        etFullName.setText("");
        etPhone.setText("");
        etAddress.setText("");
        etNumPersonal.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

}