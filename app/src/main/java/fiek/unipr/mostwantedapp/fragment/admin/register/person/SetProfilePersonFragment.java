package fiek.unipr.mostwantedapp.fragment.admin.register.person;

import static fiek.unipr.mostwantedapp.helpers.Constants.PERSONS;
import static fiek.unipr.mostwantedapp.helpers.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.helpers.Constants.WANTED_PERSONS;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import fiek.unipr.mostwantedapp.fragment.admin.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.admin.RegisterFragment;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;

public class SetProfilePersonFragment extends Fragment {

    private View view;
    private String fullName;
    private String personId;

    private CircleImageView personProfileView;
    private ImageView setNewProfile;
    private TextView tv_addprofile;
    private ProgressBar progressBarPerson;
    private Button btnSkipSetProfilePerson;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private Bundle bundle;

    public SetProfilePersonFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_set_profile_person, container, false);

        personProfileView = view.findViewById(R.id.personProfileView);
        setNewProfile = view.findViewById(R.id.setNewProfile);
        tv_addprofile = view.findViewById(R.id.tv_addprofile);
        progressBarPerson = view.findViewById(R.id.progressBarPerson);
        btnSkipSetProfilePerson = view.findViewById(R.id.btnSkipSetProfilePerson);

        bundle = getArguments();
        getAndSetFromBundle(bundle);

        StorageReference profileRef = storageReference.child(PERSONS+"/"+ personId +"/"+PROFILE_PICTURE);
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

        btnSkipSetProfilePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegisterFragment();
                loadFragment(fragment);
            }
        });

        onBackPressed();

        return view;
    }

    private void getAndSetFromBundle(Bundle bundle) {
        if(bundle != null){
            personId = bundle.getString("personId");
            fullName = bundle.getString("fullName");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                progressBarPerson.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child(PERSONS+"/"+personId +"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(personProfileView);
                        DocumentReference docRef = firebaseFirestore.collection(WANTED_PERSONS).document(personId);
                        docRef.update("urlOfProfile", uri.toString());
                        tv_addprofile.setText(R.string.profile_picture_added);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBarPerson.setVisibility(View.GONE);
                                Fragment fragment = new HomeFragment();
                                loadFragment(fragment);
                            }
                        }, 5000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                        progressBarPerson.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void onBackPressed() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Toast.makeText(getContext(), getContext().getText(R.string.error_you_need_to_add_photo_for_person), Toast.LENGTH_SHORT).show();
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