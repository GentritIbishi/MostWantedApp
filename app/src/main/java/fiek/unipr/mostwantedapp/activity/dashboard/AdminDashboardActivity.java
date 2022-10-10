package fiek.unipr.mostwantedapp.activity.dashboard;

import static android.content.ContentValues.TAG;
import static fiek.unipr.mostwantedapp.utils.Constants.FOREGROUND_SERVICE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.activity.LoginActivity;
import fiek.unipr.mostwantedapp.fragment.admin.AboutFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchLocationReportsFragment;
import fiek.unipr.mostwantedapp.fragment.admin.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.admin.HelpFragment;
import fiek.unipr.mostwantedapp.fragment.admin.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.admin.NotificationFragment;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.SettingsFragment;
import fiek.unipr.mostwantedapp.models.Notifications;
import fiek.unipr.mostwantedapp.models.NotificationState;
import fiek.unipr.mostwantedapp.services.ServiceNotification;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.PayoutsPaypalTask;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class AdminDashboardActivity extends AppCompatActivity {

    public static final String IS_LOGGED = "IS_LOGGED";
    private int selectedTab = 1;
    private String fullName, urlOfProfile;

    private DrawerLayout admin_drawerLayout_real;
    private ActionBarDrawerToggle admin_toggle;
    private Toolbar admin_toolbar;
    private NavigationView admin_nav_view;
    private Button admin_menu_group_logout;
    private ProgressBar admin_logout_progressBar;
    private ImageView admin_homeImage, admin_searchImage, admin_notificationImage, admin_profileImage;
    private LinearLayout admin_homeLayout, admin_searchLayout, admin_notificationLayout, admin_profileLayout;

    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view, topImageProfile;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        getPermissionForNotification();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        admin_nav_view = findViewById(R.id.admin_nav_view);
        View nav_header_view = admin_nav_view.getHeaderView(0);
        nav_header_name = nav_header_view.findViewById(R.id.nav_header_name);
        verifiedBadge = nav_header_view.findViewById(R.id.verifiedBadge);
        nav_header_image_view = nav_header_view.findViewById(R.id.nav_header_image_view);

        admin_homeLayout = findViewById(R.id.admin_homeLayout);
        admin_searchLayout = findViewById(R.id.admin_searchLayout);
        admin_notificationLayout = findViewById(R.id.admin_notificationLayout);
        admin_profileLayout = findViewById(R.id.admin_profileLayout);

        admin_homeImage = findViewById(R.id.admin_homeImage);
        admin_searchImage = findViewById(R.id.admin_searchImage);
        admin_notificationImage = findViewById(R.id.admin_notificationImage);
        admin_profileImage = findViewById(R.id.admin_profileImage);

        admin_drawerLayout_real = findViewById(R.id.admin_drawerLayout_real);
        admin_logout_progressBar = findViewById(R.id.admin_logout_progressBar);
        admin_toolbar = findViewById(R.id.admin_toolbar);
        topImageProfile = findViewById(R.id.topImageProfile);
        admin_menu_group_logout = findViewById(R.id.admin_menu_group_logout);

        setHomeDefaultConfig();

        Query query = firebaseFirestore.collection(LOCATION_REPORTS);
        registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            String notificationReportId = dc.getDocument().getString("docId");
                            String notificationReportUid = dc.getDocument().getString("uID");
                            String notificationReportDateTime = dc.getDocument().getString("date_time");
                            String notificationReportTitle = dc.getDocument().getString("title");
                            String notificationReportDescription = dc.getDocument().getString("description");
                            String notificationReportInformerPerson = dc.getDocument().getString("informer_person");
                            String notificationReportWantedPerson = dc.getDocument().getString("wanted_person");
                            String notificationReportPrizeToWin = dc.getDocument().getString("prizeToWin");
                            String notificationReportNewStatus = dc.getDocument().getString("status");

                            Notifications notificationsAdded = new Notifications(
                                    DateHelper.getDateTime(),
                                    String.valueOf(NotificationState.ADDED),
                                    notificationReportId,
                                    notificationReportUid,
                                    notificationReportDateTime,
                                    notificationReportTitle,
                                    notificationReportDescription,
                                    notificationReportInformerPerson,
                                    notificationReportWantedPerson,
                                    notificationReportPrizeToWin,
                                    notificationReportNewStatus,
                                    firebaseAuth.getUid()
                            );

                            Notifications notificationsModified = new Notifications(
                                    DateHelper.getDateTime(),
                                    String.valueOf(NotificationState.MODIFIED),
                                    notificationReportId,
                                    notificationReportUid,
                                    notificationReportDateTime,
                                    notificationReportTitle,
                                    notificationReportDescription,
                                    notificationReportInformerPerson,
                                    notificationReportWantedPerson,
                                    notificationReportPrizeToWin,
                                    notificationReportNewStatus,
                                    firebaseAuth.getUid()
                            );

                            Notifications notificationsRemoved = new Notifications(
                                    DateHelper.getDateTime(),
                                    String.valueOf(NotificationState.REMOVED),
                                    notificationReportId,
                                    notificationReportUid,
                                    notificationReportDateTime,
                                    notificationReportTitle,
                                    notificationReportDescription,
                                    notificationReportInformerPerson,
                                    notificationReportWantedPerson,
                                    notificationReportPrizeToWin,
                                    notificationReportNewStatus,
                                    firebaseAuth.getUid()
                            );

                            switch (dc.getType()) {
                                case ADDED:
                                    final Intent intentAdded = new Intent(AdminDashboardActivity.this, ServiceNotification.class);
                                    ServiceCaller(intentAdded, notificationsAdded);
                                    break;
                                case MODIFIED:
                                    final Intent intentModified = new Intent(AdminDashboardActivity.this, ServiceNotification.class);
                                    ServiceCaller(intentModified, notificationsModified);
                                    break;
                                case REMOVED:
                                    final Intent intentRemoved = new Intent(AdminDashboardActivity.this, ServiceNotification.class);
                                    ServiceCaller(intentRemoved, notificationsRemoved);
                                    break;
                            }
                        }
                    }
                });

        admin_menu_group_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
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
                        setHomeSelected();
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        setProfileSelected();
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_settings:
                        fragment = new SettingsFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_help:
                        fragment = new HelpFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_about:
                        fragment = new AboutFragment();
                        loadFragment(fragment);
                        setHomeSelected();
                        admin_nav_view.setCheckedItem(id);
                        break;
                    case R.id.admin_menu_group_logout:
                        admin_logout_progressBar.setVisibility(View.VISIBLE);
                        Logout();
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
                if(selectedTab != 1){
                    setHomeSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, HomeFragment.class, null)
                            .commit();
                }
                if(selectedTab == 1){
                    setHomeSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, HomeFragment.class, null)
                            .commit();
                }
            }
        });

        admin_searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 2){
                    setSearchSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, SearchLocationReportsFragment.class, null)
                            .commit();
                }
                if(selectedTab == 2){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, SearchLocationReportsFragment.class, null)
                            .commit();
                }
            }
        });

        admin_notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 3){
                    setNotificationSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, NotificationFragment.class, null)
                            .commit();
                }
                if(selectedTab == 3){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, NotificationFragment.class, null)
                            .commit();
                }

            }
        });

        admin_profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 4){
                    setProfileSelected();
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, ProfileFragment.class, null)
                            .commit();
                }
                if(selectedTab == 4){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.admin_fragmentContainer, ProfileFragment.class, null)
                            .commit();
                }
            }
        });

    }

    private void setHomeDefaultConfig() {
        setSupportActionBar(admin_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        admin_toolbar.setTitle("");
        admin_toolbar.setSubtitle("");

        admin_toggle = new ActionBarDrawerToggle(AdminDashboardActivity.this, admin_drawerLayout_real, admin_toolbar, R.string.open, R.string.close);
        admin_drawerLayout_real.addDrawerListener(admin_toggle);
        admin_toggle.syncState();

        //set home fragment by default
        admin_homeImage.setImageResource(R.drawable.ic_home_selected);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.admin_fragmentContainer, HomeFragment.class, null)
                .commit();

        //set default selected
        admin_nav_view.getMenu().getItem(0).setChecked(true);
    }

    private void setHomeSelected() {
        admin_homeImage.setImageResource(R.drawable.ic_home_selected);
        admin_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        admin_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        admin_searchImage.setImageResource(R.drawable.ic_search_unselected);
        admin_nav_view.getMenu().getItem(0).setChecked(true);
    }

    private void setSearchSelected() {
        admin_searchImage.setImageResource(R.drawable.ic_search_selected);
        admin_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        admin_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        admin_homeImage.setImageResource(R.drawable.ic_home_unselected);
        unSelectAllItemInMenu();
    }

    private void setProfileSelected() {
        admin_profileImage.setImageResource(R.drawable.ic_profile_selected);
        admin_notificationImage.setImageResource(R.drawable.ic_notification_unselected);
        admin_searchImage.setImageResource(R.drawable.ic_search_unselected);
        admin_homeImage.setImageResource(R.drawable.ic_home_unselected);
        admin_nav_view.getMenu().getItem(1).setChecked(true);
    }

    private void setNotificationSelected() {
        admin_notificationImage.setImageResource(R.drawable.ic_notification_selected);
        admin_profileImage.setImageResource(R.drawable.ic_profile_unselected);
        admin_searchImage.setImageResource(R.drawable.ic_search_unselected);
        admin_homeImage.setImageResource(R.drawable.ic_home_unselected);
        unSelectAllItemInMenu();
    }

    private void unSelectAllItemInMenu() {
        for(int i = 0; i<admin_nav_view.getMenu().size();i++){
            admin_nav_view.getMenu().getItem(i).setChecked(false);
        }
    }

    private void Logout()
    {
        if(firebaseAuth != null){
            firebaseAuth.signOut();
            sendUserToLogin();
        }
        admin_logout_progressBar.setVisibility(View.GONE);
    }

    public void sendUserToLogin()
    {
        Intent loginIntent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        admin_drawerLayout_real.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setVerifiedBadge(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            verifiedBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            loadInfoFromFirebase(firebaseAuth);
        }
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(getApplicationContext())){
            documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        fullName = task.getResult().getString("fullName");
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        //set Image, verified if is email verified, name
                        setVerifiedBadge(firebaseAuth.getCurrentUser());
                        if(!urlOfProfile.isEmpty()){
                            Picasso.get().load(urlOfProfile).into(nav_header_image_view);
                            Picasso.get().load(urlOfProfile).into(topImageProfile);
                        }else {
                            nav_header_image_view.setImageResource(R.drawable.ic_profile_picture_default);
                            topImageProfile.setImageResource(R.drawable.ic_profile_picture_default);
                        }

                        if(fullName != null){
                            nav_header_name.setText(fullName);
                        }
                    }
                }
            });
        }
    }

    private void getPermissionForNotification() {
        ActivityCompat.requestPermissions(this, new String[] {FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
    }

    private void ServiceCaller(Intent intent, Notifications notifications) {
        stopService(intent);
        intent.putExtra("notificationDateTime", notifications.getNotificationDateTime());
        intent.putExtra("notificationType", notifications.getNotificationType());
        intent.putExtra("notificationReportId", notifications.getNotificationReportId());
        intent.putExtra("notificationReportUid", notifications.getNotificationReportUid());
        intent.putExtra("notificationReportDateTime", notifications.getNotificationReportDateTime());
        intent.putExtra("notificationReportTitle", notifications.getNotificationReportTitle());
        intent.putExtra("notificationReportDescription", notifications.getNotificationReportDescription());
        intent.putExtra("notificationReportInformerPerson", notifications.getNotificationReportInformerPerson());
        intent.putExtra("notificationReportWantedPerson", notifications.getNotificationReportWantedPerson());
        intent.putExtra("notificationReportPrizeToWin", notifications.getNotificationReportPrizeToWin());
        intent.putExtra("notificationReportNewStatus", notifications.getNotificationReportNewStatus());
        intent.putExtra("notificationForUserId", notifications.getNotificationForUserId());
        startService(intent);
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

    public void payoutPaypalRunTask() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);



        Timer time = new Timer(); // Instantiate Timer Object

        // Start running the task on Monday at 15:40:00, period is set to 8 hours
        // if you want to run the task immediately, set the 2nd parameter to 0
        time.schedule(new PayoutsPaypalTask(), calendar.getTime(), TimeUnit.MINUTES.toMillis(8));
    }

}