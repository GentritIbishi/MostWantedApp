package fiek.unipr.mostwantedapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "userProfilePreference";
    private String role;
    private String fullName;
    private String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    TextView tv_real_fullName, tv_real_email, tv_real_role;
    ImageView img_profile;
    Button bt_edit_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_real_fullName = findViewById(R.id.tv_real_fullName);
        tv_real_email = findViewById(R.id.tv_real_email);
        tv_real_role = findViewById(R.id.tv_real_role);
        img_profile = findViewById(R.id.img_profile);
        bt_edit_data = findViewById(R.id.bt_edit_data);
        bt_edit_data.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(img_profile);
            }
        });

        // kthej preferencat
        SharedPreferences settings = getSharedPreferences("loginPreferences", 0);
        role = settings.getString("Role", "");
        fullName = settings.getString("fullName", "");
        email = settings.getString("email", "");

        ((Activity) Profile.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) tv_real_fullName).setText(fullName);
                ((TextView) tv_real_email).setText(email);
                ((TextView) tv_real_role).setText(role);
            }
        });

    }

//        bt_changeProfileImage.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // qyty me bo upload profile image
//            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(openGalleryIntent, 1000);
//        }
//    });
//
//        bt_logout.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            FirebaseAuth.getInstance().signOut();
//            Intent profileSignout = new Intent(Profile.this, LoginActivity.class);
//            profileSignout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(profileSignout);
//            finish();
//        }
//    });

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
                        Picasso.get().load(uri).into(img_profile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_edit_data:
                startActivity(new Intent(Profile.this, UpdateUser.class));
                break;
        }
    }
}