package fiek.unipr.mostwantedapp.fragment.admin.register.investigator;

import static fiek.unipr.mostwantedapp.helpers.Constants.INVESTIGATORS;
import static fiek.unipr.mostwantedapp.helpers.Constants.PROFILE_PICTURE;

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

public class SetProfileInvestigatorFragment extends Fragment {

    private View view;
    private String investigator_id, fullName;

    private CircleImageView investigator_ProfileView;
    private ImageView investigator_setNewProfile;
    private TextView investigator_tv_addprofile;
    private ProgressBar investigator_progressBarPerson;
    private Button btnSkipSetProfileInvestigator;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private Bundle bundle;

    public SetProfileInvestigatorFragment() {}

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
        view = inflater.inflate(R.layout.fragment_set_profile_investigator, container, false);

        investigator_ProfileView = view.findViewById(R.id.investigator_ProfileView);
        investigator_setNewProfile = view.findViewById(R.id.investigator_setNewProfile);
        investigator_tv_addprofile = view.findViewById(R.id.investigator_tv_addprofile);
        investigator_progressBarPerson = view.findViewById(R.id.investigator_progressBarPerson);
        btnSkipSetProfileInvestigator = view.findViewById(R.id.btnSkipSetProfileInvestigator);

        bundle = getArguments();
        getAndSetFromBundle(bundle);

        StorageReference profileRef = storageReference.child(INVESTIGATORS+"/"+ investigator_id +"/"+PROFILE_PICTURE);
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

        btnSkipSetProfileInvestigator.setOnClickListener(new View.OnClickListener() {
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
            investigator_id = bundle.getString("investigator_id");
            fullName = bundle.getString("fullName");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                investigator_progressBarPerson.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child(INVESTIGATORS+"/"+ investigator_id +"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(investigator_ProfileView);
                        DocumentReference docRef = firebaseFirestore.collection(INVESTIGATORS).document(investigator_id);
                        docRef.update("urlOfProfile", uri.toString());
                        investigator_tv_addprofile.setText(R.string.profile_picture_added);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                investigator_progressBarPerson.setVisibility(View.GONE);
                                Fragment fragment = new HomeFragment();
                                loadFragment(fragment);
                            }
                        }, 5000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                        investigator_progressBarPerson.setVisibility(View.GONE);
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
                    Toast.makeText(getContext(), getContext().getText(R.string.error_you_need_to_add_photo_for_investigator), Toast.LENGTH_SHORT).show();
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