package fiek.unipr.mostwantedapp.fragment.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;

public class ProfileFragment extends Fragment {

    private View admin_account_fragment_view;
    private Button admin_btnUploadNewPicture, admin_btnDeletePhoto, admin_btnSaveChanges;
    private CircleImageView admin_imageOfProfile;
    private EditText admin_et_first_name, admin_et_parent_name, admin_et_last_name, admin_et_address_account, admin_et_personal_number, admin_et_phone_number_account,
            admin_et_email_account;
    private ProgressBar admin_saveChangesProgressBar;

    private String uID;
    private String informer_fullName;
    private String first_name, last_name, parent_name, address, personal_number, phone_number, email, fullName;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private ProgressBar admin_acc_uploadProgressBar;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();
        getSetUserData(uID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_account_fragment_view = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();

        admin_btnUploadNewPicture = admin_account_fragment_view.findViewById(R.id.admin_btnUploadNewPicture);
        admin_btnDeletePhoto = admin_account_fragment_view.findViewById(R.id.admin_btnDeletePhoto);
        admin_btnSaveChanges = admin_account_fragment_view.findViewById(R.id.admin_btnSaveChanges);
        admin_imageOfProfile = admin_account_fragment_view.findViewById(R.id.admin_imageOfProfile);
        admin_et_first_name = admin_account_fragment_view.findViewById(R.id.admin_et_first_name);
        admin_et_parent_name = admin_account_fragment_view.findViewById(R.id.admin_et_parent_name);
        admin_et_last_name = admin_account_fragment_view.findViewById(R.id.admin_et_last_name);
        admin_et_address_account = admin_account_fragment_view.findViewById(R.id.admin_et_address_account);
        admin_et_personal_number = admin_account_fragment_view.findViewById(R.id.admin_et_personal_number);
        admin_et_phone_number_account = admin_account_fragment_view.findViewById(R.id.admin_et_phone_number_account);
        admin_et_email_account = admin_account_fragment_view.findViewById(R.id.admin_et_email_account);
        admin_saveChangesProgressBar = admin_account_fragment_view.findViewById(R.id.admin_saveChangesProgressBar);
        admin_acc_uploadProgressBar = admin_account_fragment_view.findViewById(R.id.admin_acc_uploadProgressBar);

        loadImage(uID);
        getSetUserData(uID);

        final SwipeRefreshLayout pullToRefreshInHome = admin_account_fragment_view.findViewById(R.id.admin_swipeToRefreshProfile);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSetUserData(uID);
                pullToRefreshInHome.setRefreshing(false);
            }
        });

        admin_btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin_saveChangesProgressBar.setVisibility(View.VISIBLE);
                update();
            }
        });

        admin_btnUploadNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        admin_btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin_acc_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        return admin_account_fragment_view;
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile_picture.jpg");
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                admin_acc_uploadProgressBar.setVisibility(View.GONE);
                admin_imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(getContext(), R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                admin_acc_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfUserExist(String uid) {
        firebaseFirestore.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult() != null){
                            getFullNameOfUser(task.getResult().getString("fullName"));
                            Toast.makeText(getContext(), informer_fullName+" "+R.string.user_exist, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), R.string.user_not_exist, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        admin_acc_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile_picture.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(admin_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                        docRef.update("urlOfProfile", uri.toString());
                        admin_acc_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                admin_acc_uploadProgressBar.setVisibility(View.GONE);
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

    private String getFullNameOfUser(String full_name) {
        informer_fullName = full_name;
        return informer_fullName;
    }

    private void loadImage(String uID) {
        admin_acc_uploadProgressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child("users/" + uID + "/profile_picture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(admin_imageOfProfile);
                admin_acc_uploadProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSetUserData(String uID) {
        firebaseFirestore.collection("users").document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null) {
                            first_name = documentSnapshot.getString("name");
                            parent_name = documentSnapshot.getString("parentName");
                            last_name = documentSnapshot.getString("lastname");
                            address = documentSnapshot.getString("address");
                            personal_number = documentSnapshot.getString("personal_number");
                            phone_number = documentSnapshot.getString("phone");
                            email = documentSnapshot.getString("email");

                            admin_et_first_name.setText(documentSnapshot.getString("name"));
                            admin_et_parent_name.setText(documentSnapshot.getString("parentName"));
                            admin_et_last_name.setText(documentSnapshot.getString("lastname"));
                            admin_et_address_account.setText(documentSnapshot.getString("address"));
                            admin_et_personal_number.setText(documentSnapshot.getString("personal_number"));
                            admin_et_phone_number_account.setText(documentSnapshot.getString("phone"));
                            admin_et_email_account.setText(documentSnapshot.getString("email"));
                        } else {
                            if(firebaseAuth.getCurrentUser().isAnonymous())
                            {
                                first_name = "Anonymous";
                                last_name = "Anonymous";
                                parent_name = "Anonymous";
                                address = "Anonymous";
                                personal_number = "Anonymous";
                                phone_number = "Anonymous";
                                email = "Anonymous";
                                fullName = "Anonymous";
                            }else {
                                first_name = "Anonymous";
                                last_name = "Anonymous";
                                parent_name = "Anonymous";
                                address = "Anonymous";
                                personal_number = "Anonymous";
                                phone_number = firebaseAuth.getCurrentUser().getPhoneNumber();
                                email = "Anonymous";
                                fullName = "Anonymous";
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isFirstNameChanged() {
        if(!first_name.equals(admin_et_first_name.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_first_name.getText().toString())){
                admin_et_first_name.setError(getText(R.string.error_fullname_required));
                admin_et_first_name.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("name", admin_et_first_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isLastNameChanged() {
        if(!last_name.equals(admin_et_last_name.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_last_name.getText().toString())){
                admin_et_last_name.setError(getText(R.string.error_last_name_required));
                admin_et_last_name.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("lastname", admin_et_last_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isFullNameChanged() {
        String fullNameFromUser = admin_et_first_name.getText().toString() + admin_et_last_name.getText().toString();
        if(!fullName.equals(fullNameFromUser))
        {
            DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            docRef.update("fullName", fullNameFromUser);
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isParentNameChanged() {
        if(!parent_name.equals(admin_et_parent_name.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_parent_name.getText().toString())){
                admin_et_parent_name.setError(getText(R.string.error_parent_name_required));
                admin_et_parent_name.requestFocus();
                return false;
            }else{
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("parentName", admin_et_parent_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if(!address.equals(admin_et_address_account.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_address_account.getText().toString())){
                admin_et_address_account.setError(getText(R.string.error_address_required));
                admin_et_address_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("address", admin_et_address_account.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPersonalNumberChanged() {
        if(!personal_number.equals(admin_et_personal_number.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_personal_number.getText().toString())){
                admin_et_personal_number.setError(getText(R.string.error_number_personal_required));
                admin_et_personal_number.requestFocus();
                return false;
            }else if(admin_et_personal_number.length()>10){
                admin_et_personal_number.setError(getText(R.string.error_number_personal_is_ten_digit));
                admin_et_personal_number.requestFocus();
                return false;
            }else if(admin_et_personal_number.length()<10){
                admin_et_personal_number.setError(getText(R.string.error_number_personal_less_than_ten_digits));
                admin_et_personal_number.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("personal_number", admin_et_personal_number.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPhoneNumberChanged() {
        if(!phone_number.equals(admin_et_phone_number_account.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_phone_number_account.getText().toString())){
                admin_et_phone_number_account.setError(getText(R.string.error_phone_required));
                admin_et_phone_number_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("phone", admin_et_phone_number_account.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if(!email.equals(admin_et_email_account.getText().toString()))
        {
            if(TextUtils.isEmpty(admin_et_email_account.getText().toString())){
                admin_et_email_account.setError(getText(R.string.error_email_required));
                admin_et_email_account.requestFocus();
                return false;
            }else if(!admin_et_email_account.getText().toString().matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
                admin_et_email_account.setError(getText(R.string.error_validate_email));
                admin_et_email_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                docRef.update("email", admin_et_email_account.getText().toString());
                return true;
            }

        }else
        {
            return false;
        }
    }

    private void update() {

        try {
            if(isFirstNameChanged()
                    || isLastNameChanged()
                    || isParentNameChanged()
                    || isAddressChanged()
                    || isPersonalNumberChanged()
                    || isPhoneNumberChanged()
                    || isEmailChanged()
                    || isFullNameChanged()){
                admin_saveChangesProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                getSetUserData(uID);
            }
        }catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}