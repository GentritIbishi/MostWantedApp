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
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.user.AboutFragment;
import fiek.unipr.mostwantedapp.fragment.user.AccountFragment;
import fiek.unipr.mostwantedapp.fragment.user.HelpFragment;
import fiek.unipr.mostwantedapp.fragment.user.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.user.NotificationFragment;
import fiek.unipr.mostwantedapp.fragment.user.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.user.SearchFragment;
import fiek.unipr.mostwantedapp.fragment.user.SettingsFragment;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;

public class UserDashboardActivity extends AppCompatActivity {

    public static final String USER_INFORMER_PREFS = "USER_INFORMER_PREFS";
    private DrawerLayout user_drawerLayout_real;
    private ActionBarDrawerToggle user_toggle;
    private Toolbar user_toolbar;
    private NavigationView user_nav_view;
    private Button user_menu_group_logout;
    private ProgressBar user_logout_progressBar;

    //nav header
    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view, topImageProfile;

    public static final String PREFS_NAME = "LOG_PREF";
    public static final String LOGIN_INFORMER_PREFS = "loginInformerPreferences";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String user_anonymousID = null;

    private Integer balance;
    private String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;

    //num of selected tab. We have 4 tabs so value must lie between 1-4. Default value is 1, cause first tab is selected by deafult
    private int selectedTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        final LinearLayout user_homeLayout = findViewById(R.id.user_homeLayout);
        final LinearLayout user_searchLayout = findViewById(R.id.user_searchLayout);
        final LinearLayout user_notificationLayout = findViewById(R.id.user_notificationLayout);
        final LinearLayout user_profileLayout = findViewById(R.id.user_profileLayout);

        final ImageView user_homeImage = findViewById(R.id.user_homeImage);
        final ImageView user_searchImage = findViewById(R.id.user_searchImage);
        final ImageView user_notificationImage = findViewById(R.id.user_notificationImage);
        final ImageView user_profileImage = findViewById(R.id.user_profileImage);

        final TextView user_homeTxt = findViewById(R.id.user_homeTxt);
        final TextView user_searchTxt = findViewById(R.id.user_searchTxt);
        final TextView user_notificationTxt = findViewById(R.id.user_notificationTxt);
        final TextView user_profileTxt = findViewById(R.id.user_profileTxt);

        //nav
        user_drawerLayout_real = findViewById(R.id.user_drawerLayout_real);
        user_logout_progressBar = findViewById(R.id.user_logout_progressBar);
        user_toolbar = findViewById(R.id.user_toolbar);
        topImageProfile = findViewById(R.id.topImageProfile);
        setSupportActionBar(user_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        user_toolbar.setTitle("");
        user_toolbar.setSubtitle("");
        //toolbar.setLogo(R.drawable.ic_toolbar);

        user_toggle = new ActionBarDrawerToggle(UserDashboardActivity.this, user_drawerLayout_real, user_toolbar, R.string.open, R.string.close);
        user_drawerLayout_real.addDrawerListener(user_toggle);
        user_toggle.syncState();
        user_nav_view = findViewById(R.id.user_nav_view);
        user_menu_group_logout = findViewById(R.id.user_menu_group_logout);

        //Get All Nav header to use elements like textview and any...
        View headerView = user_nav_view.getHeaderView(0);
        nav_header_name = headerView.findViewById(R.id.nav_header_name);
        verifiedBadge = headerView.findViewById(R.id.verifiedBadge);
        nav_header_image_view = headerView.findViewById(R.id.nav_header_image_view);
        //Get All Nav header to use elements like textview and any...
        //nav

        //set home fragment by default
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.user_fragmentContainer, HomeFragment.class, null)
                .commit();

        //set deafult seleted
        user_nav_view.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.user_fragmentContainer, new HomeFragment()).commit();

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
                switch (id)
                {
                    case R.id.user_menu_group_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_account:
                        fragment = new AccountFragment();
                        loadFragment(fragment);
                        //unselect all tabs
                        user_homeTxt.setVisibility(View.GONE);
                        user_searchTxt.setVisibility(View.GONE);
                        user_notificationTxt.setVisibility(View.GONE);
                        user_profileTxt.setVisibility(View.GONE);

                        user_homeImage.setImageResource(R.drawable.ic_home_);
                        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                        user_profileImage.setImageResource(R.drawable.ic_user);

                        user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        //unselect all tabs
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_settings:
                        fragment = new SettingsFragment();
                        loadFragment(fragment);
                        //unselect all tabs
                        user_homeTxt.setVisibility(View.GONE);
                        user_searchTxt.setVisibility(View.GONE);
                        user_notificationTxt.setVisibility(View.GONE);
                        user_profileTxt.setVisibility(View.GONE);

                        user_homeImage.setImageResource(R.drawable.ic_home_);
                        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                        user_profileImage.setImageResource(R.drawable.ic_user);

                        user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        //unselect all tabs
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_help:
                        fragment = new HelpFragment();
                        loadFragment(fragment);
                        //unselect all tabs
                        user_homeTxt.setVisibility(View.GONE);
                        user_searchTxt.setVisibility(View.GONE);
                        user_notificationTxt.setVisibility(View.GONE);
                        user_profileTxt.setVisibility(View.GONE);

                        user_homeImage.setImageResource(R.drawable.ic_home_);
                        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                        user_profileImage.setImageResource(R.drawable.ic_user);

                        user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        //unselect all tabs
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_about:
                        fragment = new AboutFragment();
                        loadFragment(fragment);
                        //unselect all tabs
                        user_homeTxt.setVisibility(View.GONE);
                        user_searchTxt.setVisibility(View.GONE);
                        user_notificationTxt.setVisibility(View.GONE);
                        user_profileTxt.setVisibility(View.GONE);

                        user_homeImage.setImageResource(R.drawable.ic_home_);
                        user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                        user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                        user_profileImage.setImageResource(R.drawable.ic_user);

                        user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        //unselect all tabs
                        user_nav_view.setCheckedItem(id);
                        break;
                    case R.id.user_menu_group_logout:
                        user_logout_progressBar.setVisibility(View.VISIBLE);
                        Logout();
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
                //check if home is already selected or not.
                if(selectedTab != 1) {
                    //set home fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, HomeFragment.class, null)
                            .commit();

                    //unselect other tabs except home tab
                    user_searchTxt.setVisibility(View.GONE);
                    user_notificationTxt.setVisibility(View.GONE);
                    user_profileTxt.setVisibility(View.GONE);

                    user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                    user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                    user_profileImage.setImageResource(R.drawable.ic_user);

                    user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    user_homeTxt.setVisibility(View.VISIBLE);
                    user_homeImage.setImageResource(R.drawable.ic_selected_home);
                    user_homeLayout.setBackgroundResource(R.drawable.round_back_home_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    user_homeLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 1;
                }
            }
        });

        user_searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if search is already selected or not.
                if(selectedTab != 2) {
                    //set search fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, SearchFragment.class, null)
                            .commit();

                    //unselect other tabs except search tab
                    user_homeTxt.setVisibility(View.GONE);
                    user_notificationTxt.setVisibility(View.GONE);
                    user_profileTxt.setVisibility(View.GONE);

                    user_homeImage.setImageResource(R.drawable.ic_home_);
                    user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
                    user_profileImage.setImageResource(R.drawable.ic_user);

                    user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    user_searchTxt.setVisibility(View.VISIBLE);
                    user_searchImage.setImageResource(R.drawable.ic_selected_search);
                    user_searchLayout.setBackgroundResource(R.drawable.round_back_search_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    user_searchLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 2;
                }
            }
        });

        user_notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if notification is already selected or not.
                if(selectedTab != 3) {
                    //set notification fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, NotificationFragment.class, null)
                            .commit();

                    //unselect other tabs except notification tab
                    user_homeTxt.setVisibility(View.GONE);
                    user_searchTxt.setVisibility(View.GONE);
                    user_profileTxt.setVisibility(View.GONE);

                    user_homeImage.setImageResource(R.drawable.ic_home_);
                    user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                    user_profileImage.setImageResource(R.drawable.ic_user);

                    user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    user_notificationTxt.setVisibility(View.VISIBLE);
                    user_notificationImage.setImageResource(R.drawable.ic_selected_notification);
                    user_notificationLayout.setBackgroundResource(R.drawable.round_back_notification_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    user_notificationLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 3;
                }
            }
        });

        user_profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if profile is already selected or not.
                if(selectedTab != 4) {
                    //set profile fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.user_fragmentContainer, ProfileFragment.class, null)
                            .commit();

                    //unselect other tabs except profile tab
                    user_homeTxt.setVisibility(View.GONE);
                    user_searchTxt.setVisibility(View.GONE);
                    user_notificationTxt.setVisibility(View.GONE);

                    user_homeImage.setImageResource(R.drawable.ic_home_);
                    user_searchImage.setImageResource(R.drawable.ic_search_unselected);
                    user_notificationImage.setImageResource(R.drawable.ic_notification_unselected);

                    user_homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    user_notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    user_profileTxt.setVisibility(View.VISIBLE);
                    user_profileImage.setImageResource(R.drawable.ic_selected_user);
                    user_profileLayout.setBackgroundResource(R.drawable.round_back_profile_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    user_profileLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 4;
                }
            }
        });

    }

    private void Logout() {
        //check for phone authentication
        if(firebaseAuth != null){
            firebaseAuth.signOut();
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
        if(!checkInternet.isConnected(UserDashboardActivity.this)){
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
        SharedPreferences settings = getSharedPreferences(USER_INFORMER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
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
                            Picasso.get().load(urlOfProfile).into(topImageProfile);
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
            topImageProfile.setImageResource(R.drawable.ic_anonymous);
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
            topImageProfile.setImageResource(R.drawable.ic_phone_login);
        }
    }

}