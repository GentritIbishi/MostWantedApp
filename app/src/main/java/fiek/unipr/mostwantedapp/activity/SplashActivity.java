package fiek.unipr.mostwantedapp.activity;

import static fiek.unipr.mostwantedapp.utils.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.APPEARANCE_MODE_PREFERENCE;
import static fiek.unipr.mostwantedapp.utils.Constants.DARK_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.USER_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.LIGHT_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.SYSTEM_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.SEMI_ADMIN_ROLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.activity.dashboard.AnonymousDashboardActivity;
import fiek.unipr.mostwantedapp.activity.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.ServiceManager;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mContext = getApplicationContext();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mode = sharedPreferences.getString(APPEARANCE_MODE_PREFERENCE, SYSTEM_MODE);

        if (mode.equals(SYSTEM_MODE)) {
            //detect night mode or light mode
            int nightModeFlags =
                    getApplicationContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;

            }
            processLogin();
        } else if (mode.equals(DARK_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            processLogin();
        } else if (mode.equals(LIGHT_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            processLogin();
        }

    }

    private void processLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CheckInternet.isConnected(getApplicationContext())) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // user is signed in
                        DocumentReference documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    String role = task.getResult().getString(ROLE);
                                    if (role != null && role.matches(ADMIN_ROLE)) {
                                        // start AdminDashboardActivity
                                        ServiceManager.startServiceAdmin(mContext);
                                        Intent intent = new Intent(SplashActivity.this, AdminDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else if (role != null && role.matches(SEMI_ADMIN_ROLE)) {
                                        // start Semi-AdminDashboardActivity with limitation
                                        ServiceManager.startServiceAdmin(mContext);
                                        Intent intent = new Intent(SplashActivity.this, AdminDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else if (role != null && role.matches(USER_ROLE)) {
                                        // start UserDashboardActivity
                                        ServiceManager.startServiceUser(mContext);
                                        Intent intent = new Intent(SplashActivity.this, UserDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        // start AnonymousDashboardActivity not happen cause logout on destroy
                                        Intent intent = new Intent(SplashActivity.this, AnonymousDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                    } else {
                        // No user is signed in
                        // start login activity
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                } else {
                    CheckInternet.showSettingsAlert(getApplicationContext());
                }
            }
        }, 300);
    }

}