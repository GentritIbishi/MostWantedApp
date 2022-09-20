package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.helpers.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.helpers.Constants.COINS;
import static fiek.unipr.mostwantedapp.helpers.Constants.EURO;
import static fiek.unipr.mostwantedapp.helpers.Constants.NA;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.helpers.FirebaseScrypt;
import fiek.unipr.mostwantedapp.helpers.SecurityHelper;
import fiek.unipr.mostwantedapp.models.User;

public class ProfileFragment extends Fragment {

    private View admin_account_fragment_view;
    private String informer_fullName;
    private Boolean emailVerified;
    private String address, email, fullName, gender, lastname, name, parentName, password, personal_number, phone, grade,
            register_date_time, role, userID, urlOfProfile, balance, coins;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private CircleImageView admin_imageOfProfile;
    private Button admin_btnUploadNewPicture, admin_btnDeletePhoto, admin_btnSaveChanges;
    private TextInputEditText admin_et_firstName, admin_et_lastName,
            admin_et_parentName, admin_etPhone, admin_etAddress, admin_etNumPersonal,
            admin_etEmailToUser, admin_etPasswordToUser, admin_et_fullName, admin_etDateRegistration;
    private MaterialAutoCompleteTextView admin_et_gender, admin_et_role_autocomplete, admin_et_coins_autocomplete,
            admin_et_balance_autocomplete, admin_et_grade_autocomplete;
    private TextInputLayout admin_etNumPersonalLayout;
    private ProgressBar admin_saveChangesProgressBar, admin_uploadProgressBar;
    private String[] BALANCE_ARRAY = null;
    private String[] COINS_ARRAY = null;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_account_fragment_view = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        admin_imageOfProfile = admin_account_fragment_view.findViewById(R.id.admin_imageOfProfile);
        admin_btnUploadNewPicture = admin_account_fragment_view.findViewById(R.id.admin_btnUploadNewPicture);
        admin_btnDeletePhoto = admin_account_fragment_view.findViewById(R.id.admin_btnDeletePhoto);
        admin_btnSaveChanges = admin_account_fragment_view.findViewById(R.id.admin_btnSaveChanges);
        admin_et_firstName = admin_account_fragment_view.findViewById(R.id.admin_et_firstName);
        admin_et_lastName = admin_account_fragment_view.findViewById(R.id.admin_et_lastName);
        admin_et_parentName = admin_account_fragment_view.findViewById(R.id.admin_et_parentName);
        admin_et_fullName = admin_account_fragment_view.findViewById(R.id.admin_et_fullName);
        admin_etPhone = admin_account_fragment_view.findViewById(R.id.admin_etPhone);
        admin_etAddress = admin_account_fragment_view.findViewById(R.id.admin_etAddress);
        admin_etNumPersonal = admin_account_fragment_view.findViewById(R.id.admin_etNumPersonal);
        admin_etNumPersonalLayout = admin_account_fragment_view.findViewById(R.id.admin_etNumPersonalLayout);
        admin_etEmailToUser = admin_account_fragment_view.findViewById(R.id.admin_etEmailToUser);
        admin_etPasswordToUser = admin_account_fragment_view.findViewById(R.id.admin_etPasswordToUser);
        admin_et_gender = admin_account_fragment_view.findViewById(R.id.admin_et_gender);
        admin_et_role_autocomplete = admin_account_fragment_view.findViewById(R.id.admin_et_role_autocomplete);
        admin_saveChangesProgressBar = admin_account_fragment_view.findViewById(R.id.admin_saveChangesProgressBar);
        admin_uploadProgressBar = admin_account_fragment_view.findViewById(R.id.admin_uploadProgressBar);
        admin_et_coins_autocomplete = admin_account_fragment_view.findViewById(R.id.admin_et_coins_autocomplete);
        admin_et_balance_autocomplete = admin_account_fragment_view.findViewById(R.id.admin_et_balance_autocomplete);
        admin_et_grade_autocomplete = admin_account_fragment_view.findViewById(R.id.admin_et_grade_autocomplete);
        admin_etDateRegistration = admin_account_fragment_view.findViewById(R.id.admin_etDateRegistration);

        admin_et_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
                admin_et_gender.setAdapter(gender_adapter);
                admin_et_gender.showDropDown();
            }
        });

        admin_et_role_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> role_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
                admin_et_role_autocomplete.setAdapter(role_adapter);
                admin_et_role_autocomplete.showDropDown();
            }
        });

        admin_et_grade_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> grade_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grade));
                admin_et_grade_autocomplete.setAdapter(grade_adapter);
                admin_et_grade_autocomplete.showDropDown();
            }
        });

        setBalanceArray(EURO);
        setCoinsArray(COINS);

        admin_et_balance_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> balance_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, BALANCE_ARRAY);
                admin_et_balance_autocomplete.setAdapter(balance_adapter);
                admin_et_balance_autocomplete.showDropDown();
            }
        });

        admin_et_coins_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> coins_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, COINS_ARRAY);
                admin_et_coins_autocomplete.setAdapter(coins_adapter);
                admin_et_coins_autocomplete.showDropDown();
            }
        });


        getSetUserData(userID);
        loadImage(userID);

        final SwipeRefreshLayout pullToRefreshInHome = admin_account_fragment_view.findViewById(R.id.admin_swipeUpToRefresh);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               // getSetUserData(userID);
                pullToRefreshInHome.setRefreshing(false);
            }
        });

        admin_btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin_saveChangesProgressBar.setVisibility(View.VISIBLE);
                try {
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                admin_uploadProgressBar.setVisibility(View.VISIBLE);
                deletePhoto();
            }
        });

        admin_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getContext().getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        admin_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    admin_etNumPersonalLayout.setError(getContext().getText(R.string.no_more_than_ten_digits));
                }else if(charSequence.length() < 10) {
                    admin_etNumPersonalLayout.setError(null);
                }else if(charSequence.length() == 10){
                    admin_etNumPersonalLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return admin_account_fragment_view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        admin_et_gender.setAdapter(gender_adapter);

        ArrayAdapter<String> role_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        admin_et_role_autocomplete.setAdapter(role_adapter);

        ArrayAdapter<String> grade_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grade));
        admin_et_grade_autocomplete.setAdapter(grade_adapter);

        ArrayAdapter<String> balance_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, BALANCE_ARRAY);
        admin_et_balance_autocomplete.setAdapter(balance_adapter);

        ArrayAdapter<String> coins_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, COINS_ARRAY);
        admin_et_coins_autocomplete.setAdapter(coins_adapter);

    }

    private void setBalanceArray(String euro) {
        BALANCE_ARRAY = new String[50000];
        for(int i=0; i<50000; i++) {
            BALANCE_ARRAY[i] = i+" "+euro;
        }
    }

    private void setCoinsArray(String coin) {
        COINS_ARRAY = new String[50000];
        for(int i=0; i<50000; i++) {
            COINS_ARRAY[i] = (i+5)+" "+coin;
        }
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                admin_uploadProgressBar.setVisibility(View.GONE);
                admin_imageOfProfile.setImageResource(R.drawable.ic_profile_picture_default);
                Toast.makeText(getContext(), R.string.image_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                admin_uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        admin_uploadProgressBar.setVisibility(View.VISIBLE);
        //upload image to storage in firebase
        StorageReference fileRef = storageReference.child(USERS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(admin_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
                        docRef.update("urlOfProfile", uri.toString());
                        admin_uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), R.string.images_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                admin_uploadProgressBar.setVisibility(View.GONE);
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

    private void loadImage(String uID) {
        admin_uploadProgressBar.setVisibility(View.VISIBLE);
        StorageReference profileRef = storageReference.child(USERS+"/" + uID + "/"+PROFILE_PICTURE);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(admin_imageOfProfile);
                admin_uploadProgressBar.setVisibility(View.GONE);
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
                            //no need to change
                            userID = documentSnapshot.getString("userID");
                            urlOfProfile = documentSnapshot.getString("urlOfProfile");

                            address = documentSnapshot.getString("address");
                            admin_etAddress.setText(address);

                            email = documentSnapshot.getString("email");
                            admin_etEmailToUser.setText(email);

                            gender = documentSnapshot.getString("gender");
                            admin_et_gender.setText(gender);

                            lastname = documentSnapshot.getString("lastname");
                            admin_et_lastName.setText(lastname);

                            name = documentSnapshot.getString("name");
                            admin_et_firstName.setText(name);

                            parentName = documentSnapshot.getString("parentName");
                            admin_et_parentName.setText(parentName);

                            password = documentSnapshot.getString("password");
                            admin_etPasswordToUser.setText(password);

                            personal_number = documentSnapshot.getString("personal_number");
                            admin_etNumPersonal.setText(personal_number);

                            phone = documentSnapshot.getString("phone");
                            admin_etPhone.setText(phone);

                            role = documentSnapshot.getString("role");
                            admin_et_role_autocomplete.setText(role);

                            balance = documentSnapshot.getString("balance");
                            admin_et_balance_autocomplete.setText(balance);

                            coins = documentSnapshot.getString("coins");
                            admin_et_coins_autocomplete.setText(coins);

                            fullName = documentSnapshot.getString("fullName");
                            admin_et_fullName.setText(fullName);

                            grade = documentSnapshot.getString("grade");
                            admin_et_grade_autocomplete.setText(grade);

                            //no option for change by default disable
                            register_date_time = documentSnapshot.getString("register_date_time");
                            admin_etDateRegistration.setText(register_date_time);
                            admin_etDateRegistration.setEnabled(false);

                            emailVerified = documentSnapshot.getBoolean("emailVerified");
                        } else {
                            if(firebaseAuth.getCurrentUser().isAnonymous())
                            {
                                address = ANONYMOUS;
                                admin_etAddress.setText(address);

                                email = ANONYMOUS;
                                admin_etEmailToUser.setText(email);

                                gender = ANONYMOUS;
                                admin_et_gender.setText(gender);

                                lastname = ANONYMOUS;
                                admin_et_lastName.setText(lastname);

                                name = ANONYMOUS;
                                admin_et_firstName.setText(name);

                                parentName = ANONYMOUS;
                                admin_et_parentName.setText(parentName);

                                password = ANONYMOUS;
                                admin_etPasswordToUser.setText(password);

                                personal_number = ANONYMOUS;
                                admin_etNumPersonal.setText(personal_number);

                                phone = ANONYMOUS;
                                admin_etPhone.setText(phone);

                                role = ANONYMOUS;
                                admin_et_role_autocomplete.setText(role);

                                balance = ANONYMOUS;
                                admin_et_balance_autocomplete.setText(balance);

                                coins = ANONYMOUS;
                                admin_et_coins_autocomplete.setText(coins);

                                fullName = ANONYMOUS;
                                admin_et_fullName.setText(fullName);

                                grade = ANONYMOUS;
                                admin_et_grade_autocomplete.setText(grade);

                                //no option for change by default disable
                                register_date_time = ANONYMOUS;
                                admin_etDateRegistration.setText(register_date_time);
                            }else {

                                address = NA;
                                admin_etAddress.setText(address);

                                email = NA;
                                admin_etEmailToUser.setText(email);

                                gender = NA;
                                admin_et_gender.setText(gender);

                                lastname = NA;
                                admin_et_lastName.setText(lastname);

                                name = NA;
                                admin_et_firstName.setText(name);

                                parentName = NA;
                                admin_et_parentName.setText(parentName);

                                password = NA;
                                admin_etPasswordToUser.setText(password);

                                personal_number = NA;
                                admin_etNumPersonal.setText(personal_number);

                                phone = firebaseAuth.getCurrentUser().getPhoneNumber();;
                                admin_etPhone.setText(phone);

                                role = NA;
                                admin_et_role_autocomplete.setText(role);

                                balance = NA;
                                admin_et_balance_autocomplete.setText(balance);

                                coins = NA;
                                admin_et_coins_autocomplete.setText(coins);

                                fullName = NA;
                                admin_et_fullName.setText(fullName);

                                grade = NA;
                                admin_et_grade_autocomplete.setText(grade);

                                //no option for change by default disable
                                register_date_time = NA;
                                admin_etDateRegistration.setText(register_date_time);
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

    private void update() throws Exception {
        SecurityHelper securityHelper = new SecurityHelper();
        String new_name = admin_et_firstName.getText().toString();
        String new_lastname = admin_et_lastName.getText().toString();
        String new_fullName = admin_et_fullName.getText().toString();
        String new_address = admin_etAddress.getText().toString();
        String new_email = admin_etEmailToUser.getText().toString();
        String new_ParentName = admin_et_parentName.getText().toString();
        String new_gender = admin_et_gender.getText().toString();
        String new_role = admin_et_role_autocomplete.getText().toString();
        String new_phone = admin_etPhone.getText().toString();
        String new_personal_number = admin_etNumPersonal.getText().toString();
        String new_grade = admin_et_grade_autocomplete.getText().toString();
        String new_password = admin_etPasswordToUser.getText().toString();
        String new_balance = admin_et_balance_autocomplete.getText().toString();
        String new_coins = admin_et_coins_autocomplete.getText().toString();

        if(TextUtils.isEmpty(new_name)){
            admin_et_firstName.setError(getText(R.string.error_first_name_required));
            admin_et_firstName.requestFocus();
        }else if(TextUtils.isEmpty(new_lastname)){
            admin_et_lastName.setError(getText(R.string.error_last_name_required));
            admin_et_lastName.requestFocus();
        }else if(TextUtils.isEmpty(new_fullName)){
            admin_et_fullName.setError(getText(R.string.error_fullname_required));
            admin_et_fullName.requestFocus();
        }else if(TextUtils.isEmpty(new_ParentName)){
            admin_et_parentName.setError(getText(R.string.error_parent_name_required));
            admin_et_parentName.requestFocus();
        }else if(TextUtils.isEmpty(new_role)){
            admin_et_role_autocomplete.setError(getText(R.string.error_role_required));
            admin_et_role_autocomplete.requestFocus();
        }else if(TextUtils.isEmpty(new_grade)){
            admin_et_grade_autocomplete.setError(getText(R.string.error_grade_required));
            admin_et_grade_autocomplete.requestFocus();
        }else if(TextUtils.isEmpty(new_balance)){
            admin_et_balance_autocomplete.setError(getText(R.string.error_balance_required));
            admin_et_balance_autocomplete.requestFocus();
        }else if(TextUtils.isEmpty(new_coins)){
            admin_et_coins_autocomplete.setError(getText(R.string.error_coins_required));
            admin_et_coins_autocomplete.requestFocus();
        }else if(TextUtils.isEmpty(new_personal_number)){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_required));
            admin_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()>10){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
            admin_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()<10){
            admin_etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
            admin_etNumPersonal.requestFocus();
        }else if(TextUtils.isEmpty(new_phone)){
            admin_etPhone.setError(getText(R.string.error_phone_required));
            admin_etPhone.requestFocus();
        }else if(TextUtils.isEmpty(new_address)){
            admin_etAddress.setError(getText(R.string.error_address_required));
            admin_etAddress.requestFocus();
        }else if(TextUtils.isEmpty(new_email)){
            admin_etEmailToUser.setError(getText(R.string.error_email_required));
            admin_etEmailToUser.requestFocus();
        }else if(!new_email.matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
            admin_etEmailToUser.setError(getText(R.string.error_validate_email));
            admin_etEmailToUser.requestFocus();
        }else if(!new_password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
            admin_etPasswordToUser.setError(getText(R.string.error_password_weak));
            admin_etPasswordToUser.requestFocus();
        } else if(TextUtils.isEmpty(new_password)){
            admin_etPasswordToUser.setError(getText(R.string.error_password_required));
            admin_etPasswordToUser.requestFocus();
        }else if (TextUtils.isEmpty(new_gender)) {
            admin_et_gender.setError(getText(R.string.error_gender_required));
            admin_et_gender.requestFocus();
        }else {
            String hashPassword = securityHelper.encrypt(new_password);
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
                    new_coins,
                    emailVerified);
            firebaseFirestore.collection(USERS)
                    .document(firebaseAuth.getUid())
                    .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                            getSetUserData(userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}