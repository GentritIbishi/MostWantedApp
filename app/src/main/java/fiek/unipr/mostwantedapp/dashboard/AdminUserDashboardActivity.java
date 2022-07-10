package fiek.unipr.mostwantedapp.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.lists.LocationActivity;
import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.lists.PersonActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.register.RegisterPersonActivity;
import fiek.unipr.mostwantedapp.models.Profile;

public class AdminUserDashboardActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PREFS_NAME = "userPreference";
    private String role;
    private String fullName;
    private String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    LinearLayout l_userProfile, l_registerPerson, l_u_personsList, l_u_locationReports;
    ImageView user_img_profile;
    TextView tv_dashboard;
    Button bt_logout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_dashboard);

        l_registerPerson = findViewById(R.id.l_registerPerson);
        l_registerPerson.setOnClickListener(this);
        l_userProfile = findViewById(R.id.l_userProfile);
        l_userProfile.setOnClickListener(this);
        l_u_personsList = findViewById(R.id.l_u_personsList);
        l_u_personsList.setOnClickListener(this);
        l_u_locationReports = findViewById(R.id.l_u_locationReports);
        l_u_locationReports.setOnClickListener(this);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        bt_logout = findViewById(R.id.bt_logout);
        user_img_profile = findViewById(R.id.user_img_profile);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(user_img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminUserDashboardActivity.this, R.string.image_not_set, Toast.LENGTH_SHORT).show();
            }
        });

        // kthej preferencat
        SharedPreferences settings = getSharedPreferences("loginPreferences", 0);
        role = settings.getString("Role", "");
        fullName = settings.getString("fullName", "");
        email = settings.getString("email", "");


            ((Activity) AdminUserDashboardActivity.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        ((TextView) tv_dashboard).setText(fullName+", Dashboard");
                }
            });


        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                progressBar.setVisibility(View.GONE);
                Intent signout = new Intent(AdminUserDashboardActivity.this, LoginActivity.class);
                signout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signout);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.l_userProfile:
                startActivity(new Intent(AdminUserDashboardActivity.this, Profile.class));
                break;
            case R.id.l_registerPerson:
                startActivity(new Intent(AdminUserDashboardActivity.this, RegisterPersonActivity.class));
                break;
            case R.id.l_u_personsList:
                startActivity(new Intent(AdminUserDashboardActivity.this, PersonActivity.class));
                break;
            case R.id.l_u_locationReports:
                startActivity(new Intent(AdminUserDashboardActivity.this, LocationActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}