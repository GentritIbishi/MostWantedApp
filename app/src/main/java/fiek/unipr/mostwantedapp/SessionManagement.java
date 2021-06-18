package fiek.unipr.mostwantedapp;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SessionManagement extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            Intent session = new Intent(SessionManagement.this, DashboardActivity.class);
            session.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(session);
        }
    }
}
