package fiek.unipr.mostwantedapp;

import static fiek.unipr.mostwantedapp.utils.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // user is signed in
                    DocumentReference documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                String role = task.getResult().getString(ROLE);
                                if(role !=null && role.matches(ADMIN_ROLE))
                                {
                                    // start AdminDashboardActivity
                                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                                }
                                else
                                {
                                    // start UserDashboardActivity
                                    startActivity(new Intent(SplashActivity.this, UserDashboardActivity.class));
                                }
                            }
                        }
                    });

                } else {
                    // No user is signed in
                    // start login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        }, 500);
    }
}