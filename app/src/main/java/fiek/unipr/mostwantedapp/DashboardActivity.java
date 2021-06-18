package fiek.unipr.mostwantedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout l_registerUser, l_registerPerson;
    TextView tv_dashboard;
    Button bt_logout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        l_registerPerson = findViewById(R.id.l_registerPerson);
        l_registerPerson.setOnClickListener(this);
        l_registerUser = findViewById(R.id.l_registerUser);
        l_registerUser.setOnClickListener(this);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        bt_logout = findViewById(R.id.bt_logout);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                progressBar.setVisibility(View.GONE);
                Intent signout = new Intent(DashboardActivity.this, LoginActivity.class);
                signout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signout);
                finish();
            }
        });

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
                startActivity(new Intent(DashboardActivity.this, RegisterUser.class));
                break;
            case R.id.l_registerPerson:
                startActivity(new Intent(DashboardActivity.this, RegisterPerson.class));
                break;
        }
    }

}