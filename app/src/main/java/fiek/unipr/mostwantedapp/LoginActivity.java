package fiek.unipr.mostwantedapp;


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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import fiek.unipr.mostwantedapp.auth.ForgotPasswordActivity;
import fiek.unipr.mostwantedapp.auth.GoogleSignInActivity;
import fiek.unipr.mostwantedapp.auth.PhoneSignInActivity;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.InformerDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.models.User;
import fiek.unipr.mostwantedapp.register.RegisterUserActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "loginPreferences";
    public static final String LOGIN_INFORMER_PREFS = "loginInformerPreferences";
    public static final String ANONYMOUS = "ANONYMOUS";
    private static final String login_users = "logins_users";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private TextView forgotPassword, tv_createNewAccount;
    private EditText etEmail, etPassword;
    private Button bt_Login, btnPhone, btnGoogle, btnAnonymous;
    private ProgressBar login_progressBar;
    private ProgressBar google_progressBar;
    private ProgressBar phone_progressBar;
    private ProgressBar anonymous_progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);

        tv_createNewAccount = findViewById(R.id.tv_remember);
        tv_createNewAccount.setOnClickListener(this);

        btnAnonymous = findViewById(R.id.btnAnonymous);
        btnAnonymous.setOnClickListener(this);

        bt_Login = findViewById(R.id.bt_Login);
        bt_Login.setOnClickListener(this);

        btnPhone = findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(this);

        etEmail = findViewById(R.id.etEmailToRecovery);
        etPassword = findViewById(R.id.etPassword);

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        login_progressBar = findViewById(R.id.login_progressBar);
        google_progressBar = findViewById(R.id.google_progressBar);
        phone_progressBar = findViewById(R.id.phone_progressBar);
        anonymous_progressBar = findViewById(R.id.anonymous_progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkGoogleSignIn();
        if(firebaseUser != null)
        {
            if(firebaseUser.isAnonymous()){
                signInAnonymouslyInformer();
            }else if(firebaseUser.isEmailVerified()){
                checkUserRoleAndGoToDashboard(firebaseAuth.getCurrentUser().getUid());
            }else if(firebaseUser.getPhoneNumber() != null){
                goToInformerDashboard();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remember:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.btnGoogle:
                enableProgressBar(google_progressBar, btnGoogle);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        startActivity(new Intent(getApplicationContext(), GoogleSignInActivity.class));
                        disableProgressBar(google_progressBar, btnGoogle);
                    }
                }, 1000);
                break;
            case R.id.btnPhone:
                enableProgressBar(phone_progressBar, btnPhone);
                final Handler phone = new Handler(Looper.getMainLooper());
                phone.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 1000ms
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
                            String currentUserId = task.getResult().getUser().getUid();
                            checkUserRoleAndGoToDashboard(currentUserId);
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

    private void checkUserRoleAndGoToDashboard(String currentUserId) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(currentUserId);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String role = documentSnapshot.getString("role");
                    String fullName = documentSnapshot.getString("fullName");

                    User user = new User(currentUserId, etEmail.getText().toString(), role, etPassword.getText().toString());
                    setSharedPreference(currentUserId, role, fullName, etEmail.getText().toString());
                    setLoginsHistoryForUser(user, currentUserId);
                    if (role != null && role.matches("Admin")) {
                        login_progressBar.setVisibility(View.INVISIBLE);
                        bt_Login.setEnabled(true);
                        Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                        goToAdminDashboard();
                    } else if(role != null && role.matches("User")){
                        login_progressBar.setVisibility(View.INVISIBLE);
                        bt_Login.setEnabled(true);
                        Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                        goToUserDashboard();
                    }else if(role != null && role.matches("Informer")){
                        login_progressBar.setVisibility(View.INVISIBLE);
                        bt_Login.setEnabled(true);
                        Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                        goToInformerDashboard();
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

    public void setLoginsHistoryForUser(User user, String currentUserId) {
        documentReference = firebaseFirestore.collection(login_users).document(currentUserId);
        documentReference.set(user);
    }

    private void goToInformerDashboard() {
        Intent intent = new Intent(LoginActivity.this, InformerDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToAdminDashboard() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUserDashboard() {
        Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkGoogleSignIn() {
        if(checkConnection()){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account != null){
                goToInformerDashboard();
            }
        }else {
            Toast.makeText(LoginActivity.this, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }

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