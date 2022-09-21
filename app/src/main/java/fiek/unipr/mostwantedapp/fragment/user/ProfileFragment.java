package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.PROFILE_USER_PREFS;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class ProfileFragment extends Fragment {

    private View profile_fragment_view;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private TextView user_nav_header_name;
    private ImageView user_verifiedBadge;
    private CircleImageView user_nav_header_image_view;
    private String user_anonymousID = null;
    private Integer balance;
    private String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;

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
        profile_fragment_view = inflater.inflate(R.layout.fragment_profile_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        user_nav_header_image_view = profile_fragment_view.findViewById(R.id.user_nav_header_image_view);
        user_nav_header_name = profile_fragment_view.findViewById(R.id.user_nav_header_name);
        user_verifiedBadge = profile_fragment_view.findViewById(R.id.user_verifiedBadge);

        final SwipeRefreshLayout pullToRefreshInSearch = profile_fragment_view.findViewById(R.id.user_profile_refresh);
        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                loadInfoFromFirebase(firebaseAuth);
                loadInfoPhoneFirebase();
                loadInfoAnonymousFirebase();
                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        return profile_fragment_view;
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
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
                            Picasso.get().load(urlOfProfile).into(user_nav_header_image_view);
                        }

                        if(fullName != null){
                            user_nav_header_name.setText(fullName);
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

    @Override
    public void onStart() {
        super.onStart();
            if(firebaseAuth != null){
                loadInfoFromFirebase(firebaseAuth);
                loadInfoAnonymousFirebase();
                loadInfoPhoneFirebase();
            }
        }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!StringHelper.empty(phone))
        {
            //logged in with phone
            user_nav_header_name.setText(phone);
            setSharedPreference(phone);
            user_nav_header_image_view.setImageResource(R.drawable.ic_phone_login);
        }
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            user_nav_header_name.setText(firebaseAuth.getCurrentUser().getUid());
            user_nav_header_image_view.setImageResource(R.drawable.ic_anonymous);
        }
    }

    public void setSharedPreference(String phone) {
        SharedPreferences settings = getActivity().getSharedPreferences(PROFILE_USER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    private void setVerifiedBadge(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            user_verifiedBadge.setVisibility(View.VISIBLE);
        }
    }

}