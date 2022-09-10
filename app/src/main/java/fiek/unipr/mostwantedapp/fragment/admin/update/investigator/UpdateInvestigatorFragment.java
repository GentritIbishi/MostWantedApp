package fiek.unipr.mostwantedapp.fragment.admin.update.investigator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.helpers.DateInputMask;
import fiek.unipr.mostwantedapp.models.Investigator;

public class UpdateInvestigatorFragment extends Fragment {

    private View update_investigator_view;
    public static final String AGE = "AGE";
    public static final String KG = "KG";
    public static final String CM = "CM";
    private CircleImageView update_investigator_imageOfProfile;
    private Button update_investigator_btnUploadNewPicture, update_investigator_btnDeletePhoto, update_investigator_btnSaveChanges;
    private ProgressBar update_investigator_uploadProgressBar, update_investigator_saveChangesProgressBar;
    private TextInputEditText update_investigator_et_firstName, update_investigator_et_lastName, update_investigator_et_parentName,
            update_investigator_et_fullName, update_investigator_etAddress,
            update_investigator_etDateRegistration, update_investigator_et_birthday;
    private MaterialAutoCompleteTextView update_investigator_et_age_autocomplete, update_investigator_et_eye_color_autocomplete,
            update_investigator_et_hair_color_autocomplete, update_investigator_et_height_autocomplete,
            update_investigator_et_weight_autocomplete, update_investigator_et_phy_appearance_autocomplete,
            update_investigator_et_gender_autocomplete;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private String[] AGE_ARRAY = null;
    private String[] WEIGHT_ARRAY = null;
    private String[] HEIGHT_ARRAY = null;

    private String investigator_id, firstName, lastName, parentName, fullName, birthday, gender, address, age, eyeColor, hairColor, height,
            phy_appearance, urlOfProfile, weight, registration_date;

    private Bundle bundle;

    public UpdateInvestigatorFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_investigator_view = inflater.inflate(R.layout.fragment_update_investigator, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setWeightInMeasureArray(KG);
        setHeightInMeasureArray(CM);
        setAgeArray();

        update_investigator_imageOfProfile = update_investigator_view.findViewById(R.id.update_investigator_imageOfProfile);
        update_investigator_btnUploadNewPicture = update_investigator_view.findViewById(R.id.update_investigator_btnUploadNewPicture);
        update_investigator_btnDeletePhoto = update_investigator_view.findViewById(R.id.update_investigator_btnDeletePhoto);
        update_investigator_btnSaveChanges = update_investigator_view.findViewById(R.id.update_investigator_btnSaveChanges);
        update_investigator_uploadProgressBar = update_investigator_view.findViewById(R.id.update_investigator_uploadProgressBar);
        update_investigator_saveChangesProgressBar = update_investigator_view.findViewById(R.id.update_investigator_saveChangesProgressBar);
        update_investigator_et_firstName = update_investigator_view.findViewById(R.id.update_investigator_et_firstName);
        update_investigator_et_lastName = update_investigator_view.findViewById(R.id.update_investigator_et_lastName);
        update_investigator_et_parentName = update_investigator_view.findViewById(R.id.update_investigator_et_parentName);
        update_investigator_et_fullName = update_investigator_view.findViewById(R.id.update_investigator_et_fullName);
        update_investigator_et_birthday = update_investigator_view.findViewById(R.id.update_investigator_et_birthday);
        new DateInputMask(update_investigator_et_birthday);
        update_investigator_et_gender_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_gender_autocomplete);
        update_investigator_etAddress = update_investigator_view.findViewById(R.id.update_investigator_etAddress);
        update_investigator_etDateRegistration = update_investigator_view.findViewById(R.id.update_investigator_etDateRegistration);
        update_investigator_et_age_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_age_autocomplete);
        update_investigator_et_eye_color_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_eye_color_autocomplete);
        update_investigator_et_hair_color_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_hair_color_autocomplete);
        update_investigator_et_height_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_height_autocomplete);
        update_investigator_et_weight_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_weight_autocomplete);
        update_investigator_et_phy_appearance_autocomplete = update_investigator_view.findViewById(R.id.update_investigator_et_phy_appearance_autocomplete);

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        update_investigator_et_gender_autocomplete.setAdapter(gender_adapter);

        ArrayAdapter<String> age_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, AGE_ARRAY);
        update_investigator_et_age_autocomplete.setAdapter(age_adapter);

        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        update_investigator_et_eye_color_autocomplete.setAdapter(eye_color_adapter);

        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        update_investigator_et_hair_color_autocomplete.setAdapter(hair_color_adapter);

        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        update_investigator_et_phy_appearance_autocomplete.setAdapter(phy_appearance_adapter);

        ArrayAdapter<String> height_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        update_investigator_et_height_autocomplete.setAdapter(height_adapter);

        ArrayAdapter<String> weight_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        update_investigator_et_weight_autocomplete.setAdapter(weight_adapter);

        bundle = getArguments();
        getAndSetFromBundle(bundle);

        loadImage();

        final SwipeRefreshLayout update_investigator_swipeUpToRefresh = update_investigator_view.findViewById(R.id.update_investigator_swipeUpToRefresh);
        update_investigator_swipeUpToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataFromFirebase();
                update_investigator_swipeUpToRefresh.setRefreshing(false);
            }
        });

        update_investigator_btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_investigator_saveChangesProgressBar.setVisibility(View.VISIBLE);
                update();
            }
        });

        update_investigator_btnUploadNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        update_investigator_btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_investigator_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        onBackPressed();
        
        return update_investigator_view;
    }

    private void update() {
        String new_firstName = update_investigator_et_firstName.getText().toString();
        String new_lastName = update_investigator_et_lastName.getText().toString();
        String new_parentName = update_investigator_et_parentName.getText().toString();
        String new_fullName = update_investigator_et_fullName.getText().toString();
        String new_birthday = update_investigator_et_birthday.getText().toString();
        String new_gender = update_investigator_et_gender_autocomplete.getText().toString();
        String new_address = update_investigator_etAddress.getText().toString();
        String new_age = update_investigator_et_age_autocomplete.getText().toString();
        String new_eyeColor = update_investigator_et_eye_color_autocomplete.getText().toString();
        String new_hairColor = update_investigator_et_hair_color_autocomplete.getText().toString();
        String new_height = update_investigator_et_height_autocomplete.getText().toString();
        String new_weight = update_investigator_et_weight_autocomplete.getText().toString();
        String new_phy_appearance = update_investigator_et_phy_appearance_autocomplete.getText().toString();

        Investigator investigator = new Investigator(
                investigator_id,
                new_firstName,
                new_lastName,
                new_parentName,
                new_fullName,
                new_birthday,
                new_address,
                new_eyeColor,
                new_hairColor,
                new_phy_appearance,
                urlOfProfile,
                registration_date,
                new_age,
                new_gender,
                new_height,
                new_weight
        );
        firebaseFirestore.collection("investigators")
                .document(investigator_id)
                .set(investigator, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                        Fragment fragment = new UpdateInvestigatorListFragment();
                        loadFragment(fragment);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        update_investigator_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child("investigators/"+ investigator_id +"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(update_investigator_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection("investigators").document(investigator_id);
                        docRef.update("urlOfProfile", uri.toString());
                        update_investigator_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.images_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                update_investigator_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child("investigators/"+ investigator_id +"/profile.jpg");
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                update_investigator_uploadProgressBar.setVisibility(View.GONE);
                update_investigator_imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(getContext(), R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                update_investigator_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshDataFromFirebase() {
        firebaseFirestore.collection("investigators").document(investigator_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null) {
                            investigator_id = documentSnapshot.getString("investigator_id");

                            firstName = documentSnapshot.getString("firstName");
                            update_investigator_et_firstName.setText(firstName);

                            lastName = documentSnapshot.getString("lastName");
                            update_investigator_et_lastName.setText(lastName);

                            parentName = documentSnapshot.getString("parentName");
                            update_investigator_et_parentName.setText(parentName);

                            fullName = documentSnapshot.getString("fullName");
                            update_investigator_et_fullName.setText(fullName);

                            birthday = documentSnapshot.getString("birthday");
                            update_investigator_et_birthday.setText(birthday);

                            gender = documentSnapshot.getString("gender");
                            update_investigator_et_gender_autocomplete.setText(gender);

                            address = documentSnapshot.getString("address");
                            update_investigator_etAddress.setText(address);

                            age = documentSnapshot.getString("age");
                            update_investigator_et_age_autocomplete.setText(age);

                            eyeColor = documentSnapshot.getString("eyeColor");
                            update_investigator_et_eye_color_autocomplete.setText(eyeColor);

                            hairColor = documentSnapshot.getString("hairColor");
                            update_investigator_et_hair_color_autocomplete.setText(hairColor);

                            height = documentSnapshot.getString("height");
                            update_investigator_et_height_autocomplete.setText(height);

                            weight = documentSnapshot.getString("weight");
                            update_investigator_et_weight_autocomplete.setText(weight);

                            phy_appearance = documentSnapshot.getString("phy_appearance");
                            update_investigator_et_phy_appearance_autocomplete.setText(phy_appearance);

                            registration_date = documentSnapshot.getString("registration_date");
                            update_investigator_etDateRegistration.setText(registration_date);

                            urlOfProfile = documentSnapshot.getString("urlOfProfile");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadImage() {
        update_investigator_uploadProgressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child("investigators/"+ investigator_id +"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(update_investigator_imageOfProfile);
                update_investigator_uploadProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        update_investigator_et_gender_autocomplete.setAdapter(gender_adapter);

        ArrayAdapter<String> age_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, AGE_ARRAY);
        update_investigator_et_age_autocomplete.setAdapter(age_adapter);

        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        update_investigator_et_eye_color_autocomplete.setAdapter(eye_color_adapter);

        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        update_investigator_et_hair_color_autocomplete.setAdapter(hair_color_adapter);

        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        update_investigator_et_phy_appearance_autocomplete.setAdapter(phy_appearance_adapter);

        ArrayAdapter<String> height_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        update_investigator_et_height_autocomplete.setAdapter(height_adapter);

        ArrayAdapter<String> weight_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        update_investigator_et_weight_autocomplete.setAdapter(weight_adapter);
    }

    private void getAndSetFromBundle(Bundle bundle) {
        if(bundle != null){
            investigator_id = bundle.getString("investigator_id");

            firstName = bundle.getString("firstName");
            update_investigator_et_firstName.setText(firstName);

            lastName = bundle.getString("lastName");
            update_investigator_et_lastName.setText(lastName);

            parentName = bundle.getString("parentName");
            update_investigator_et_parentName.setText(parentName);

            fullName = bundle.getString("fullName");
            update_investigator_et_fullName.setText(fullName);

            birthday = bundle.getString("birthday");
            update_investigator_et_birthday.setText(birthday);

            gender = bundle.getString("gender");
            update_investigator_et_gender_autocomplete.setText(gender);

            address = bundle.getString("address");
            update_investigator_etAddress.setText(address);

            age = bundle.getString("age");
            update_investigator_et_age_autocomplete.setText(age);

            eyeColor = bundle.getString("eyeColor");
            update_investigator_et_eye_color_autocomplete.setText(eyeColor);

            hairColor = bundle.getString("hairColor");
            update_investigator_et_hair_color_autocomplete.setText(hairColor);

            height = bundle.getString("height");
            update_investigator_et_height_autocomplete.setText(height);

            weight = bundle.getString("weight");
            update_investigator_et_weight_autocomplete.setText(weight);

            phy_appearance = bundle.getString("phy_appearance");
            update_investigator_et_phy_appearance_autocomplete.setText(phy_appearance);

            registration_date = bundle.getString("registration_date");
            update_investigator_etDateRegistration.setText(registration_date);

            urlOfProfile = bundle.getString("urlOfProfile");
        }
    }

    private void setWeightInMeasureArray(String measure) {
        WEIGHT_ARRAY = new String[201];
        for(int i=0; i<201; i++) {
            WEIGHT_ARRAY[i] = i+" "+measure;
        }
    }

    private void setHeightInMeasureArray(String measure) {
        HEIGHT_ARRAY = new String[273];
        for(int i=0; i<273; i++) {
            HEIGHT_ARRAY[i] = i+" "+measure;
        }
    }

    private void setAgeArray() {
        AGE_ARRAY = new String[121];
        for(int i=0; i<121; i++) {
            AGE_ARRAY[i] = i+" "+AGE;
        }
    }

    private void onBackPressed() {
        update_investigator_view.setFocusableInTouchMode(true);
        update_investigator_view.requestFocus();
        update_investigator_view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Fragment fragment = new UpdateInvestigatorListFragment();
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .commit();
    }
    
}