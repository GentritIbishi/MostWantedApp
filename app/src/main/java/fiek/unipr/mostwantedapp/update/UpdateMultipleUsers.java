package fiek.unipr.mostwantedapp.update;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;

public class UpdateMultipleUsers extends AppCompatActivity {

    public String userID, fullName, email, role, urlOfProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    ImageView img_multipleUsers, img_plus_multipleUsers, img_delete_multipleUsers, img_update_multipleUsers;
    EditText et_user_fullname_multipleUsers, et_user_email_multipleUsers, et_user_role_multipleUsers;
    ProgressBar progressBarUpdate_multipleUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_multiple_users);

        img_multipleUsers = findViewById(R.id.img_multipleUsers);
        img_delete_multipleUsers = findViewById(R.id.img_delete_multipleUsers);
        img_plus_multipleUsers = findViewById(R.id.img_plus_multipleUsers);
        et_user_fullname_multipleUsers = findViewById(R.id.et_user_fullname_multipleUsers);
        et_user_email_multipleUsers = findViewById(R.id.et_user_email_multipleUsers);
        et_user_role_multipleUsers = findViewById(R.id.et_user_role_multipleUsers);
        img_update_multipleUsers = findViewById(R.id.img_update_multipleUsers);
        progressBarUpdate_multipleUsers = findViewById(R.id.progressBarUpdate_multipleUsers);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("users/"+userID+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(img_multipleUsers);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


        Bundle personInfos = getIntent().getExtras();
        if (personInfos != null) {
            fullName = personInfos.get("fullName").toString();
            userID = personInfos.get("userID").toString();
            email = personInfos.get("email").toString();
            role = personInfos.get("Role").toString();
            urlOfProfile = personInfos.get("urlOfProfile").toString();
            userID = personInfos.get("userID").toString();

        } else {
            fullName = null;
            userID = null;
            email = null;
            role = null;
            urlOfProfile = "";
        }

        img_update_multipleUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarUpdate_multipleUsers.setVisibility(View.VISIBLE);
                        progressBarUpdate_multipleUsers.setProgress(100);
                    }
                });
                progressBarUpdate_multipleUsers.setVisibility(View.GONE);
                Updateprofile();
            }
        });

        img_plus_multipleUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        img_delete_multipleUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarUpdate_multipleUsers.setVisibility(View.VISIBLE);
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateMultipleUsers.this, fullName+UpdateMultipleUsers.this.getText(R.string.person_delete_successfully), Toast.LENGTH_SHORT).show();
                        progressBarUpdate_multipleUsers.setVisibility(View.GONE);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateMultipleUsers.this, fullName+UpdateMultipleUsers.this.getText(R.string.person_delete_failed), Toast.LENGTH_SHORT).show();
                        progressBarUpdate_multipleUsers.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private void Updateprofile() {

        if(isFullNameChanged() || isEmailChanged() || isRoleChanged())
        {
            Toast.makeText(UpdateMultipleUsers.this, R.string.Data_success_updated, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarUpdate_multipleUsers.setVisibility(View.GONE);
                    finish();
                }
            }, 5000);

        }

    }

    private boolean isRoleChanged() {
        progressBarUpdate_multipleUsers.setVisibility(View.VISIBLE);
        if(!role.equals(et_user_role_multipleUsers.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
            docRef.update("Role", et_user_role_multipleUsers.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isFullNameChanged() {
        progressBarUpdate_multipleUsers.setVisibility(View.VISIBLE);
        if(!fullName.equals(et_user_fullname_multipleUsers.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
            docRef.update("fullName", et_user_fullname_multipleUsers.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isEmailChanged() {
        progressBarUpdate_multipleUsers.setVisibility(View.VISIBLE);
        if(!email.equals(et_user_email_multipleUsers.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
            docRef.update("email", et_user_email_multipleUsers.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private void setProfile() {

        et_user_fullname_multipleUsers.setText(fullName);
        et_user_email_multipleUsers.setText(email);
        et_user_role_multipleUsers.setText(role);
        Picasso.get().load(urlOfProfile).transform(new CircleTransform()).into(img_multipleUsers);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setProfile();
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
        StorageReference fileRef = storageReference.child("users/"+userID+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(img_multipleUsers);
                        DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                        docRef.update("urlOfProfile", uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMultipleUsers.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
            }
        });
    }
}