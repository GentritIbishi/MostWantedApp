package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.register.RegisterPersonActivity;
import fiek.unipr.mostwantedapp.register.RegisterUserActivity;
import fiek.unipr.mostwantedapp.register.RegisterUsersActivity;

public class HomeFragment extends Fragment {

    private View admin_dashboard_view;
    private CircleImageView imageOfAccount;
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

    private Integer balance;
    private String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        loadInfoAnonymousFirebase();
        loadInfoFromFirebase(firebaseAuth);
        loadInfoPhoneFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_dashboard_view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        imageOfAccount = admin_dashboard_view.findViewById(R.id.imageOfAccount);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        loadInfoAnonymousFirebase();
        loadInfoFromFirebase(firebaseAuth);
        loadInfoPhoneFirebase();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(getActivity(), gso);

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

            imageOfAccount.setImageURI(photoURL);
        }

        final LinearLayout l_admin_myAccount = admin_dashboard_view.findViewById(R.id.l_admin_myAccount);
        final GridLayout gridLayout = admin_dashboard_view.findViewById(R.id.gridLayout);
        final View registerUsers = admin_dashboard_view.findViewById(R.id.registerUsers);

        final ConstraintLayout constraintPU = registerUsers.findViewById(R.id.constraintPU);
        final ConstraintLayout constraintWP = registerUsers.findViewById(R.id.constraintWP);

        constraintPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ni regjister te ri mas miri, me role Super-Admin, Admin, User(informer)
                Intent intent = new Intent(getContext(), RegisterUsersActivity.class);
                startActivity(intent);
            }
        });

        constraintWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterPersonActivity.class);
                startActivity(intent);
            }
        });


        l_admin_myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AccountFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_register = admin_dashboard_view.findViewById(R.id.l_admin_register);
        l_admin_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridLayout.setVisibility(View.GONE);
                registerUsers.setVisibility(View.VISIBLE);
            }
        });

        final LinearLayout l_admin_analytics = admin_dashboard_view.findViewById(R.id.l_admin_analytics);
        l_admin_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ProfileFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_locationReports = admin_dashboard_view.findViewById(R.id.l_admin_locationReports);
        l_admin_locationReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LocationReportsFragment();
                loadFragment(fragment);
            }
        });

        return admin_dashboard_view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            imageOfAccount.setImageResource(R.drawable.ic_anonymous);
        }
    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            imageOfAccount.setImageResource(R.drawable.ic_phone_login);
        }
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        if(urlOfProfile != null){
                            Picasso.get().load(urlOfProfile).into(imageOfAccount);
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

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

}