package fiek.unipr.mostwantedapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;

public class ProfileFragment extends Fragment {

    private View profile_fragment_view;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView nav_view;
    private ImageView addReport;
    private Button menu_group_logout;

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
    private TextView nav_header_name;
    private ImageView verifiedBadge;
    private CircleImageView nav_header_image_view;
    private String user_anonymousID = null;
    Integer balance;
    String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    Uri photoURL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profile_fragment_view = inflater.inflate(R.layout.fragment_profile, container, false);

        drawerLayout = profile_fragment_view.findViewById(R.id.drawerLayout);
        toolbar = profile_fragment_view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view = profile_fragment_view.findViewById(R.id.nav_view);
        menu_group_logout = profile_fragment_view.findViewById(R.id.menu_group_logout);

        //Get All Nav header to use elements like textview and any...
        View headerView = nav_view.getHeaderView(0);
        nav_header_name = headerView.findViewById(R.id.nav_header_name);
        verifiedBadge = headerView.findViewById(R.id.verifiedBadge);
        nav_header_image_view = headerView.findViewById(R.id.nav_header_image_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        //set deafult seleted
        nav_view.getMenu().getItem(0).setChecked(true);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_to_change, new ProfileDashboardFragment()).commit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(getContext(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
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

        menu_group_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout(account);
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                switch (id)
                {
                    case R.id.menu_group_account:
                        fragment = new AccountFragment();
                        loadFragment(fragment);
                        nav_view.setCheckedItem(id);
                        break;
                    case R.id.menu_group_home:
                        fragment = new ProfileDashboardFragment();
                        loadFragment(fragment);
                        nav_view.setCheckedItem(id);
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

        return profile_fragment_view;
    }

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

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(getContext())){
            return false;
        }else {
            return true;
        }
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
    public void onStart() {
        super.onStart();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
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

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_to_change, fragment);
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            //logged in with phone
            nav_header_name.setText(phone);
            nav_header_image_view.setImageResource(R.drawable.ic_phone_login);
        }
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            nav_header_name.setText(firebaseAuth.getCurrentUser().getUid());
            nav_header_image_view.setImageResource(R.drawable.ic_anonymous);
        }
    }


    public void sendUserToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        getActivity().finish();
    }

    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
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

}