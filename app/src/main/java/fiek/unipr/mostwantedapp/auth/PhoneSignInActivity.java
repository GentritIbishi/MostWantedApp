package fiek.unipr.mostwantedapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.dashboard.InformerDashboardActivity;
import fiek.unipr.mostwantedapp.databinding.ActivityMainBinding;
import fiek.unipr.mostwantedapp.databinding.ActivityPhoneSignInBinding;

public class PhoneSignInActivity extends AppCompatActivity {

    //viewBinding
    private ActivityPhoneSignInBinding binding;

    //if code send failed will used to resend code OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId; // will hold OTP/Verification code
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressBar register_progressBar, verify_phone_progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneSignInBinding.inflate(getLayoutInflater());
        //Content view set in activity_phone_sign_in
        setContentView(binding.getRoot());

        register_progressBar = findViewById(R.id.register_progressBar);
        verify_phone_progressBar = findViewById(R.id.verify_phone_progressBar);

        binding.constrainRegistration.setVisibility(View.VISIBLE); // show register layout
        binding.constrainVerification.setVisibility(View.GONE);     // hide verify layout when otp send show

        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                register_progressBar.setVisibility(View.INVISIBLE);
                binding.btnRegistrationPhone.setEnabled(true);
                Toast.makeText(PhoneSignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                forceResendingToken = token;
                register_progressBar.setVisibility(View.INVISIBLE);
                binding.btnRegistrationPhone.setEnabled(true);

                //hide register layout, show verification layout
                binding.constrainRegistration.setVisibility(View.GONE);
                binding.constrainVerification.setVisibility(View.VISIBLE);

                String country_code = binding.etCountryCode.getText().toString().trim();
                String phone_number = binding.etPhoneNumber.getText().toString().trim();
                String phone = country_code + "" + phone_number;

                binding.tvPhoneNumber.setText(phone);
            }
        };

        binding.btnRegistrationPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country_code = binding.etCountryCode.getText().toString().trim();
                String phone_number = binding.etPhoneNumber.getText().toString().trim();
                String phone = country_code + "" + phone_number;
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneSignInActivity.this, R.string.please_enter_your_country_code_and_phone_number_for_verification, Toast.LENGTH_SHORT).show();
                }else {
                    startPhoneNumberVerification(phone);
                }
            }
        });

        binding.tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country_code = binding.etCountryCode.getText().toString().trim();
                String phone_number = binding.etPhoneNumber.getText().toString().trim();
                String phone = country_code + "" + phone_number;
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneSignInActivity.this, R.string.please_enter_your_country_code_and_phone_number_for_verification, Toast.LENGTH_SHORT).show();
                }else {
                    resendVerificationCode(phone, forceResendingToken);
                }
            }
        });

        binding.btnVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num1 = binding.etCode1.getText().toString().trim();
                String num2 = binding.etCode2.getText().toString().trim();
                String num3 = binding.etCode3.getText().toString().trim();
                String num4 = binding.etCode4.getText().toString().trim();
                String num5 = binding.etCode5.getText().toString().trim();
                String num6 = binding.etCode6.getText().toString().trim();
                String code = num1+""+num2+""+num3+""+num4+""+num5+""+num6;
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(PhoneSignInActivity.this, R.string.please_enter_verification_code, Toast.LENGTH_SHORT).show();
                }else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });

    }

    private void startPhoneNumberVerification(String phone) {
        register_progressBar.setVisibility(View.VISIBLE);
        binding.btnRegistrationPhone.setEnabled(false);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)                      // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
                .setActivity(this)                          // Activity (for callback binding)
                .setCallbacks(mCallbacks)                   // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)      //ForceResendingToken
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        verify_phone_progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        register_progressBar.setVisibility(View.VISIBLE);
        binding.btnRegistrationPhone.setEnabled(false);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //successfully signed in
                        register_progressBar.setVisibility(View.INVISIBLE);
                        binding.btnRegistrationPhone.setEnabled(true);
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneSignInActivity.this, R.string.logged_in_as+" "+phone, Toast.LENGTH_SHORT).show();
                        //start informer activity
                        goToInformerDashboard();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed signing in
                register_progressBar.setVisibility(View.INVISIBLE);
                binding.btnRegistrationPhone.setEnabled(true);
                Toast.makeText(PhoneSignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void goToInformerDashboard() {
        Intent intent = new Intent(PhoneSignInActivity.this, InformerDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}