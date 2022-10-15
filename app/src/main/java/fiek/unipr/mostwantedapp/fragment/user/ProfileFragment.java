package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.User;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.utils.SecurityHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class ProfileFragment extends Fragment {

    private View view;
    private Context mContext;
    private Boolean emailVerified;
    private String address, email, fullName, gender, lastname, name, parentName, password, personal_number, phone, grade,
            register_date_time, role, userID, urlOfProfile, balance, coins;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private ConstraintLayout buttonsConstraint, bottomConstrainPU;
    private CircleImageView profile_user_imageOfProfile;
    private Button profile_user_btnUploadNewPicture, profile_user_btnDeletePhoto, profile_user_btnSaveChanges;
    private TextInputEditText profile_user_et_firstName, profile_user_et_lastName,
            profile_user_et_parentName, profile_user_etPhone, profile_user_etAddress, profile_user_etNumPersonal,
            profile_user_etEmailToUser, profile_user_etPasswordToUser, profile_user_et_fullName, profile_user_etDateRegistration,
            profile_user_et_role, profile_user_et_balance, profile_user_et_grade;
    private MaterialAutoCompleteTextView profile_user_et_gender_autocomplete;
    private TextInputLayout profile_user_etNumPersonalLayout;
    private ProgressBar profile_user_saveChangesProgressBar, profile_user_uploadProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_user, container, false);
        mContext = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        initializeFields();

        profile_user_et_gender_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
                profile_user_et_gender_autocomplete.setAdapter(gender_adapter);
                profile_user_et_gender_autocomplete.showDropDown();
            }
        });

        final SwipeRefreshLayout pullToRefreshInSearch = view.findViewById(R.id.user_profile_refresh);
        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                loadInfoFromFirebase(firebaseAuth);
                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        profile_user_btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_user_saveChangesProgressBar.setVisibility(View.VISIBLE);
                try {
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        profile_user_btnUploadNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        profile_user_btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_user_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        profile_user_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mContext.getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        profile_user_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    profile_user_etNumPersonalLayout.setError(mContext.getText(R.string.no_more_than_ten_digits));
                }else if(charSequence.length() < 10) {
                    profile_user_etNumPersonalLayout.setError(null);
                }else if(charSequence.length() == 10){
                    profile_user_etNumPersonalLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void initializeFields() {
            profile_user_imageOfProfile = view.findViewById(R.id.profile_user_imageOfProfile);
            profile_user_btnUploadNewPicture = view.findViewById(R.id.profile_user_btnUploadNewPicture);
            profile_user_btnDeletePhoto = view.findViewById(R.id.profile_user_btnDeletePhoto);
            profile_user_et_firstName = view.findViewById(R.id.profile_user_et_firstName);
            profile_user_et_lastName = view.findViewById(R.id.profile_user_et_lastName);
            profile_user_et_parentName = view.findViewById(R.id.profile_user_et_parentName);
            profile_user_et_fullName = view.findViewById(R.id.profile_user_et_fullName);
            profile_user_etPhone = view.findViewById(R.id.profile_user_etPhone);
            profile_user_etAddress = view.findViewById(R.id.profile_user_etAddress);
            profile_user_etNumPersonal = view.findViewById(R.id.profile_user_etNumPersonal);
            profile_user_etNumPersonalLayout = view.findViewById(R.id.profile_user_etNumPersonalLayout);
            profile_user_etEmailToUser = view.findViewById(R.id.profile_user_etEmailToUser);
            profile_user_etPasswordToUser = view.findViewById(R.id.profile_user_etPasswordToUser);
            profile_user_et_gender_autocomplete = view.findViewById(R.id.profile_user_et_gender);
            profile_user_et_role = view.findViewById(R.id.profile_user_et_role);
            profile_user_et_balance = view.findViewById(R.id.profile_user_et_balance);
            profile_user_et_grade = view.findViewById(R.id.profile_user_et_grade);
            profile_user_etDateRegistration = view.findViewById(R.id.profile_user_etDateRegistration);
            buttonsConstraint = view.findViewById(R.id.buttonsConstraint);
            bottomConstrainPU = view.findViewById(R.id.bottomConstrainPU);
            profile_user_btnSaveChanges = view.findViewById(R.id.profile_user_btnSaveChanges);
            profile_user_saveChangesProgressBar = view.findViewById(R.id.profile_user_saveChangesProgressBar);
            profile_user_uploadProgressBar = view.findViewById(R.id.profile_user_uploadProgressBar);
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        profile_user_et_gender_autocomplete.setAdapter(gender_adapter);

    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                profile_user_uploadProgressBar.setVisibility(View.GONE);
                profile_user_imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(mContext, R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profile_user_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        profile_user_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(profile_user_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
                        docRef.update("urlOfProfile", uri.toString());
                        profile_user_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, R.string.images_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profile_user_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
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

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(mContext)){
            documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists() && Objects.requireNonNull(task.getResult().getData()).size() != 0)
                    {
                        bottomConstrainPU.setVisibility(View.VISIBLE);
                        buttonsConstraint.setVisibility(View.VISIBLE);
                        profile_user_etAddress.setText(task.getResult().getString("address"));
                        profile_user_etEmailToUser.setText(task.getResult().getString("email"));
                        profile_user_et_gender_autocomplete.setText(task.getResult().getString("gender"));
                        profile_user_et_lastName.setText(task.getResult().getString("lastname"));
                        profile_user_et_firstName.setText(task.getResult().getString("name"));
                        profile_user_et_parentName.setText(task.getResult().getString("parentName"));

                        password = task.getResult().getString("password");
                        if(!StringHelper.empty(password))
                        {
                            try {
                                SecurityHelper securityHelper = new SecurityHelper();
                                String clean_password = securityHelper.decrypt(password);
                                profile_user_etPasswordToUser.setText(clean_password);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        profile_user_etNumPersonal.setText(task.getResult().getString("personal_number"));
                        profile_user_etPhone.setText(task.getResult().getString("phone"));
                        profile_user_et_role.setText(task.getResult().getString("role"));
                        profile_user_et_balance.setText(String.valueOf(task.getResult().getDouble("balance")));
                        profile_user_et_fullName.setText(task.getResult().getString("fullName"));
                        profile_user_et_grade.setText(task.getResult().getString("grade"));
                        profile_user_etDateRegistration.setText(task.getResult().getString("register_date_time"));
                        profile_user_etDateRegistration.setEnabled(false);

                        emailVerified = task.getResult().getBoolean("emailVerified");

                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        if(urlOfProfile != null){
                            Picasso.get().load(urlOfProfile).into(profile_user_imageOfProfile);
                        }

                    }
                }
            });
        }
    }

    private void update() throws Exception
    {
        SecurityHelper securityHelper = new SecurityHelper();
        String new_name = profile_user_et_firstName.getText().toString();
        String new_lastname = profile_user_et_lastName.getText().toString();
        String new_ParentName = profile_user_et_parentName.getText().toString();
        String new_fullName = new_name +" " +"("+new_ParentName+")"+" " + new_lastname;
        String new_address = profile_user_etAddress.getText().toString();
        String new_email = profile_user_etEmailToUser.getText().toString();
        String new_gender = profile_user_et_gender_autocomplete.getText().toString();
        String new_role = profile_user_et_role.getText().toString();
        String new_phone = profile_user_etPhone.getText().toString();
        String new_personal_number = profile_user_etNumPersonal.getText().toString();
        String new_grade = profile_user_et_grade.getText().toString();
        String new_password = profile_user_etPasswordToUser.getText().toString();
        Double new_balance = Double.valueOf(profile_user_et_balance.getText().toString());

        if(TextUtils.isEmpty(new_name)){
            profile_user_et_firstName.setError(getText(R.string.error_first_name_required));
            profile_user_et_firstName.requestFocus();
        }else if(TextUtils.isEmpty(new_lastname)){
            profile_user_et_lastName.setError(getText(R.string.error_last_name_required));
            profile_user_et_lastName.requestFocus();
        }else if(TextUtils.isEmpty(new_fullName)){
            profile_user_et_fullName.setError(getText(R.string.error_fullname_required));
            profile_user_et_fullName.requestFocus();
        }else if(TextUtils.isEmpty(new_ParentName)){
            profile_user_et_parentName.setError(getText(R.string.error_parent_name_required));
            profile_user_et_parentName.requestFocus();
        }else if(TextUtils.isEmpty(new_role)){
            profile_user_et_role.setError(getText(R.string.error_role_required));
            profile_user_et_role.requestFocus();
        }else if(TextUtils.isEmpty(new_grade)){
            profile_user_et_grade.setError(getText(R.string.error_grade_required));
            profile_user_et_grade.requestFocus();
        }else if(TextUtils.isEmpty(new_balance.toString())){
            profile_user_et_balance.setError(getText(R.string.error_balance_required));
            profile_user_et_balance.requestFocus();
        }else if(TextUtils.isEmpty(new_personal_number)){
            profile_user_etNumPersonal.setError(getText(R.string.error_number_personal_required));
            profile_user_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()>10){
            profile_user_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
            profile_user_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()<10){
            profile_user_etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
            profile_user_etNumPersonal.requestFocus();
        }else if(TextUtils.isEmpty(new_phone)){
            profile_user_etPhone.setError(getText(R.string.error_phone_required));
            profile_user_etPhone.requestFocus();
        }else if(TextUtils.isEmpty(new_address)){
            profile_user_etAddress.setError(getText(R.string.error_address_required));
            profile_user_etAddress.requestFocus();
        }else if(TextUtils.isEmpty(new_email)){
            profile_user_etEmailToUser.setError(getText(R.string.error_email_required));
            profile_user_etEmailToUser.requestFocus();
        }else if(!new_email.matches("^[a-z0-9](\\.?[a-z0-9_-])*@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
            profile_user_etEmailToUser.setError(getText(R.string.error_validate_email));
            profile_user_etEmailToUser.requestFocus();
        }else if(!new_password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
            profile_user_etPasswordToUser.setError(getText(R.string.error_password_weak));
            profile_user_etPasswordToUser.requestFocus();
        } else if(TextUtils.isEmpty(new_password)){
            profile_user_etPasswordToUser.setError(getText(R.string.error_password_required));
            profile_user_etPasswordToUser.requestFocus();
        }else if (TextUtils.isEmpty(new_gender)) {
            profile_user_et_gender_autocomplete.setError(getText(R.string.error_gender_required));
            profile_user_et_gender_autocomplete.requestFocus();
        }else {
            firebaseFirestore.collection(USERS)
                    .document(userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Double totalPaid = documentSnapshot.getDouble("totalPaid");
                            String hashPassword = securityHelper.encrypt(new_password);
                            String register_date_time = documentSnapshot.getString("register_date_time");
                            String urlOfProfile = documentSnapshot.getString("urlOfProfile");
                            User user = new User(
                                    userID,
                                    new_name,
                                    new_lastname,
                                    new_fullName,
                                    new_address,
                                    new_email,
                                    new_ParentName,
                                    new_gender,
                                    new_role,
                                    new_phone,
                                    new_personal_number,
                                    register_date_time,
                                    new_grade,
                                    hashPassword,
                                    urlOfProfile,
                                    new_balance,
                                    totalPaid,
                                    emailVerified);

                            firebaseFirestore.collection(USERS)
                                    .document(firebaseAuth.getCurrentUser().getUid())
                                    .set(user, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(mContext, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                                                loadInfoFromFirebase(firebaseAuth);
                                            }else {
                                                Toast.makeText(mContext, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
            if(firebaseAuth != null){
                loadInfoFromFirebase(firebaseAuth);
            }
        }

}