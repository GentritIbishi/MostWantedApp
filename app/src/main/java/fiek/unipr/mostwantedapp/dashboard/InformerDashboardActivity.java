package fiek.unipr.mostwantedapp.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Informer;

public class InformerDashboardActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "LOG_PREF";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private String user_anonymousID = null;
    Button btnLogout;
    TextView tvEmail, tvName, tvPhone;
    Integer balance;
    String collection = "informers_by_google_sign_in";
    String fullName, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    Uri photoURL;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informer_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        btnLogout = findViewById(R.id.btnLogout);
        tvEmail = findViewById(R.id.tvPhone);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);

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

            tvName.setText(fullName);
            tvEmail.setText(email);
        }
        if(firebaseUser != null){
            phone = firebaseUser.getPhoneNumber();
            tvPhone.setText(phone);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for google login
                if(account != null){
                    SignOut();
                }

                //check for phone authentication
                if(firebaseAuth != null){
                    firebaseAuth.signOut();
                    sendUserToLogin();
                }

                //need to do login with random email like admin and user

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String fullName = account.getDisplayName();
            String email = account.getEmail();
            tvName.setText(fullName);
            tvEmail.setText(email);
        }

        if(firebaseUser == null){
            sendUserToLogin();
        }else {
            String phone = firebaseUser.getPhoneNumber();
            tvPhone.setText(phone);
        }

    }

    public void sendUserToLogin() {
        Intent loginIntent = new Intent(InformerDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void registerInformer(String collection, String googleID, Informer informer) {
        firebaseFirestore.collection(collection).document(googleID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    Toast.makeText(InformerDashboardActivity.this, InformerDashboardActivity.this.getText(R.string.welcome_back) + " " +fullName, Toast.LENGTH_LONG).show();
                }
                else
                {
                    firebaseFirestore.collection(collection).document(googleID).set(informer).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(InformerDashboardActivity.this, InformerDashboardActivity.this.getText(R.string.this_person_with_this)+" "+fullName+" "+InformerDashboardActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InformerDashboardActivity.this, R.string.person_failed_to_register, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}