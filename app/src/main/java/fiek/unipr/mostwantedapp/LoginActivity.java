package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
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

import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.lists.PersonActivity;
import fiek.unipr.mostwantedapp.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "loginPreferences";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private TextView tvguest;
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;
    private Button bt_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        bt_Login = findViewById(R.id.bt_Login);
        tvguest = (TextView)findViewById(R.id.tvguest);
        tvguest.setOnClickListener(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvguest:
                startActivity(new Intent(this, PersonActivity.class));
                break;
        }

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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });



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
                                        progressBar.setVisibility(View.GONE);
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
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
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