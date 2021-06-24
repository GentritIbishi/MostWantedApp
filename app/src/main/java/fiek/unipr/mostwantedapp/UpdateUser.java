package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends AppCompatActivity {

    public static final String PREFS_NAME = "updatePreference";
    String role;
    String fullName;
    String email;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private EditText et_user_fullname_update, et_user_email_update;
    private Button btn_update_user;
    private ProgressBar progressBarUpdate;
    private ImageView imgUser_update, img_plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        et_user_fullname_update = findViewById(R.id.et_user_fullname_update);
        et_user_email_update = findViewById(R.id.et_user_email_update);
        btn_update_user = findViewById(R.id.btn_update_user);
        progressBarUpdate = findViewById(R.id.progressBarUpdate);
        imgUser_update = findViewById(R.id.imgUser_update);
        img_plus = findViewById(R.id.img_plus);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(imgUser_update);
            }
        });

        btn_update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarUpdate.setVisibility(View.VISIBLE);
                        progressBarUpdate.setProgress(100);
                    }
                });
                Updateprofile();
            }
        });

        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent, 1000);
            }
        });
    }

    private void Updateprofile() {

        if(isEmailChanged() || isFullNameChanged())
        {
            Toast.makeText(UpdateUser.this, R.string.Data_success_updated, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UpdateUser.this, LoginActivity.class);
                    progressBarUpdate.setVisibility(View.GONE);
                    startActivity(intent);
                    finish();
                }
            }, 5000);

        }

    }

    private boolean isFullNameChanged() {
        if(!email.equals(et_user_fullname_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            docRef.update("fullName", et_user_fullname_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isEmailChanged() {
        progressBarUpdate.setVisibility(View.VISIBLE);
        if(!email.equals(et_user_email_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            docRef.update("email", et_user_email_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProfile();
    }

    private void setProfile() {

        documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null)
                {
                    role = task.getResult().getString("role");
                    fullName = task.getResult().getString("fullName");
                    email = task.getResult().getString("email");

                    et_user_email_update.setText(email);
                    et_user_fullname_update.setText(fullName);
                }
                else
                {
                    Toast.makeText(UpdateUser.this, "Profile not set!", Toast.LENGTH_LONG).show();
                    progressBarUpdate.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(imgUser_update);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateUser.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
            }
        });
    }
}