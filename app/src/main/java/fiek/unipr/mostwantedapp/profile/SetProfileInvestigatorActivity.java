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

public class SetProfileInvestigatorActivity extends AppCompatActivity {

    private String investigator_id, fullName;

    private CircleImageView investigator_ProfileView;
    private ImageView investigator_setNewProfile;
    private TextView investigator_tv_addprofile;
    private ProgressBar investigator_progressBarPerson;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_investigator);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        investigator_ProfileView = findViewById(R.id.investigator_ProfileView);
        investigator_setNewProfile = findViewById(R.id.investigator_setNewProfile);
        investigator_tv_addprofile = findViewById(R.id.investigator_tv_addprofile);
        investigator_progressBarPerson = findViewById(R.id.investigator_progressBarPerson);

        getFromBundle(bundle);

        StorageReference profileRef = storageReference.child("investigators/"+ investigator_id +"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(investigator_ProfileView);
            }
        });

        investigator_setNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }

    private void getFromBundle(Bundle bundle) {
        bundle = getIntent().getExtras();
        if(bundle != null)
        {
            investigator_id = bundle.get("investigator_id").toString();
            fullName = bundle.get("fullName").toString();
        }
        else
        {
            investigator_id = null;
            fullName = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                investigator_progressBarPerson.setVisibility(View.VISIBLE);
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
        StorageReference fileRef = storageReference.child("investigators/"+ investigator_id +"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(investigator_ProfileView);
                        DocumentReference docRef = firebaseFirestore.collection("investigators").document(investigator_id);
                        docRef.update("urlOfProfile", uri.toString());
                        investigator_tv_addprofile.setText(R.string.profile_picture_added);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                investigator_progressBarPerson.setVisibility(View.GONE);
                                finish();
                            }
                        }, 5000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetProfileInvestigatorActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                        investigator_progressBarPerson.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

}