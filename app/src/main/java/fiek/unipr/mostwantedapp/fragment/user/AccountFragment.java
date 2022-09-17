package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.helpers.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.helpers.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.helpers.Constants.USERS;

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

public class AccountFragment extends Fragment {

    private View account_fragment_view;
    private Button btnUploadNewPicture, btnDeletePhoto, btnSaveChanges;
    private CircleImageView imageOfProfile;
    private EditText et_first_name, et_parent_name, et_last_name, et_address_account, et_personal_number, et_phone_number_account,
            et_email_account;
    private ProgressBar saveChangesProgressBar;

    private String uID;
    private String informer_fullName;
    private String first_name, last_name, parent_name, address, personal_number, phone_number, email, fullName;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private ProgressBar acc_uploadProgressBar;

    public AccountFragment() {

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
        account_fragment_view = inflater.inflate(R.layout.fragment_account_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();

        btnUploadNewPicture = account_fragment_view.findViewById(R.id.btnUploadNewPicture);
        btnDeletePhoto = account_fragment_view.findViewById(R.id.btnDeletePhoto);
        btnSaveChanges = account_fragment_view.findViewById(R.id.btnSaveChanges);
        imageOfProfile = account_fragment_view.findViewById(R.id.imageOfProfile);
        et_first_name = account_fragment_view.findViewById(R.id.et_first_name);
        et_parent_name = account_fragment_view.findViewById(R.id.et_parent_name);
        et_last_name = account_fragment_view.findViewById(R.id.et_last_name);
        et_address_account = account_fragment_view.findViewById(R.id.et_address_account);
        et_personal_number = account_fragment_view.findViewById(R.id.et_personal_number);
        et_phone_number_account = account_fragment_view.findViewById(R.id.et_phone_number_account);
        et_email_account = account_fragment_view.findViewById(R.id.et_email_account);
        saveChangesProgressBar = account_fragment_view.findViewById(R.id.saveChangesProgressBar);
        acc_uploadProgressBar = account_fragment_view.findViewById(R.id.acc_uploadProgressBar);

        loadImage(uID);
        getSetUserData(uID);

        final SwipeRefreshLayout pullToRefreshInHome = account_fragment_view.findViewById(R.id.swipeToRefreshProfile);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSetUserData(uID);
                pullToRefreshInHome.setRefreshing(false);
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangesProgressBar.setVisibility(View.VISIBLE);
                update();
            }
        });

        btnUploadNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acc_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        return account_fragment_view;
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                acc_uploadProgressBar.setVisibility(View.GONE);
                imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(getContext(), R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                acc_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        acc_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                        docRef.update("urlOfProfile", uri.toString());
                        acc_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.images_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                acc_uploadProgressBar.setVisibility(View.GONE);
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
        acc_uploadProgressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child(USERS+"/" + uID + "/"+PROFILE_PICTURE);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(imageOfProfile);
                acc_uploadProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSetUserData(String uID) {
        firebaseFirestore.collection(USERS).document(uID)
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

                            et_first_name.setText(documentSnapshot.getString("name"));
                            et_parent_name.setText(documentSnapshot.getString("parentName"));
                            et_last_name.setText(documentSnapshot.getString("lastname"));
                            et_address_account.setText(documentSnapshot.getString("address"));
                            et_personal_number.setText(documentSnapshot.getString("personal_number"));
                            et_phone_number_account.setText(documentSnapshot.getString("phone"));
                            et_email_account.setText(documentSnapshot.getString("email"));
                        } else {
                            if(firebaseAuth.getCurrentUser().isAnonymous())
                            {
                                first_name = ANONYMOUS;
                                last_name = ANONYMOUS;
                                parent_name = ANONYMOUS;
                                address = ANONYMOUS;
                                personal_number = ANONYMOUS;
                                phone_number = ANONYMOUS;
                                email = ANONYMOUS;
                                fullName = ANONYMOUS;
                            }else {
                                first_name = ANONYMOUS;
                                last_name = ANONYMOUS;
                                parent_name = ANONYMOUS;
                                address = ANONYMOUS;
                                personal_number = ANONYMOUS;
                                phone_number = firebaseAuth.getCurrentUser().getPhoneNumber();
                                email = ANONYMOUS;
                                fullName = ANONYMOUS;
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
        if(!first_name.equals(et_first_name.getText().toString()))
        {
            if(TextUtils.isEmpty(et_first_name.getText().toString())){
                et_first_name.setError(getText(R.string.error_fullname_required));
                et_first_name.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("name", et_first_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isLastNameChanged() {
        if(!last_name.equals(et_last_name.getText().toString()))
        {
            if(TextUtils.isEmpty(et_last_name.getText().toString())){
                et_last_name.setError(getText(R.string.error_last_name_required));
                et_last_name.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("lastname", et_last_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isFullNameChanged() {
        String fullNameFromUser = et_first_name.getText().toString() + et_last_name.getText().toString();
        if(!fullName.equals(fullNameFromUser))
        {
            DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
            docRef.update("fullName", fullNameFromUser);
            return true;
        }else
        {
            return false;
        }
    }

    private boolean isParentNameChanged() {
        if(!parent_name.equals(et_parent_name.getText().toString()))
        {
            if(TextUtils.isEmpty(et_parent_name.getText().toString())){
                et_parent_name.setError(getText(R.string.error_parent_name_required));
                et_parent_name.requestFocus();
                return false;
            }else{
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("parentName", et_parent_name.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if(!address.equals(et_address_account.getText().toString()))
        {
            if(TextUtils.isEmpty(et_address_account.getText().toString())){
                et_address_account.setError(getText(R.string.error_address_required));
                et_address_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("address", et_address_account.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPersonalNumberChanged() {
        if(!personal_number.equals(et_personal_number.getText().toString()))
        {
            if(TextUtils.isEmpty(et_personal_number.getText().toString())){
                et_personal_number.setError(getText(R.string.error_number_personal_required));
                et_personal_number.requestFocus();
                return false;
            }else if(et_personal_number.length()>10){
                et_personal_number.setError(getText(R.string.error_number_personal_is_ten_digit));
                et_personal_number.requestFocus();
                return false;
            }else if(et_personal_number.length()<10){
                et_personal_number.setError(getText(R.string.error_number_personal_less_than_ten_digits));
                et_personal_number.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("personal_number", et_personal_number.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPhoneNumberChanged() {
        if(!phone_number.equals(et_phone_number_account.getText().toString()))
        {
            if(TextUtils.isEmpty(et_phone_number_account.getText().toString())){
                et_phone_number_account.setError(getText(R.string.error_phone_required));
                et_phone_number_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("phone", et_phone_number_account.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if(!email.equals(et_email_account.getText().toString()))
        {
            if(TextUtils.isEmpty(et_email_account.getText().toString())){
                et_email_account.setError(getText(R.string.error_email_required));
                et_email_account.requestFocus();
                return false;
            }else if(!et_email_account.getText().toString().matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
                et_email_account.setError(getText(R.string.error_validate_email));
                et_email_account.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
                docRef.update("email", et_email_account.getText().toString());
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
                saveChangesProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                getSetUserData(uID);
            }
        }catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}