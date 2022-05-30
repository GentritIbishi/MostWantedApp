package fiek.unipr.mostwantedapp.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import fiek.unipr.mostwantedapp.fragment.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.PersonFragment;
import fiek.unipr.mostwantedapp.fragment.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.SearchFragment;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.User;

public class InformerDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView nav_view;
    private ImageView addReport;

    public static final String PREFS_NAME = "LOG_PREF";
    public static final String LOGIN_INFORMER_PREFS = "loginInformerPreferences";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view;
    private String user_anonymousID = null;
    Integer balance;
    String collection = "informers_by_google_sign_in";
    String fullName, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    Uri photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informer_dashboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view = findViewById(R.id.nav_view);
        nav_view.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new HomeFragment()).commit();
        addReport = findViewById(R.id.addReport);

        //Get All Nav header to use elements like textview and any...
        View headerView = nav_view.getHeaderView(0);
        nav_header_name = headerView.findViewById(R.id.nav_header_name);
        verifiedBadge = headerView.findViewById(R.id.verifiedBadge);
        nav_header_image_view = headerView.findViewById(R.id.nav_header_image_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        user_anonymousID = getIntent().getStringExtra("uid_anonymous");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
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

        if(firebaseAuth != null){
            loadInformationFromFirebase(firebaseAuth);
        }


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                switch (id)
                {
                    case R.id.menu_group_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.menu_group_search:
                        fragment = new SearchFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.menu_group_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.menu_group_logout:
                        Logout(account);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        addReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InformerDashboardActivity.this, "Test clicked!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadInformationFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String fullName = task.getResult().getString("fullName");
                    String name = task.getResult().getString("name");
                    String lastname = task.getResult().getString("lastname");
                    String parentName = task.getResult().getString("parentName");
                    String personal_number = task.getResult().getString("personal_number");
                    String phone = task.getResult().getString("phone");
                    String urlOfProfile = task.getResult().getString("urlOfProfile");
                    String register_date_time = task.getResult().getString("register_date_time");
                    String address = task.getResult().getString("address");
//                    Integer balance = task.getResult().get
                    String email = task.getResult().getString("email");
                    String grade = task.getResult().getString("grade");

                    setVerifiedBadge(firebaseAuth.getCurrentUser());

                    storageReference = firebaseStorage.getReference().child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile_picture.jpg");
                    if(storageReference != null) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).transform(new CircleTransform()).into(nav_header_image_view);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InformerDashboardActivity.this, R.string.image_not_set, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    nav_header_name.setText(fullName);
                }
            });
        }
    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(this)){
            return false;
        }else {
            return true;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_home, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String fullName = account.getDisplayName();
            String email = account.getEmail();
            nav_header_name.setText(fullName);
        }

        if(firebaseUser == null){
            sendUserToLogin();
        }else {
            String phone = firebaseUser.getPhoneNumber();
            nav_header_name.setText(phone);
            loadInformationFromFirebase(firebaseAuth);
        }

    }

    public void sendUserToLogin() {
        Intent loginIntent = new Intent(InformerDashboardActivity.this, LoginActivity.class);
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
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void setVerifiedBadge(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            verifiedBadge.setVisibility(View.VISIBLE);
        }
    }

}