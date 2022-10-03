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
    private DrawerLayout anonymous_drawerLayout_real;
    private ActionBarDrawerToggle anonymous_toggle;
    private Toolbar anonymous_toolbar;
    private NavigationView anonymous_nav_view;
    private Button anonymous_menu_group_logout;
    private ProgressBar anonymous_logout_progressBar;
    private ImageView anonymous_homeImage, anonymous_searchImage, anonymous_notificationImage, anonymous_profileImage;
    private LinearLayout anonymous_homeLayout, anonymous_searchLayout, anonymous_notificationLayout, anonymous_profileLayout;

    //nav header
    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view, topImageProfile;

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

        anonymous_nav_view = findViewById(R.id.anonymous_nav_view);
        View nav_header_view = anonymous_nav_view.getHeaderView(0);
        nav_header_name = nav_header_view.findViewById(R.id.nav_header_name);
        verifiedBadge = nav_header_view.findViewById(R.id.verifiedBadge);
        nav_header_image_view = nav_header_view.findViewById(R.id.nav_header_image_view);

        anonymous_drawerLayout_real = findViewById(R.id.anonymous_drawerLayout_real);
        anonymous_logout_progressBar = findViewById(R.id.anonymous_logout_progressBar);
        anonymous_toolbar = findViewById(R.id.anonymous_toolbar);
        topImageProfile = findViewById(R.id.topImageProfile);
        anonymous_menu_group_logout = findViewById(R.id.anonymous_menu_group_logout);

        anonymousID = firebaseAuth.getUid();

        setHomeDefaultConfig();

        anonymous_nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                switch (id)
                {
                    case R.id.anonymous_menu_group_logout:
                        anonymous_logout_progressBar.setVisibility(View.VISIBLE);
                        Logout();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        
    }

    private void Logout() {
        //check for phone authentication
        if(firebaseAuth != null){
            firebaseAuth.signOut();
            sendAnonymousToLogin();
        }
        anonymous_logout_progressBar.setVisibility(View.GONE);
    }

    public void sendAnonymousToLogin() {
        Intent loginIntent = new Intent(AnonymousDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void setHomeDefaultConfig() {
        setSupportActionBar(anonymous_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        anonymous_toolbar.setTitle("");
        anonymous_toolbar.setSubtitle("");

        anonymous_toggle = new ActionBarDrawerToggle(AnonymousDashboardActivity.this, anonymous_drawerLayout_real, anonymous_toolbar, R.string.open, R.string.close);
        anonymous_drawerLayout_real.addDrawerListener(anonymous_toggle);
        anonymous_toggle.syncState();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.anonymous_fragmentContainer, MapsFragment.class, null)
                .commit();

    }
}