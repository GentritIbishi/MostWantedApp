package fiek.unipr.mostwantedapp.dashboard;

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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.fragment.admin.AboutFragment;
import fiek.unipr.mostwantedapp.fragment.admin.AccountFragment;
import fiek.unipr.mostwantedapp.fragment.admin.HelpFragment;
import fiek.unipr.mostwantedapp.fragment.admin.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.admin.NotificationFragment;
import fiek.unipr.mostwantedapp.fragment.admin.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.admin.SearchFragment;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.SettingsFragment;
import fiek.unipr.mostwantedapp.fragment.user.ProfileDashboardFragment;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;

public class AdminDashboardActivity extends AppCompatActivity {

    public static final String ADMIN_INFORMER_PREFS = "ADMIN_INFORMER_PREFS";
    private DrawerLayout admin_drawerLayout_real;
    private ActionBarDrawerToggle admin_toggle;
    private Toolbar admin_toolbar;
    private NavigationView admin_nav_view;
    private Button admin_menu_group_logout;
    private ProgressBar admin_logout_progressBar;

    //nav header
    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view;

    public static final String PREFS_NAME = "LOG_PREF";
    public static final String LOGIN_INFORMER_PREFS = "loginInformerPreferences";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private String user_anonymousID = null;
    Integer balance;
    String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    Uri photoURL;

    //num of selected tab. We have 4 tabs so value must lie between 1-4. Default value is 1, cause first tab is selected by deafult
    private int selectedTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        final LinearLayout admin_homeLayout = findViewById(R.id.admin_homeLayout);
        final LinearLayout admin_searchLayout = findViewById(R.id.admin_searchLayout);
        final LinearLayout admin_notificationLayout = findViewById(R.id.admin_notificationLayout);
        final LinearLayout admin_profileLayout = findViewById(R.id.admin_profileLayout);

        final ImageView admin_homeImage = findViewById(R.id.admin_homeImage);
        final ImageView admin_searchImage = findViewById(R.id.admin_searchImage);
        final ImageView admin_notificationImage = findViewById(R.id.admin_notificationImage);
        final ImageView admin_profileImage = findViewById(R.id.admin_profileImage);

        final TextView admin_homeTxt = findViewById(R.id.admin_homeTxt);
        final TextView admin_searchTxt = findViewById(R.id.admin_searchTxt);
        final TextView admin_notificationTxt = findViewById(R.id.admin_notificationTxt);
        final TextView admin_profileTxt = findViewById(R.id.admin_profileTxt);

        //nav
        admin_drawerLayout_real = findViewById(R.id.admin_drawerLayout_real);
        admin_logout_progressBar = findViewById(R.id.admin_logout_progressBar);
        admin_toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(admin_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        admin_toolbar.setTitle("");
        admin_toolbar.setSubtitle("");
        //toolbar.setLogo(R.drawable.ic_toolbar);

        admin_toggle = new ActionBarDrawerToggle(AdminDashboardActivity.this, admin_drawerLayout_real, admin_toolbar, R.string.open, R.string.close);
        admin_drawerLayout_real.addDrawerListener(admin_toggle);
        admin_toggle.syncState();
        admin_nav_view = findViewById(R.id.admin_nav_view);
        admin_menu_group_logout = findViewById(R.id.admin_menu_group_logout);

        //Get All Nav header to use elements like textview and any...
        View headerView = admin_nav_view.getHeaderView(0);
        nav_header_name = headerView.findViewById(R.id.nav_header_name);
        verifiedBadge = headerView.findViewById(R.id.verifiedBadge);
        nav_header_image_view = headerView.findViewById(R.id.nav_header_image_view);
        //Get All Nav header to use elements like textview and any...
        //nav

        //set home fragment by default
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.admin_fragmentContainer, HomeFragment.class, null)
                .commit();

        //set deafult seleted
        admin_nav_view.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer, new HomeFragment()).commit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(AdminDashboardActivity.this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(AdminDashboardActivity.this);
        if(account != null){
            personal_number = null;
            balance = 0;
            name = account.getGivenName();
            lastname = account.getFamilyName();
            fullName = account.getDisplayName();
            address = null;
            email = account.getEmail();
            parentName = null;
            grade = "E";
            photoURL = account.getPhotoUrl();
            googleID = account.getId();

            nav_header_name.setText(fullName);
        }

        admin_menu_group_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout(account);
            }
        });

        admin_nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                switch (id)
                {
                    case R.id.admin_menu_group_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_account:
                        fragment = new AccountFragment();
                        loadFragment(fragment);
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_settings:
                        fragment = new SettingsFragment();
                        loadFragment(fragment);
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_help:
                        fragment = new HelpFragment();
                        loadFragment(fragment);
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_about:
                        fragment = new AboutFragment();
                        loadFragment(fragment);
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_logout:
                        admin_logout_progressBar.setVisibility(View.VISIBLE);
                        Logout(account);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        admin_homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if home is already selected or not.
                if(selectedTab != 1) {
                    //set home fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, HomeFragment.class, null)
                            .commit();

                    //unselect other tabs except home tab
                    admin_searchTxt.setVisibility(View.GONE);
                    admin_notificationTxt.setVisibility(View.GONE);
                    admin_profileTxt.setVisibility(View.GONE);

                    admin_searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    admin_notificationImage.setImageResource(R.drawable.ic_notification);
                    admin_profileImage.setImageResource(R.drawable.ic_user);

                    admin_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    admin_homeTxt.setVisibility(View.VISIBLE);
                    admin_homeImage.setImageResource(R.drawable.ic_selected_home);
                    admin_homeLayout.setBackgroundResource(R.drawable.round_back_home_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    admin_homeLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 1;
                }
            }
        });

        admin_searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if search is already selected or not.
                if(selectedTab != 2) {
                    //set search fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, SearchFragment.class, null)
                            .commit();

                    //unselect other tabs except search tab
                    admin_homeTxt.setVisibility(View.GONE);
                    admin_notificationTxt.setVisibility(View.GONE);
                    admin_profileTxt.setVisibility(View.GONE);

                    admin_homeImage.setImageResource(R.drawable.ic_home_);
                    admin_notificationImage.setImageResource(R.drawable.ic_notification);
                    admin_profileImage.setImageResource(R.drawable.ic_user);

                    admin_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    admin_searchTxt.setVisibility(View.VISIBLE);
                    admin_searchImage.setImageResource(R.drawable.ic_selected_search);
                    admin_searchLayout.setBackgroundResource(R.drawable.round_back_search_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    admin_searchLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 2;
                }
            }
        });

        admin_notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if notification is already selected or not.
                if(selectedTab != 3) {
                    //set notification fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, NotificationFragment.class, null)
                            .commit();

                    //unselect other tabs except notification tab
                    admin_homeTxt.setVisibility(View.GONE);
                    admin_searchTxt.setVisibility(View.GONE);
                    admin_profileTxt.setVisibility(View.GONE);

                    admin_homeImage.setImageResource(R.drawable.ic_home_);
                    admin_searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    admin_profileImage.setImageResource(R.drawable.ic_user);

                    admin_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    admin_notificationTxt.setVisibility(View.VISIBLE);
                    admin_notificationImage.setImageResource(R.drawable.ic_selected_notification);
                    admin_notificationLayout.setBackgroundResource(R.drawable.round_back_notification_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    admin_notificationLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 3;
                }
            }
        });

        admin_profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if profile is already selected or not.
                if(selectedTab != 4) {
                    //set profile fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, ProfileFragment.class, null)
                            .commit();

                    //unselect other tabs except profile tab
                    admin_homeTxt.setVisibility(View.GONE);
                    admin_searchTxt.setVisibility(View.GONE);
                    admin_notificationTxt.setVisibility(View.GONE);

                    admin_homeImage.setImageResource(R.drawable.ic_home_);
                    admin_searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    admin_notificationImage.setImageResource(R.drawable.ic_notification);

                    admin_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    admin_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    admin_profileTxt.setVisibility(View.VISIBLE);
                    admin_profileImage.setImageResource(R.drawable.ic_selected_user);
                    admin_profileLayout.setBackgroundResource(R.drawable.round_back_profile_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    admin_profileLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 4;
                }
            }
        });

    }


    //functions

    private void Logout(GoogleSignInAccount account) {
        //check for google login
        if(account != null){
            SignOut();
        }

        //check for phone authentication
        if(firebaseAuth != null){
            firebaseAuth.signOut();
            sendUserToLogin();
        }
        admin_logout_progressBar.setVisibility(View.GONE);
    }

    public void sendUserToLogin() {
        Intent loginIntent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        admin_drawerLayout_real.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(AdminDashboardActivity.this)){
            return false;
        }else {
            return true;
        }
    }

    private void setVerifiedBadge(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            verifiedBadge.setVisibility(View.VISIBLE);
        }
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    public void setSharedPreference(String phone) {
        SharedPreferences settings = getSharedPreferences(ADMIN_INFORMER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(AdminDashboardActivity.this);
        if (account != null) {
            String fullName = account.getDisplayName();
            nav_header_name.setText(fullName);
            nav_header_image_view.setImageURI(account.getPhotoUrl());
        }
        if(firebaseAuth != null){
            loadInfoFromFirebase(firebaseAuth);
            loadInfoAnonymousFirebase();
            loadInfoPhoneFirebase();
        }
    }

    //firebase logical function

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        fullName = task.getResult().getString("fullName");
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        //set Image, verified if is email verified, name
                        setVerifiedBadge(firebaseAuth.getCurrentUser());
                        if(urlOfProfile != null){
                            Picasso.get().load(urlOfProfile).into(nav_header_image_view);
                        }

                        if(fullName != null){
                            nav_header_name.setText(fullName);
                        }
                    }
                }
            });
        }
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            nav_header_name.setText(firebaseAuth.getCurrentUser().getUid());
            nav_header_image_view.setImageResource(R.drawable.ic_anonymous);
        }
    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            //logged in with phone
            nav_header_name.setText(phone);
            setSharedPreference(phone);
            nav_header_image_view.setImageResource(R.drawable.ic_phone_login);
        }
    }

}