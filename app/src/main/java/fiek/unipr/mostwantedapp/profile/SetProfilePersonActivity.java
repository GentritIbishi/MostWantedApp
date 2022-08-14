package fiek.unipr.mostwantedapp.profile;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;

public class SetProfilePersonActivity extends AppCompatActivity {

    private String fullName;
    private String personId;

    private CircleImageView personProfileView;
    private ImageView setNewProfile;
    private TextView tv_addprofile;
    private ProgressBar progressBarPerson;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_image);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        personProfileView = findViewById(R.id.personProfileView);
        setNewProfile = findViewById(R.id.setNewProfile);
        tv_addprofile = findViewById(R.id.tv_addprofile);
        progressBarPerson = findViewById(R.id.progressBarPerson);

        Bundle personImage = getIntent().getExtras();
        if(personImage != null)
        {
            fullName = personImage.get("fullName").toString();
            personId = personImage.get("personId").toString();
        }
        else
        {
            fullName = null;
            personId = null;
        }

        StorageReference profileRef = storageReference.child("persons/"+ personId +"/"+fullName +"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(personProfileView);
            }
        });

        setNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                progressBarPerson.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    private void uploadImageToFirebase(Uri imageUri) {

            //upload image to storage in firebase
            StorageReference fileRef = storageReference.child("persons/"+ personId +"/"+fullName +"/profile.jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).transform(new CircleTransform()).into(personProfileView);
                            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(personId);
                            docRef.update("urlOfProfile", uri.toString());
                            tv_addprofile.setText(R.string.profile_picture_added);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarPerson.setVisibility(View.GONE);
                                    finish();
                                }
                            }, 5000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetProfilePersonActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                    progressBarPerson.setVisibility(View.GONE);
                }
            });
            }
        });
    }

}