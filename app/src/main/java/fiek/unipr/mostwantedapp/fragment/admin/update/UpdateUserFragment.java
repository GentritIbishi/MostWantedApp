package fiek.unipr.mostwantedapp.fragment.admin.update;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
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

public class UpdateUserFragment extends Fragment {

    private View update_user_view;
    private String address, email, fullName, gender, lastname, name, parentName, password, personal_number, phone, grade,
            register_date_time, role, userID, urlOfProfile;
    private Integer balance, coins;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private CircleImageView update_user_imageOfProfile;
    private Button update_user_btnUploadNewPicture, update_user_btnDeletePhoto, update_user_btnSaveChanges;
    private TextInputEditText update_user_et_firstName, update_user_et_lastName,
            update_user_et_parentName, update_user_etPhone, update_user_etAddress, update_user_etNumPersonal,
            update_user_etEmailToUser, update_user_etPasswordToUser, update_user_et_fullName, update_user_etDateRegistration;
    private MaterialAutoCompleteTextView update_user_et_gender, update_user_et_role_autocomplete, update_user_et_coins_autocomplete,
            update_user_et_balance_autocomplete, update_user_et_grade_autocomplete;
    private ProgressBar update_user_saveChangesProgressBar, update_user_uploadProgressBar;
    private SwipeRefreshLayout update_user_swipeUpToRefresh;

    public UpdateUserFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_user_view = inflater.inflate(R.layout.fragment_update_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        update_user_imageOfProfile = update_user_view.findViewById(R.id.update_user_imageOfProfile);
        update_user_btnUploadNewPicture = update_user_view.findViewById(R.id.update_user_btnUploadNewPicture);
        update_user_btnDeletePhoto = update_user_view.findViewById(R.id.update_user_btnDeletePhoto);
        update_user_btnSaveChanges = update_user_view.findViewById(R.id.update_user_btnSaveChanges);
        update_user_et_firstName = update_user_view.findViewById(R.id.update_user_et_firstName);
        update_user_et_lastName = update_user_view.findViewById(R.id.update_user_et_lastName);
        update_user_et_parentName = update_user_view.findViewById(R.id.update_user_et_parentName);
        update_user_et_fullName = update_user_view.findViewById(R.id.update_user_et_fullName);
        update_user_etPhone = update_user_view.findViewById(R.id.update_user_etPhone);
        update_user_etAddress = update_user_view.findViewById(R.id.update_user_etAddress);
        update_user_etNumPersonal = update_user_view.findViewById(R.id.update_user_etNumPersonal);
        update_user_etEmailToUser = update_user_view.findViewById(R.id.update_user_etEmailToUser);
        update_user_etPasswordToUser = update_user_view.findViewById(R.id.update_user_etPasswordToUser);
        update_user_et_gender = update_user_view.findViewById(R.id.update_user_et_gender);
        update_user_et_role_autocomplete = update_user_view.findViewById(R.id.update_user_et_role_autocomplete);
        update_user_saveChangesProgressBar = update_user_view.findViewById(R.id.update_user_saveChangesProgressBar);
        update_user_uploadProgressBar = update_user_view.findViewById(R.id.update_user_uploadProgressBar);
        update_user_swipeUpToRefresh = update_user_view.findViewById(R.id.update_user_swipeUpToRefresh);
        update_user_et_coins_autocomplete = update_user_view.findViewById(R.id.update_user_et_coins_autocomplete);
        update_user_et_balance_autocomplete = update_user_view.findViewById(R.id.update_user_et_balance_autocomplete);
        update_user_et_grade_autocomplete = update_user_view.findViewById(R.id.update_user_et_grade_autocomplete);
        update_user_etDateRegistration = update_user_view.findViewById(R.id.update_user_etDateRegistration);

        Bundle bundle = getArguments();
        if(bundle != null){

            //no need to change
            userID = bundle.getString("userID");
            urlOfProfile = bundle.getString("urlOfProfile");

            address = bundle.getString("address");
            update_user_etAddress.setText(address);

            email = bundle.getString("email");
            update_user_etEmailToUser.setText(email);

            gender = bundle.getString("gender");
            update_user_et_gender.setText(gender);

            lastname = bundle.getString("lastname");
            update_user_et_lastName.setText(lastname);

            name = bundle.getString("name");
            update_user_et_firstName.setText(name);

            parentName = bundle.getString("parentName");
            update_user_et_parentName.setText(parentName);

            password = bundle.getString("password");
            update_user_etPasswordToUser.setText(password);

            personal_number = bundle.getString("personal_number");
            update_user_etNumPersonal.setText(personal_number);

            phone = bundle.getString("phone");
            update_user_etPhone.setText(phone);

            role = bundle.getString("role");
            update_user_et_role_autocomplete.setText(role);

            balance = bundle.getInt("balance");
            update_user_et_balance_autocomplete.setText(balance);

            coins = bundle.getInt("coins");
            update_user_et_coins_autocomplete.setText(coins);

            fullName = bundle.getString("fullName");
            update_user_et_fullName.setText(fullName);

            grade = bundle.getString("grade");
            update_user_et_grade_autocomplete.setText(grade);

            //no option for change by default disable
            register_date_time = bundle.getString("register_date_time");
            update_user_etDateRegistration.setText(register_date_time);
            update_user_etDateRegistration.setEnabled(false);


        }

        loadImage();

        final SwipeRefreshLayout update_user_swipeUpToRefresh = update_user_view.findViewById(R.id.update_user_swipeUpToRefresh);
        update_user_swipeUpToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataFromFirebase();
                update_user_swipeUpToRefresh.setRefreshing(false);
            }
        });

        update_user_btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_user_saveChangesProgressBar.setVisibility(View.VISIBLE);
                update();
            }
        });

        update_user_btnUploadNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        update_user_btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_user_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        return update_user_view;
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child("users/"+userID+"/profile_picture.jpg");
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                update_user_uploadProgressBar.setVisibility(View.GONE);
                update_user_imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(getContext(), R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                update_user_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        update_user_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child("users/"+userID+"/profile_picture.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(update_user_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                        docRef.update("urlOfProfile", uri.toString());
                        update_user_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.images_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                update_user_uploadProgressBar.setVisibility(View.GONE);
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

    private void loadImage() {
        update_user_uploadProgressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child("users/" + userID + "/profile_picture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(update_user_imageOfProfile);
                update_user_uploadProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isFirstNameChanged() {
        if(!name.equals(update_user_et_firstName.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_firstName.getText().toString())){
                update_user_et_firstName.setError(getText(R.string.error_fullname_required));
                update_user_et_firstName.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("name", update_user_et_firstName.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isLastNameChanged() {
        if(!lastname.equals(update_user_et_lastName.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_lastName.getText().toString())){
                update_user_et_lastName.setError(getText(R.string.error_last_name_required));
                update_user_et_lastName.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("lastname", update_user_et_lastName.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isFullNameChanged() {
        if(!fullName.equals(update_user_et_fullName.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_fullName.getText().toString())){
                update_user_et_fullName.setError(getText(R.string.error_fullname_required));
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("fullName", update_user_et_fullName.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isParentNameChanged() {
        if(!parentName.equals(update_user_et_parentName.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_parentName.getText().toString())){
                update_user_et_parentName.setError(getText(R.string.error_parent_name_required));
                update_user_et_parentName.requestFocus();
                return false;
            }else{
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("parentName", update_user_et_parentName.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if(!address.equals(update_user_etAddress.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_etAddress.getText().toString())){
                update_user_etAddress.setError(getText(R.string.error_address_required));
                update_user_etAddress.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("address", update_user_etAddress.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPersonalNumberChanged() {
        if(!personal_number.equals(update_user_etNumPersonal.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_etNumPersonal.getText().toString())){
                update_user_etNumPersonal.setError(getText(R.string.error_number_personal_required));
                update_user_etNumPersonal.requestFocus();
                return false;
            }else if(update_user_etNumPersonal.length()>10){
                update_user_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
                update_user_etNumPersonal.requestFocus();
                return false;
            }else if(update_user_etNumPersonal.length()<10){
                update_user_etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
                update_user_etNumPersonal.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("personal_number", update_user_etNumPersonal.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isPhoneNumberChanged() {
        if(!phone.equals(update_user_etPhone.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_etPhone.getText().toString())){
                update_user_etPhone.setError(getText(R.string.error_phone_required));
                update_user_etPhone.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("phone", update_user_etPhone.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if(!email.equals(update_user_etEmailToUser.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_etEmailToUser.getText().toString())){
                update_user_etEmailToUser.setError(getText(R.string.error_email_required));
                update_user_etEmailToUser.requestFocus();
                return false;
            }else if(!update_user_etEmailToUser.getText().toString().matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
                update_user_etEmailToUser.setError(getText(R.string.error_validate_email));
                update_user_etEmailToUser.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("email", update_user_etEmailToUser.getText().toString());
                return true;
            }

        }else
        {
            return false;
        }
    }

    private boolean isBalanceChanged() {
        if(!balance.equals(update_user_et_balance_autocomplete.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_balance_autocomplete.getText().toString())){
                update_user_et_balance_autocomplete.setError(getText(R.string.error_balance_required));
                update_user_et_balance_autocomplete.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("balance", update_user_etPhone.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isCoinsChanged() {
        if(!coins.equals(update_user_et_coins_autocomplete.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_coins_autocomplete.getText().toString())){
                update_user_et_coins_autocomplete.setError(getText(R.string.error_coins_required));
                update_user_et_coins_autocomplete.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("coins", update_user_et_coins_autocomplete.getText().toString());
                return true;
            }
        }else
        {
            return false;
        }
    }

    private boolean isGradeChanged() {
        if(!grade.equals(update_user_et_grade_autocomplete.getText().toString()))
        {
            if(TextUtils.isEmpty(update_user_et_grade_autocomplete.getText().toString())){
                update_user_et_grade_autocomplete.setError(getText(R.string.error_grade_required));
                update_user_et_grade_autocomplete.requestFocus();
                return false;
            }else {
                DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
                docRef.update("grade", update_user_et_grade_autocomplete.getText().toString());
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
                    || isFullNameChanged()
                    || isBalanceChanged()
                    || isCoinsChanged()
                    || isGradeChanged()){
                update_user_saveChangesProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                refreshDataFromFirebase();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshDataFromFirebase() {
        firebaseFirestore.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null) {
                            name = documentSnapshot.getString("name");
                            parentName = documentSnapshot.getString("parentName");
                            lastname = documentSnapshot.getString("lastname");
                            address = documentSnapshot.getString("address");
                            personal_number = documentSnapshot.getString("personal_number");
                            phone = documentSnapshot.getString("phone");
                            email = documentSnapshot.getString("email");
                            gender = documentSnapshot.getString("gender");
                            password = documentSnapshot.getString("password");
                            role = documentSnapshot.getString("role");
                            fullName = documentSnapshot.getString("fullName");
                            grade = documentSnapshot.getString("grade");
                            register_date_time = documentSnapshot.getString("register_date_time");
                            balance = Integer.valueOf(documentSnapshot.getString("balance"));
                            coins = Integer.valueOf(documentSnapshot.getString("coins"));

                            update_user_et_firstName.setText(name);
                            update_user_et_parentName.setText(parentName);
                            update_user_et_lastName.setText(lastname);
                            update_user_etAddress.setText(address);
                            update_user_etNumPersonal.setText(personal_number);
                            update_user_etPhone.setText(phone);
                            update_user_etEmailToUser.setText(email);
                            update_user_et_gender.setText(gender);
                            update_user_etPasswordToUser.setText(password);
                            update_user_et_role_autocomplete.setText(role);
                            update_user_et_fullName.setText(fullName);
                            update_user_et_grade_autocomplete.setText(grade);
                            update_user_etDateRegistration.setText(register_date_time);
                            update_user_et_balance_autocomplete.setText(balance);
                            update_user_et_coins_autocomplete.setText(coins);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}