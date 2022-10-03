package fiek.unipr.mostwantedapp.activity;

import static fiek.unipr.mostwantedapp.utils.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.utils.CircleTransform;

public class SetProfileUserActivity extends AppCompatActivity {

    private String userID;

    private CircleImageView circleImageViewUserOutside;
    private ImageView ic_add_user_outside;
    private TextView tv_add_profile_user_outside;
    private ProgressBar progressBarUserOutside;
    private Button btnSkipSetImageOutside;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_user);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(this);

        initializeFields();

        StorageReference profileRef = storageReference.child(USERS+"/" + userID + "/"+PROFILE_PICTURE);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(circleImageViewUserOutside);
            }
        });

        ic_add_user_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        btnSkipSetImageOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage(getApplicationContext().getText(R.string.successfully_login_to_new_user));
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Intent intent = new Intent(SetProfileUserActivity.this, UserDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                        startActivity(intent);
                    }
                }, 4500);
            }
        });

    }

    private void initializeFields() {
        circleImageViewUserOutside = findViewById(R.id.circleImageViewUserOutside);
        ic_add_user_outside = findViewById(R.id.ic_add_user_outside);
        tv_add_profile_user_outside = findViewById(R.id.tv_addprofile_user_outside);
        progressBarUserOutside = findViewById(R.id.progressBarUserOutside);
        btnSkipSetImageOutside = findViewById(R.id.btnSkipSetImageOutside);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        progressDialog.setMessage(getApplicationContext().getText(R.string.loading));
        progressDialog.show();
        StorageReference profRef = storageReference.child(USERS+"/" + userID + "/"+PROFILE_PICTURE);
        profRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.show();
                        Picasso.get().load(uri).transform(new CircleTransform()).into(circleImageViewUserOutside);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(userID);
                        docRef.update("urlOfProfile", uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task != null && task.isSuccessful()) {
                                    progressDialog.setMessage(getApplicationContext().getText(R.string.profile_picture_added));
                                    tv_add_profile_user_outside.setText(getApplicationContext().getText(R.string.profile_picture_added));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBarUserOutside.setVisibility(View.GONE);
                                            progressDialog.setMessage(getApplicationContext().getText(R.string.successfully_login_to_new_user));
                                            Intent intent = new Intent(SetProfileUserActivity.this, UserDashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                        }
                                    }, 4500);
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage(e.getMessage());
                                progressDialog.dismiss();
                            }
                        }, 4500);
                        progressBarUserOutside.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

}