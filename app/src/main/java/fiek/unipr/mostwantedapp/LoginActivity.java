package fiek.unipr.mostwantedapp;


import static fiek.unipr.mostwantedapp.helpers.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.helpers.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.helpers.Constants.INFORMER_ROLE;
import static fiek.unipr.mostwantedapp.helpers.Constants.LOGIN_INFORMER_PREFS;
import static fiek.unipr.mostwantedapp.helpers.Constants.LOGIN_HISTORY;
import static fiek.unipr.mostwantedapp.helpers.Constants.PREFS_NAME;
import static fiek.unipr.mostwantedapp.helpers.Constants.USERS;
import static fiek.unipr.mostwantedapp.helpers.Constants.USER_ROLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import fiek.unipr.mostwantedapp.auth.ForgotPasswordActivity;
import fiek.unipr.mostwantedapp.auth.PhoneSignInActivity;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.helpers.DateHelper;
import fiek.unipr.mostwantedapp.helpers.SecurityHelper;
import fiek.unipr.mostwantedapp.models.LoginHistory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private TextView forgotPassword, tv_createNewAccount;
    private TextInputEditText etEmail, etPassword;
    private Button bt_Login, btnPhone, btnAnonymous;
    private ProgressBar login_progressBar, phone_progressBar, anonymous_progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tv_createNewAccount = findViewById(R.id.tv_remember);
        tv_createNewAccount.setOnClickListener(this);

        btnAnonymous = findViewById(R.id.btnAnonymous);
        btnAnonymous.setOnClickListener(this);

        bt_Login = findViewById(R.id.bt_Login);
        bt_Login.setOnClickListener(this);

        btnPhone = findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        login_progressBar = findViewById(R.id.login_progressBar);
        phone_progressBar = findViewById(R.id.phone_progressBar);
        anonymous_progressBar = findViewById(R.id.anonymous_progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser != null)
        {
            if(firebaseUser.isAnonymous()){
                signInAnonymouslyInformer();
            }else if(firebaseUser.isEmailVerified()){
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                checkUserRoleAndGoToDashboard(firebaseAuth.getCurrentUser().getUid(), email, password);
            }else if(firebaseUser.getPhoneNumber() != null){
                goToInformerDashboard();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remember:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnPhone:
                enableProgressBar(phone_progressBar, btnPhone);
                final Handler phone = new Handler(Looper.getMainLooper());
                phone.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), PhoneSignInActivity.class));
                        disableProgressBar(phone_progressBar, btnPhone);
                    }
                }, 1000);
                break;
            case R.id.btnAnonymous:
                enableProgressBar(anonymous_progressBar, btnAnonymous);
                final Handler anonymous = new Handler(Looper.getMainLooper());
                anonymous.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        signInAnonymouslyInformer();
                        disableProgressBar(anonymous_progressBar, btnAnonymous);
                    }
                }, 2000);
                break;
            case R.id.bt_Login:
                Login();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }

    }

    private void Login() {

        login_progressBar.setVisibility(View.VISIBLE);
        bt_Login.setEnabled(false);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            etEmail.setError(getText(R.string.error_email_required));
            etEmail.requestFocus();
            login_progressBar.setVisibility(View.GONE);
            bt_Login.setEnabled(true);
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etEmail.setError(getText(R.string.error_please_provide_valid_email));
            etEmail.requestFocus();
            login_progressBar.setVisibility(View.GONE);
            bt_Login.setEnabled(true);
            return;
        }else if(password.isEmpty())
        {
            etPassword.setError(getText(R.string.error_password_required));
            etPassword.requestFocus();
            login_progressBar.setVisibility(View.GONE);
            bt_Login.setEnabled(true);
            return;
        }else if(password.length() < 6)
        {
            etPassword.setError(getText(R.string.error_min_password_length));
            etPassword.requestFocus();
            login_progressBar.setVisibility(View.GONE);
            bt_Login.setEnabled(true);
            return;
        }else
        {
            signIn(email, password);
        }
    }

    public void signIn(String email, String password) {
        if(checkConnection()){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                            String userID = task.getResult().getUser().getUid();
                            checkUserRoleAndGoToDashboard(userID, email, password);
                        }else {
                            sendEmailVerification();
                        }

                    } else {
                        login_progressBar.setVisibility(View.INVISIBLE);
                        bt_Login.setEnabled(true);
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserRoleAndGoToDashboard(String userID, String email, String password){
        if(checkConnection()){
            documentReference = firebaseFirestore.collection(USERS).document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try
                    {
                        String role = documentSnapshot.getString("role");
                        String fullName = documentSnapshot.getString("fullName");
                        String date_time = DateHelper.getDateTime();
                        SecurityHelper securityHelper = new SecurityHelper();
                        String hashPassword = securityHelper.encrypt(password);

                        LoginHistory loginHistory = new LoginHistory(userID, email, hashPassword, role, date_time);
                        setSharedPreference(userID, role, fullName, etEmail.getText().toString());
                        setLoginHistory(loginHistory);

                        if (role != null && role.matches(ADMIN_ROLE)) {
                            login_progressBar.setVisibility(View.INVISIBLE);
                            bt_Login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                            goToAdminDashboard();
                        } else if(role != null && role.matches(USER_ROLE)){
                            login_progressBar.setVisibility(View.INVISIBLE);
                            bt_Login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                            goToUserDashboard();
                        }else if(role != null && role.matches(INFORMER_ROLE)){
                            login_progressBar.setVisibility(View.INVISIBLE);
                            bt_Login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                            goToInformerDashboard();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    login_progressBar.setVisibility(View.INVISIBLE);
                    bt_Login.setEnabled(true);
                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void signInAnonymouslyInformer() {
        if(checkConnection()){
            firebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    setSharedPreferenceInformer(firebaseAuth.getCurrentUser().getUid());
                    setSharedPreferenceAnonymous(ANONYMOUS);
                    goToInformerDashboard();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, R.string.error_failed_to_login_as_anonymous, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    public void setSharedPreference(String currentUserId, String role, String fullName, String email) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userID", currentUserId);
        editor.putString("Role", role);
        editor.putString("fullName", fullName);
        editor.putString("email", email);
        editor.commit();
    }

    public void setSharedPreferenceInformer(String currentUserId) {
        SharedPreferences settings = getSharedPreferences(LOGIN_INFORMER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userID", currentUserId);
        editor.commit();
    }

    public void setSharedPreferenceAnonymous(String name) {
        SharedPreferences settings = getSharedPreferences(LOGIN_INFORMER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("anonymous", name);
        editor.commit();
    }

    public void setLoginHistory(LoginHistory loginHistory) {

        documentReference = firebaseFirestore.collection(LOGIN_HISTORY).document(loginHistory.getUserID()).collection(loginHistory.getDate_time()).document();
        documentReference.set(loginHistory);

        firebaseFirestore.collection(USERS).document(loginHistory.getUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String passFromDb = documentSnapshot.getString("password");
                            if(!passFromDb.equals(loginHistory.getPassword()))
                            {
                                firebaseFirestore.collection(USERS).document(loginHistory.getUserID())
                                        .update("password", loginHistory.getPassword());
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToInformerDashboard() {
        Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAdminDashboard() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUserDashboard() {
//        Intent intent = new Intent(LoginActivity.this, AdminUserDashboardActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void sendEmailVerification() {
        if(checkConnection()){
            firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    login_progressBar.setVisibility(View.INVISIBLE);
                    bt_Login.setEnabled(true);
                    Toast.makeText(LoginActivity.this, R.string.please_verify_email_to_login_check_in_email_we_sent_link_for_verification, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        //disabled back pressed
    }

    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event);
            return true;
        }
        return false;
    }

    private void enableProgressBar(ProgressBar progressBar, Button button) {
                progressBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
    }

    private void disableProgressBar(ProgressBar progressBar, Button button) {
        progressBar.setVisibility(View.INVISIBLE);
        button.setEnabled(true);
    }
}