package fiek.unipr.mostwantedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDashboardActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "userPreference";
    public String role;
    public String fullName;
    LinearLayout l_userProfile, l_registerPerson;
    TextView tv_dashboard;
    Button bt_logout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        l_registerPerson = findViewById(R.id.l_registerPerson);
        l_registerPerson.setOnClickListener(this);
        l_userProfile = findViewById(R.id.l_userProfile);
        l_userProfile.setOnClickListener(this);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        bt_logout = findViewById(R.id.bt_logout);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle user = getIntent().getExtras();
        if(user != null)
        {
            role = user.get("Role").toString();
            fullName = user.get("fullName").toString();
        }
        else
        {
            role = null;
        }

        // kthej preferencat
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        role = settings.getString("Role", role);
        fullName = settings.getString("fullName", fullName);

        ((Activity) UserDashboardActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(role != null) {
                    ((TextView) tv_dashboard).setText(fullName+", Dashboard");
                }
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                progressBar.setVisibility(View.GONE);
                Intent signout = new Intent(UserDashboardActivity.this, LoginActivity.class);
                signout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signout);
                finish();
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        //Editori eshte objekt si redaktor qe i kallxon qe jon ndrru preferencat
        //Te gjitha objektet jan prej librarise android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Role", role);
        editor.putString("fullName", fullName);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.l_userProfile:
                startActivity(new Intent(UserDashboardActivity.this, Profile.class));
                break;
            case R.id.l_registerPerson:
                startActivity(new Intent(UserDashboardActivity.this, RegisterPerson.class));
                break;
        }
    }
}