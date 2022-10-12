package fiek.unipr.mostwantedapp.activity.dashboard;

import static fiek.unipr.mostwantedapp.activity.dashboard.AdminDashboardActivity.IS_LOGGED;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.activity.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.user.AboutFragment;
import fiek.unipr.mostwantedapp.fragment.user.HelpFragment;
import fiek.unipr.mostwantedapp.fragment.user.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.user.NotificationFragment;
import fiek.unipr.mostwantedapp.fragment.user.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.user.SearchFragment;
import fiek.unipr.mostwantedapp.fragment.user.SettingsFragment;
import fiek.unipr.mostwantedapp.fragment.user.WithdrawFragment;
import fiek.unipr.mostwantedapp.services.UserNotificationService;
import fiek.unipr.mostwantedapp.utils.CheckInternet;

public class UserDashboardActivity extends AppCompatActivity {

    private int selectedTab = 1;
    private Integer balance;
    private String userID, fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;
    private String user_anonymousID = null;

    private DrawerLayout user_drawerLayout_real;
    private ActionBarDrawerToggle user_toggle;
    private Toolbar user_toolbar;
    private NavigationView user_nav_view;
    private Button user_menu_group_logout;
    private ProgressBar user_logout_progressBar;
    private ImageView user_homeImage, user_searchImage, user_notificationImage, user_profileImage;
    private LinearLayout user_homeLayout, user_searchLayout, user_notificationLayout, user_profileLayout;

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
        setContentView(R.layout.activity_user_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        startService();

        user_nav_view = findViewById(R.id.user_nav_view);
        View nav_header_view = user_nav_view.getHeaderView(0);
        nav_header_name = nav_header_view.findViewById(R.id.nav_header_name);
        verifiedBadge = nav_header_view.findViewById(R.id.verifiedBadge);
        nav_header_image_view = nav_header_view.findViewById(R.id.nav_header_image_view);

        user_homeLayout = findViewById(R.id.user_homeLayout);
        user_searchLayout = findViewById(R.id.user_searchLayout);
        user_notificationLayout = findViewById(R.id.user_notificationLayout);
        user_profileLayout = findViewById(R.id.user_profileLayout);

        user_homeImage = findViewById(R.id.user_homeImage);
        user_searchImage = findViewById(R.id.user_searchImage);
        user_notificationImage = findViewById(R.id.user_notificationImage);
        user_profileImage = findViewById(R.id.user_profileImage);

        user_drawerLayout_real = findViewById(R.id.user_drawerLayout_real);
        user_logout_progressBar = findViewById(R.id.user_logout_progressBar);
        user_toolbar = findViewById(R.id.user_toolbar);
        topImageProfile = findViewById(R.id.topImageProfile);
        user_menu_group_logout = findViewById(R.id.user_menu_group_logout);

        userID = firebaseAuth.getUid();

        setHomeDefaultConfig();

        user_menu_group_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });

        user_nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                switch (id) {
                    case R.id.user_menu_group_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        setProfileSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_settings:
                        fragment = new SettingsFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_help:
                        fragment = new HelpFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_about:
                        fragment = new AboutFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_logout:
                        user_logout_progressBar.setVisibility(View.VISIBLE);
                        Logout();
                        break;
                    case R.id.user_menu_group_withdraw:
                        fragment = new WithdrawFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        user_nav_view.setCheckedItem(id);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        user_homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 1) {
                    setHomeSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, HomeFragment.class, null)
                            .commit();
                }
                if (selectedTab == 1) {
                    setHomeSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, HomeFragment.class, null)
                            .commit();
                }
            }
        });

        user_searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 2) {
                    setSearchSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, SearchFragment.class, null)
                            .commit();
                }
                if (selectedTab == 2) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, SearchFragment.class, null)
                            .commit();
                }
            }
        });

        user_notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 3) {
                    setNotificationSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, NotificationFragment.class, null)
                            .commit();
                }
                if (selectedTab == 3) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, NotificationFragment.class, null)
                            .commit();
                }

            }
        });

        user_profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 4) {
                    setProfileSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, ProfileFragment.class, null)
                            .commit();
                }
                if (selectedTab == 4) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, ProfileFragment.class, null)
                            .commit();
                }
            }
        });

    }

    private void startService() {
        final Intent intentModified = new Intent(UserDashboardActivity.this, UserNotificationService.class);
        startService(intentModified);
    }

    private void stopService() {
        final Intent intentModified = new Intent(UserDashboardActivity.this, UserNotificationService.class);
        stopService(intentModified);
    }

    @Override
    protected void onDestroy() {
        if (firebaseAuth != null) {
            SharedPreferences.Editor editor = getSharedPreferences(IS_LOGGED, MODE_PRIVATE).edit();
            editor.putBoolean("isLogged", true);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(IS_LOGGED, MODE_PRIVATE).edit();
            editor.putBoolean("isLogged", false);
            editor.apply();
        }
        super.onDestroy();
    }

    private void setHomeSelected() {
        user_homeImage.setImageResource(R.drawable.ic_home_selected);
        user_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
        user_nav_view.getMenu().getItem(0).setChecked(true);
    }

    private void setSearchSelected() {
        user_searchImage.setImageResource(R.drawable.ic_search_selected);
        user_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        user_homeImage.setImageResource(R.drawable.ic_home_unselected);
        unSelectAllItemInMenu();
    }

    private void unSelectAllItemInMenu() {
        for (int i = 0; i < user_nav_view.getMenu().size(); i++) {
            user_nav_view.getMenu().getItem(i).setChecked(false);
        }
    }

    private void setProfileSelected() {
        user_profileImage.setImageResource(R.drawable.ic_profile_selected);
        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
        user_homeImage.setImageResource(R.drawable.ic_home_unselected);
        user_nav_view.getMenu().getItem(1).setChecked(true);
    }

    private void setNotificationSelected() {
        user_notificationImage.setImageResource(R.drawable.ic_notification_selected);
        user_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
        user_homeImage.setImageResource(R.drawable.ic_home_unselected);
        unSelectAllItemInMenu();
    }

    private void setHomeDefaultConfig() {
        setSupportActionBar(user_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        user_toolbar.setTitle("");
        user_toolbar.setSubtitle("");

        user_toggle = new ActionBarDrawerToggle(UserDashboardActivity.this, user_drawerLayout_real, user_toolbar, R.string.open, R.string.close);
        user_drawerLayout_real.addDrawerListener(user_toggle);
        user_toggle.syncState();

        user_homeImage.setImageResource(R.drawable.ic_home_selected);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.user_fragmentContainer, HomeFragment.class, null)
                .commit();

        //set deafult seleted
        user_nav_view.getMenu().getItem(0).setChecked(true);
    }

    private void Logout() {
        //check for phone authentication
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
            stopService();
            sendUserToLogin();
        }
        user_logout_progressBar.setVisibility(View.GONE);
    }

    public void sendUserToLogin() {
        Intent loginIntent = new Intent(UserDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.user_fragmentContainer, fragment);
        user_drawerLayout_real.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(UserDashboardActivity.this)) {
            return false;
        } else {
            return true;
        }
    }

    private void setVerifiedBadge(FirebaseUser firebaseUser) {
        if (firebaseUser.isEmailVerified()) {
            verifiedBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth != null) {
            loadInfoFromFirebase(firebaseAuth);
        }
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if (checkConnection()) {
            documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        fullName = task.getResult().getString("fullName");
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        //set Image, verified if is email verified, name
                        setVerifiedBadge(firebaseAuth.getCurrentUser());
                        if (urlOfProfile != null) {
                            Picasso.get().load(urlOfProfile).into(nav_header_image_view);
                            Picasso.get().load(urlOfProfile).into(topImageProfile);
                        }

                        if (fullName != null) {
                            nav_header_name.setText(fullName);
                        }
                    }
                }
            });
        }
    }

}