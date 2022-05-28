package fiek.unipr.mostwantedapp.helpers;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import fiek.unipr.mostwantedapp.RegisterUserActivity;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.InformerDashboardActivity;

public class SetImageActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView circle_profile_picture;
    private Button btn_skip, btn_setProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private FirebaseStorage firebaseStorage;
    private ProgressBar setprofile_progressBar1, setprofile_progressBar2, skip_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_image);
        circle_profile_picture = findViewById(R.id.circle_profile_picture);
        btn_skip = findViewById(R.id.btn_skip);
        btn_skip.setOnClickListener(this);
        btn_setProfile = findViewById(R.id.btn_setProfile);
        btn_setProfile.setOnClickListener(this);
        setprofile_progressBar1 = findViewById(R.id.setprofile_progressBar1);
        setprofile_progressBar2 = findViewById(R.id.setprofile_progressBar2);
        skip_progressBar = findViewById(R.id.skip_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_skip:
                goToInformerDashboard();
                break;
            case R.id.btn_setProfile:
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 2000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                setprofile_progressBar1.setVisibility(View.VISIBLE);
                setprofile_progressBar2.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void goToInformerDashboard() {
        skip_progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(SetImageActivity.this, InformerDashboardActivity.class);
        startActivity(intent);
        skip_progressBar.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    private void uploadImageToFirebase(Uri imageUri) {

        //upload image to storage in firebase
        StorageReference profRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile_picture.jpg");
        profRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(circle_profile_picture);
                        documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                        documentReference.update("urlOfProfile", uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task != null && task.isSuccessful()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setprofile_progressBar1.setVisibility(View.INVISIBLE);
                                            setprofile_progressBar2.setVisibility(View.INVISIBLE);
                                            finish();
                                        }
                                    }, 3000);
                                    goToInformerDashboard();
                                }else {
                                    Toast.makeText(SetImageActivity.this, "No upload image!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetImageActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                        setprofile_progressBar1.setVisibility(View.INVISIBLE);
                        setprofile_progressBar2.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    private void loadImg() {
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile_picture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(circle_profile_picture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetImageActivity.this, R.string.image_not_set, Toast.LENGTH_SHORT).show();
            }
        });
    }
}