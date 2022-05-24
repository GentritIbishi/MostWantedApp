package fiek.unipr.mostwantedapp;

import static android.util.Log.e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import fiek.unipr.mostwantedapp.auth.GoogleSignInActivity;
import fiek.unipr.mostwantedapp.auth.PhoneSignInActivity;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.InformerDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "loginPreferences";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private TextView forgotPassword, tv_createNewAccount;
    private EditText etEmail, etPassword;
    private Button bt_Login, btnPhone, btnGoogle, btnAnonymous;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Remove pjesen lart full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        findViewById(R.id.btnGoogle).setOnClickListener(this);
        bt_Login = findViewById(R.id.bt_Login);
        tv_createNewAccount = findViewById(R.id.tv_createNewAccount);
        tv_createNewAccount.setOnClickListener(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnPhone = findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(this);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);
        btnAnonymous = findViewById(R.id.btnAnonymous);
        btnAnonymous.setOnClickListener(this);

        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            navigateToInformer();
        }

        if(firebaseUser != null)
        {
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        String role = task.getResult().getString("role");
                        if(role !=null && role.matches("User"))
                        {
                            Intent user = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            user.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(user);
                        }
                        else if(role != null && role.matches("Admin"))
                        {
                            Intent admin = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            admin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(admin);
                        }
                    }
                }
            });
            //Nese so as user as Admin, por nese firebaseUser eshte login po ska privilegje atehere qoje te informeri nese o login
            if(firebaseUser.isAnonymous()){
                anonymousAuth();
            }

            sendInformerToDashboard();
        }
    }

    public void sendInformerToDashboard() {
        Intent informer = new Intent(LoginActivity.this, InformerDashboardActivity.class);
        informer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(informer);
    }

    private void navigateToInformer() {
        Intent intent = new Intent(this, InformerDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_createNewAccount:
                startActivity(new Intent(this, RegisterInformerActivity.class));
                break;
            case R.id.btnGoogle:
                startActivity(new Intent(this, GoogleSignInActivity.class));
                finish();
                break;
            case R.id.btnPhone:
                startActivity(new Intent(this, PhoneSignInActivity.class));
                break;
            case R.id.btnAnonymous:
                anonymousAuth();
                break;
        }

    }

    private void anonymousAuth() {
        firebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent anonymous = new Intent(LoginActivity.this, InformerDashboardActivity.class);
                String uid = firebaseAuth.getCurrentUser().getUid().toString();
                anonymous.putExtra("uid_anonymous", uid);
                startActivity(anonymous);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.error_failed_to_login_as_anonymous, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if(email.isEmpty())
        {
            etEmail.setError(getText(R.string.error_email_required));
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etEmail.setError(getText(R.string.error_please_provide_valid_email));
            etEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            etPassword.setError(getText(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            etPassword.setError(getText(R.string.error_min_password_length));
            etPassword.requestFocus();
            return;
        }


        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() && task.getResult() !=null)
                {
                    String currentUserId = task.getResult().getUser().getUid();
                    documentReference = firebaseFirestore.collection("users").document(currentUserId);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null)
                            {
                                    String role = task.getResult().getString("role");
                                    String fullName = task.getResult().getString("fullName");
                                    String email = task.getResult().getString("email");

                                    //Set on Preferences on PREFS_NAME: loginPreferences
                                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("Role", role);
                                    editor.putString("fullName", fullName);
                                    editor.putString("email", email);
                                    editor.commit();
                                    //Set on Preferences on PREFS_NAME: loginPreferences

                                    User user = new User(currentUserId, email, role);
                                    documentReference = firebaseFirestore.collection("logins").document(currentUserId);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, R.string.logins_successfully, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, R.string.logins_failed, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                        if(role !=null && role.matches("Admin")) {
                                            Intent adminIntent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                            startActivity(adminIntent);
                                        }else {
                                            Intent userIntent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                            startActivity(userIntent);
                                        }
                            }
                            else
                                {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

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
}