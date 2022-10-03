package fiek.unipr.mostwantedapp.activity.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.LoginActivity;
import fiek.unipr.mostwantedapp.fragment.anonymous.MapsFragment;
import fiek.unipr.mostwantedapp.fragment.user.AboutFragment;
import fiek.unipr.mostwantedapp.fragment.user.HelpFragment;
import fiek.unipr.mostwantedapp.fragment.user.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.user.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.user.SettingsFragment;

public class AnonymousDashboardActivity extends AppCompatActivity {

    private String anonymousID;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ListenerRegistration registration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        anonymousID = firebaseAuth.getUid();
        setHomeDefaultConfig();

    }

    public void sendAnonymousToLogin() {
        Intent loginIntent = new Intent(AnonymousDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void setHomeDefaultConfig() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.anonymous_fragmentContainer, MapsFragment.class, null)
                .commit();
    }
}