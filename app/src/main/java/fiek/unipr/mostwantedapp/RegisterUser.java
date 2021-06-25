package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private EditText et_user_fullname, et_user_email, et_user_password, et_user_confirm_password;
    private Button btn_register_user;
    private ProgressBar progressBar;
    private SpinnerAdapter spAdapter;
    private Spinner sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_register_user = (Button) findViewById(R.id.btn_register_user);
        btn_register_user.setOnClickListener(this);

        et_user_fullname = (EditText) findViewById(R.id.et_user_fullname);
        et_user_email = (EditText) findViewById(R.id.et_user_email);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        et_user_confirm_password = (EditText) findViewById(R.id.et_user_confirm_password);

        //Per Spinner

        sp = (Spinner) findViewById(R.id.spinner1);

        spAdapter = new SpinnerAdapter(this, R.layout.spinner_row, getResources().getStringArray(R.array.roles));
        spAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp.setAdapter(spAdapter);

        //Per Spinner

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_register_user:
                registerUser();
                break;
        }
    }

    private void registerUser() {

        String fullName = et_user_fullname.getText().toString().trim();
        String email = et_user_email.getText().toString().trim();
        String password = et_user_password.getText().toString().trim();
        String confirm_password = et_user_confirm_password.getText().toString().trim();
        String role = sp.getSelectedItem().toString();

        if(fullName.isEmpty())
        {
            et_user_fullname.setError(getText(R.string.error_fullname_required));
            et_user_fullname.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            et_user_email.setError(getText(R.string.error_email_required));
            et_user_email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            et_user_email.setError(getText(R.string.error_please_provide_valid_email));
            et_user_email.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            et_user_password.setError(getText(R.string.error_password_required));
            et_user_password.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            et_user_password.setError(getText(R.string.error_min_password_length));
            et_user_password.requestFocus();
            return;
        }

        if(confirm_password.isEmpty())
        {
            et_user_password.setError(getText(R.string.error_confirm_password_required));
            et_user_password.requestFocus();
            return;
        }

        if(!password.equals(confirm_password))
        {
            et_user_password.setError(getText(R.string.error_password_not_same));
            et_user_password.requestFocus();
            return;
        }

        if(!confirm_password.equals(password))
        {
            et_user_confirm_password.setError(getText(R.string.error_confirm_password_not_same));
            et_user_confirm_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Pjea e dokumentit
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    User user = new User(userID, fullName, email, role, null);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterUser.this, "Collection Successfully!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterUser.this, "Collection Failed!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    //Pjesa e dokumentit
                     Toast.makeText(RegisterUser.this, R.string.user_registered_successfully, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(RegisterUser.this, R.string.user_failed_to_register, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}