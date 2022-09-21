package fiek.unipr.mostwantedapp.activity;

import static fiek.unipr.mostwantedapp.utils.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.INFORMER_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.USER_ROLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.utils.WindowHelper;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        WindowHelper.setFullScreenActivity(getWindow());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

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
                                else if(role !=null && role.matches(INFORMER_ROLE))
                                {
                                    // start UserDashboardActivity
                                    startActivity(new Intent(SplashActivity.this, UserDashboardActivity.class));
                                }else if(role !=null && role.matches(USER_ROLE)){
                                    // start AdminUserDashboardActivity .. soon!
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