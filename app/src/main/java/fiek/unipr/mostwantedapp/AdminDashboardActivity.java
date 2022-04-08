package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AdminDashboardActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PREFS_NAME = "adminPreferences";
    private String role;
    private String fullName;
    private String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    LinearLayout l_registerUser, l_registerPerson, l_adminProfile, l_userList, l_personsList, l_locationReports;
    ImageView admin_img_profile;
    TextView tv_dashboard;
    Button bt_logout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        l_registerPerson = findViewById(R.id.l_registerPerson);
        l_registerPerson.setOnClickListener(this);
        l_registerUser = findViewById(R.id.l_registerUser);
        l_registerUser.setOnClickListener(this);
        l_locationReports = findViewById(R.id.l_locationReports);
        l_locationReports.setOnClickListener(this);
        l_userList = findViewById(R.id.l_userList);
        l_userList.setOnClickListener(this);
        l_personsList = findViewById(R.id.l_personsList);
        l_personsList.setOnClickListener(this);
        l_adminProfile = findViewById(R.id.l_adminProfile);
        l_adminProfile.setOnClickListener(this);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        admin_img_profile = findViewById(R.id.admin_img_profile);
        bt_logout = findViewById(R.id.bt_logout);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(admin_img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminDashboardActivity.this, R.string.image_not_set, Toast.LENGTH_SHORT).show();
            }
        });

            // kthej preferencat
            SharedPreferences settings = getSharedPreferences("loginPreferences", 0);
            role = settings.getString("Role", "");
            fullName = settings.getString("fullName", "");
            email = settings.getString("email", "");

        ((Activity) AdminDashboardActivity.this).runOnUiThread(new Runnable() {
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
                Intent signout = new Intent(AdminDashboardActivity.this, LoginActivity.class);
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
            case R.id.l_registerUser:
                startActivity(new Intent(AdminDashboardActivity.this, RegisterUser.class));
                break;
            case R.id.l_registerPerson:
                startActivity(new Intent(AdminDashboardActivity.this, RegisterPerson.class));
                break;
            case R.id.l_adminProfile:
                startActivity(new Intent(AdminDashboardActivity.this, Profile.class));
                break;
            case R.id.l_userList:
                startActivity(new Intent(AdminDashboardActivity.this, UserActivity.class));
                break;
            case R.id.l_personsList:
                startActivity(new Intent(AdminDashboardActivity.this, PersonActivity.class));
                break;
            case R.id.l_locationReports:
                startActivity(new Intent(AdminDashboardActivity.this, LocationActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}