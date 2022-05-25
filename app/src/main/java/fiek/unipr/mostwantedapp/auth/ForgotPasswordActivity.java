package fiek.unipr.mostwantedapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import fiek.unipr.mostwantedapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailToRecovery;
    private Button btnResetPassword;
    private ProgressBar reset_progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmailToRecovery = findViewById(R.id.etEmailToRecovery);
        btnResetPassword = findViewById(R.id.bt_Login);

        reset_progressBar = findViewById(R.id.reset_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email = etEmailToRecovery.getText().toString().trim();

        if(email.isEmpty()){
            etEmailToRecovery.setError(getText(R.string.error_email_required));
            etEmailToRecovery.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmailToRecovery.setError(getText(R.string.error_please_provide_valid_email));
            etEmailToRecovery.requestFocus();
            return;
        }

        reset_progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    reset_progressBar.setVisibility(View.INVISIBLE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, R.string.check_your_email_to_reset_password, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    reset_progressBar.setVisibility(View.INVISIBLE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_try_again_something_wrong_happened, Toast.LENGTH_SHORT).show();
                    etEmailToRecovery.setText("");
                }
            }
        });


    }
}