package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvguest;
    private ImageView imgIcon;
    private TextView tvEmail, tvPassword, tvforgotPassword;
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Button bt_Login;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_Login = findViewById(R.id.bt_Login);
        tvguest = (TextView)findViewById(R.id.tvguest);
        tvguest.setOnClickListener(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
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
                startActivity(new Intent(this, ListViewPerson.class));
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

        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            currentUserId = currentUser.getUid();
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    DocumentReference docRef = fStore.collection("users").document(currentUserId);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot != null)
                                {
                                    String role = documentSnapshot.getString("role");
                                    User user = new User(currentUserId, email, role);

                                    DocumentReference documentReference = fStore.collection("logins").document(currentUserId);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, R.string.logins_are_saved_successfully, Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, R.string.failed_logins_are_not_saved, Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                    //

                                    if(role.equals("Admin"))
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(LoginActivity.this, RegisterPerson.class);
                                        startActivity(intent);
                                        intent.putExtra("User ID",currentUserId);
                                        intent.putExtra("Role", role);
                                        finish();
                                    }
                                    else if(role.equals("User"))
                                    {
                                        //Mbetet mu permisu
                                        Intent intent = new Intent(LoginActivity.this, ListViewPerson.class);
                                        startActivity(intent);
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, R.string.role_not_finded, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            else
                                {
                                Toast.makeText(LoginActivity.this, R.string.user_not_finded, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, R.string.user_not_finded, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }



}