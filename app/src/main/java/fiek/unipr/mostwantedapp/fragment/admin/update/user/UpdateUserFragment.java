package fiek.unipr.mostwantedapp.fragment.admin.update.user;

import static fiek.unipr.mostwantedapp.utils.Constants.COINS;
import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.utils.SecurityHelper;
import fiek.unipr.mostwantedapp.models.User;

public class UpdateUserFragment extends Fragment {

    private View update_user_view;
    private Boolean emailVerified;
    private String address, email, fullName, gender, lastname, name, parentName, password, personal_number, phone, grade,
            register_date_time, role, userID, urlOfProfile, balance, coins;

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
    private TextInputLayout update_user_etNumPersonalLayout;
    private ProgressBar update_user_saveChangesProgressBar, update_user_uploadProgressBar;
    private SwipeRefreshLayout update_user_swipeUpToRefresh;
    private String[] BALANCE_ARRAY = null;
    private String[] COINS_ARRAY = null;
    private Bundle bundle;

    public UpdateUserFragment() {}

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
        update_user_etNumPersonalLayout = update_user_view.findViewById(R.id.update_user_etNumPersonalLayout);
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

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        update_user_et_gender.setAdapter(gender_adapter);

        ArrayAdapter<String> role_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        update_user_et_role_autocomplete.setAdapter(role_adapter);

        ArrayAdapter<String> grade_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grade));
        update_user_et_grade_autocomplete.setAdapter(grade_adapter);

        setBalanceArray(EURO);
        setCoinsArray(COINS);

        ArrayAdapter<String> balance_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, BALANCE_ARRAY);
        update_user_et_balance_autocomplete.setAdapter(balance_adapter);

        ArrayAdapter<String> coins_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, COINS_ARRAY);
        update_user_et_coins_autocomplete.setAdapter(coins_adapter);

        bundle = getArguments();
        try {
            getAndSetFromBundle(bundle);
        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        update_user_etNumPersonalLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getContext().getText(R.string.info_number_personal_is_ten_digit), Toast.LENGTH_SHORT).show();
            }
        });

        update_user_etNumPersonal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>10){
                    update_user_etNumPersonalLayout.setError(getContext().getText(R.string.no_more_than_ten_digits));
                }else if(charSequence.length() < 10) {
                    update_user_etNumPersonalLayout.setError(null);
                }else if(charSequence.length() == 10){
                    update_user_etNumPersonalLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //What happen when on back pressed
        onBackPressed();

        return update_user_view;
    }

    private void onBackPressed() {
        update_user_view.setFocusableInTouchMode(true);
        update_user_view.requestFocus();
        update_user_view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Fragment fragment = new UpdateUserListFragment();
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void getAndSetFromBundle(Bundle bundle) throws Exception {
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
            SecurityHelper securityHelper = new SecurityHelper();
            String decryptedPassword  = securityHelper.decrypt(password);
            update_user_etPasswordToUser.setText(decryptedPassword);

            personal_number = bundle.getString("personal_number");
            update_user_etNumPersonal.setText(personal_number);

            phone = bundle.getString("phone");
            update_user_etPhone.setText(phone);

            role = bundle.getString("role");
            update_user_et_role_autocomplete.setText(role);

            balance = bundle.getString("balance");
            update_user_et_balance_autocomplete.setText(balance);

            coins = bundle.getString("coins");
            update_user_et_coins_autocomplete.setText(coins);

            fullName = bundle.getString("fullName");
            update_user_et_fullName.setText(fullName);

            grade = bundle.getString("grade");
            update_user_et_grade_autocomplete.setText(grade);

            //no option for change by default disable
            register_date_time = bundle.getString("register_date_time");
            update_user_etDateRegistration.setText(register_date_time);
            update_user_etDateRegistration.setEnabled(false);

            emailVerified = bundle.getBoolean("emailVerified");

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        update_user_et_gender.setAdapter(gender_adapter);

        ArrayAdapter<String> role_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.roles));
        update_user_et_role_autocomplete.setAdapter(role_adapter);

        ArrayAdapter<String> grade_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grade));
        update_user_et_grade_autocomplete.setAdapter(grade_adapter);

        ArrayAdapter<String> balance_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, BALANCE_ARRAY);
        update_user_et_balance_autocomplete.setAdapter(balance_adapter);

        ArrayAdapter<String> coins_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, COINS_ARRAY);
        update_user_et_coins_autocomplete.setAdapter(coins_adapter);

    }

    private void setBalanceArray(String euro) {
        BALANCE_ARRAY = new String[1000000];
        for(int i=0; i<1000000; i++) {
            BALANCE_ARRAY[i] = i+" "+euro;
        }
    }

    private void setCoinsArray(String coin) {
        COINS_ARRAY = new String[1000000];
        for(int i=0; i<1000000; i++) {
            COINS_ARRAY[i] = (i+5)+" "+coin;
        }
    }

    private void deletePhoto() {
        StorageReference fileRef = storageReference.child(USERS+"/"+userID+"/"+PROFILE_PICTURE);
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
        StorageReference fileRef = storageReference.child(USERS+"/"+userID+"/"+PROFILE_PICTURE);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into(update_user_imageOfProfile);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(userID);
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
        StorageReference profileRef = storageReference.child(USERS+"/" + userID + "/"+PROFILE_PICTURE);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(update_user_imageOfProfile);
                update_user_uploadProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void update() throws Exception {
        SecurityHelper securityHelper = new SecurityHelper();
        String new_name = update_user_et_firstName.getText().toString();
        String new_lastname = update_user_et_lastName.getText().toString();
        String new_fullName = update_user_et_fullName.getText().toString();
        String new_address = update_user_etAddress.getText().toString();
        String new_email = update_user_etEmailToUser.getText().toString();
        String new_ParentName = update_user_et_parentName.getText().toString();
        String new_gender = update_user_et_gender.getText().toString();
        String new_role = update_user_et_role_autocomplete.getText().toString();
        String new_phone = update_user_etPhone.getText().toString();
        String new_personal_number = update_user_etNumPersonal.getText().toString();
        String new_grade = update_user_et_grade_autocomplete.getText().toString();
        String new_password = update_user_etPasswordToUser.getText().toString();
        String new_balance = update_user_et_balance_autocomplete.getText().toString();
        String new_coins = update_user_et_coins_autocomplete.getText().toString();

        if(TextUtils.isEmpty(new_fullName)){
            update_user_et_fullName.setError(getText(R.string.error_fullname_required));
            update_user_et_fullName.requestFocus();
        } else if(TextUtils.isEmpty(new_personal_number)){
            update_user_etNumPersonal.setError(getText(R.string.error_number_personal_required));
            update_user_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()>10){
            update_user_etNumPersonal.setError(getText(R.string.error_number_personal_is_ten_digit));
            update_user_etNumPersonal.requestFocus();
        }else if(new_personal_number.length()<10){
            update_user_etNumPersonal.setError(getText(R.string.error_number_personal_less_than_ten_digits));
            update_user_etNumPersonal.requestFocus();
        }else if(TextUtils.isEmpty(new_phone)){
            update_user_etPhone.setError(getText(R.string.error_phone_required));
            update_user_etPhone.requestFocus();
        }else if(TextUtils.isEmpty(new_address)){
            update_user_etAddress.setError(getText(R.string.error_address_required));
            update_user_etAddress.requestFocus();
        }else if(TextUtils.isEmpty(new_email)){
            update_user_etEmailToUser.setError(getText(R.string.error_email_required));
            update_user_etEmailToUser.requestFocus();
        }else if(!email.matches("^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,6}$")){
            update_user_etEmailToUser.setError(getText(R.string.error_validate_email));
            update_user_etEmailToUser.requestFocus();
        }else if(!new_password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
            update_user_etPasswordToUser.setError(getText(R.string.error_password_weak));
            update_user_etPasswordToUser.requestFocus();
        } else if(TextUtils.isEmpty(new_password)){
            update_user_etPasswordToUser.setError(getText(R.string.error_password_required));
            update_user_etPasswordToUser.requestFocus();
        }else if (TextUtils.isEmpty(new_gender)) {
            update_user_et_gender.setError(getText(R.string.error_gender_required));
            update_user_et_gender.requestFocus();
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
                    .document(userID)
                    .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                            Fragment fragment = new UpdateUserListFragment();
                            loadFragment(fragment);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void refreshDataFromFirebase() {
        firebaseFirestore.collection(USERS).document(userID)
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
                            balance = documentSnapshot.getString("balance");
                            coins = documentSnapshot.getString("coins");
                            update_user_et_firstName.setText(name);
                            update_user_et_parentName.setText(parentName);
                            update_user_et_lastName.setText(lastname);
                            update_user_etAddress.setText(address);
                            update_user_etNumPersonal.setText(personal_number);
                            update_user_etPhone.setText(phone);
                            update_user_etEmailToUser.setText(email);
                            update_user_et_gender.setText(gender);
                            try {
                                SecurityHelper securityHelper = new SecurityHelper();
                                String decryptedPassword  = securityHelper.decrypt(password);
                                update_user_etPasswordToUser.setText(decryptedPassword);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .commit();
    }

}