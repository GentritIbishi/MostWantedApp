package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboardActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "adminPreferences";
    public String role;
    public String fullName;
    LinearLayout l_registerUser, l_registerPerson;
    TextView tv_dashboard;
    Button bt_logout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        l_registerPerson = findViewById(R.id.l_registerPerson);
        l_registerPerson.setOnClickListener(this);
        l_registerUser = findViewById(R.id.l_registerUser);
        l_registerUser.setOnClickListener(this);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        bt_logout = findViewById(R.id.bt_logout);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle admin = getIntent().getExtras();
        if(admin != null)
        {
            role = admin.get("Role").toString();
            fullName = admin.get("fullName").toString();
        }
        else
        {
            role = null;
        }

        // kthej preferencat
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        role = settings.getString("Role", role);
        fullName = settings.getString("fullName", fullName);

        ((Activity) AdminDashboardActivity.this).runOnUiThread(new Runnable() {
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
                Intent signout = new Intent(AdminDashboardActivity.this, LoginActivity.class);
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
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.l_registerUser:
                startActivity(new Intent(AdminDashboardActivity.this, RegisterUser.class));
                break;
            case R.id.l_registerPerson:
                startActivity(new Intent(AdminDashboardActivity.this, RegisterPerson.class));
                break;
        }
    }

}