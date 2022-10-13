package fiek.unipr.mostwantedapp.activity.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.LoginActivity;
import fiek.unipr.mostwantedapp.fragment.anonymous.MapsFragment;
import fiek.unipr.mostwantedapp.fragment.anonymous.SearchFragment;

public class AnonymousDashboardActivity extends AppCompatActivity {

    private Context mContext;
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
        mContext = getApplicationContext();
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
                .replace(R.id.anonymous_fragmentContainer, SearchFragment.class, null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        firebaseAuth.signOut();
        sendAnonymousToLogin();
        super.onBackPressed();
    }
}