package fiek.unipr.mostwantedapp;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdatePerson extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    String fullName, address, eyeColor, hairColor, phy_appearance, acts, urlOfProfile, status;
    Integer age, height, weight;
    ImageView imgPerson_update, img_setNewProfile;
    EditText et_person_fullName_update, et_person_address_update, et_person_age_update, et_person_height_update, et_person_weight_update, et_person_eyeColor_update,
            et_person_hairColor_update, et_person_phy_appearance_update, et_person_acts_update, et_person_status_update;
    Button bt_updatePerson;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_person);

        imgPerson_update = findViewById(R.id.imgPerson_update);
        et_person_fullName_update = findViewById(R.id.et_person_fullName_update);
        et_person_address_update = findViewById(R.id.et_person_address_update);
        et_person_age_update = findViewById(R.id.et_person_age_update);
        et_person_height_update = findViewById(R.id.et_person_height_update);
        et_person_weight_update = findViewById(R.id.et_person_weight_update);
        et_person_eyeColor_update = findViewById(R.id.et_person_eyeColor_update);
        et_person_hairColor_update = findViewById(R.id.et_person_hairColor_update);
        et_person_phy_appearance_update = findViewById(R.id.et_person_phy_appearance_update);
        et_person_acts_update = findViewById(R.id.et_person_acts_update);
        et_person_status_update = findViewById(R.id.et_person_status_update);
        img_setNewProfile = findViewById(R.id.img_setNewProfile);
        bt_updatePerson = findViewById(R.id.bt_updatePerson);
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            //User logged in dhe button visible
        }
        else
        {
            //No User logged in dhe button gone
        }

        Bundle personInfos = getIntent().getExtras();
        if(personInfos != null)
        {
            fullName = personInfos.get("fullName").toString();
            acts = personInfos.get("acts").toString();
            address = personInfos.get("address").toString();
            age = personInfos.getInt("age");
            address = personInfos.get("address").toString();
            eyeColor = personInfos.get("eyeColor").toString();
            height = personInfos.getInt("height");
            phy_appearance = personInfos.get("phy_appearance").toString();
            status = personInfos.get("status").toString();
            urlOfProfile = personInfos.get("urlOfProfile").toString();
            weight = personInfos.getInt("weight");
            hairColor = personInfos.get("hairColor").toString();
        }
        else
        {
            fullName = null;
            acts = null;
            address = null;
            age = null;
            address = null;
            eyeColor = null;
            height = null;
            phy_appearance = null;
            status = null;
            urlOfProfile = null;
            weight = null;
            hairColor = null;
        }

        StorageReference profileRef = storageReference.child("persons/"+fullName+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(imgPerson_update);
            }
        });

        bt_updatePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(100);
                    }
                });
                Updateprofile();
            }
        });

        img_setNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }

    private void setProfile() {

        et_person_fullName_update.setText(fullName);
        et_person_acts_update.setText(acts);
        et_person_address_update.setText(address);
        et_person_eyeColor_update.setText(eyeColor);
        et_person_height_update.setText(String.valueOf(height));
        et_person_age_update.setText(String.valueOf(age));
        et_person_phy_appearance_update.setText(phy_appearance);
        et_person_status_update.setText(status);
        et_person_hairColor_update.setText(hairColor);
        et_person_weight_update.setText(String.valueOf(weight));
        Picasso.get().load(urlOfProfile).transform(new CircleTransform()).into(imgPerson_update);

    }

    private void Updateprofile() {

        if(isFullNameChanged() || isActsChanged() || isAddressChanged() || isEyeColorChanged() || isHeightChanged() || isAgeChanged()
        || isPhyAppearanceChanged() || isStatusChanged() || isHairColorChanged() || isWeightChanged())
        {
            Toast.makeText(UpdatePerson.this, R.string.Data_success_updated, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }, 5000);

        }

    }

    private boolean isWeightChanged() {
        if(!weight.equals(et_person_weight_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("weight", Integer.parseInt(et_person_weight_update.getText().toString()));
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isHairColorChanged() {
        if(!hairColor.equals(et_person_hairColor_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("hairColor", et_person_hairColor_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isStatusChanged() {
        if(!status.equals(et_person_status_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("status", et_person_status_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isPhyAppearanceChanged() {
        if(!phy_appearance.equals(et_person_phy_appearance_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("phy_appearance", et_person_phy_appearance_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isAgeChanged() {
        if(!age.equals(et_person_age_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("age", et_person_age_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isHeightChanged() {
        if(!height.equals(et_person_height_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("height", Integer.parseInt(et_person_height_update.getText().toString()));
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isEyeColorChanged() {
        if(!eyeColor.equals(et_person_eyeColor_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("eyeColor", et_person_eyeColor_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if(!address.equals(et_person_address_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("address", et_person_address_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isActsChanged() {
        if(!acts.equals(et_person_acts_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("acts", et_person_acts_update.getText().toString());
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isFullNameChanged() {
        if(!fullName.equals(et_person_fullName_update.getText().toString()))
        {
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("fullName", et_person_fullName_update.getText().toString());
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
        StorageReference fileRef = storageReference.child("persons/"+fullName+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(imgPerson_update);
                        DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                        docRef.update("urlOfProfile", uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdatePerson.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
            }
        });
    }

}