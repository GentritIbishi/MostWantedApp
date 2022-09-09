package fiek.unipr.mostwantedapp.fragment.admin.update.person;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserListFragment;

public class UpdatePersonFragment extends Fragment {

    private View update_person_view;
    private CircleImageView update_person_imageOfProfile;
    private Button update_person_btnUploadNewPicture, update_person_btnDeletePhoto, update_person_btnSaveChanges;
    private ProgressBar update_person_uploadProgressBar, update_person_saveChangesProgressBar;
    private TextInputEditText update_person_et_firstName, update_person_et_lastName, update_person_et_parentName,
            update_person_et_fullName, update_person_etAddress, update_person_etLatitude,
            update_person_etLongitude, update_person_etDateRegistration;
    private MaterialAutoCompleteTextView update_person_et_age_autocomplete, update_person_et_eye_color_autocomplete,
            update_person_et_hair_color_autocomplete, update_person_et_height_autocomplete,
            update_person_et_weight_autocomplete, update_person_et_phy_appearance_autocomplete,
            update_person_et_prize_autocomplete, update_person_et_status_autocomplete, update_person_et_gender_autocomplete;
    private MultiAutoCompleteTextView update_person_et_acts_multiAutocomplete;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    public UpdatePersonFragment() {}

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
        update_person_view = inflater.inflate(R.layout.fragment_update_person, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        update_person_imageOfProfile = update_person_view.findViewById(R.id.update_person_imageOfProfile);
        update_person_btnUploadNewPicture = update_person_view.findViewById(R.id.update_person_btnUploadNewPicture);
        update_person_btnDeletePhoto = update_person_view.findViewById(R.id.update_person_btnDeletePhoto);
        update_person_btnSaveChanges = update_person_view.findViewById(R.id.update_person_btnSaveChanges);
        update_person_uploadProgressBar = update_person_view.findViewById(R.id.update_person_uploadProgressBar);
        update_person_saveChangesProgressBar = update_person_view.findViewById(R.id.update_person_saveChangesProgressBar);
        update_person_et_firstName = update_person_view.findViewById(R.id.update_person_et_firstName);
        update_person_et_lastName = update_person_view.findViewById(R.id.update_person_et_lastName);
        update_person_et_parentName = update_person_view.findViewById(R.id.update_person_et_parentName);
        update_person_et_fullName = update_person_view.findViewById(R.id.update_person_et_fullName);
        update_person_et_gender_autocomplete = update_person_view.findViewById(R.id.update_person_et_gender_autocomplete);
        update_person_etAddress = update_person_view.findViewById(R.id.update_person_etAddress);
        update_person_etLatitude = update_person_view.findViewById(R.id.update_person_etLatitude);
        update_person_etLongitude = update_person_view.findViewById(R.id.update_person_etLongitude);
        update_person_etDateRegistration = update_person_view.findViewById(R.id.update_person_etDateRegistration);
        update_person_et_age_autocomplete = update_person_view.findViewById(R.id.update_person_et_age_autocomplete);
        update_person_et_eye_color_autocomplete = update_person_view.findViewById(R.id.update_person_et_eye_color_autocomplete);
        update_person_et_hair_color_autocomplete = update_person_view.findViewById(R.id.update_person_et_hair_color_autocomplete);
        update_person_et_height_autocomplete = update_person_view.findViewById(R.id.update_person_et_height_autocomplete);
        update_person_et_weight_autocomplete = update_person_view.findViewById(R.id.update_person_et_weight_autocomplete);
        update_person_et_phy_appearance_autocomplete = update_person_view.findViewById(R.id.update_person_et_phy_appearance_autocomplete);
        update_person_et_prize_autocomplete = update_person_view.findViewById(R.id.update_person_et_prize_autocomplete);
        update_person_et_status_autocomplete = update_person_view.findViewById(R.id.update_person_et_status_autocomplete);
        update_person_et_acts_multiAutocomplete = update_person_view.findViewById(R.id.update_person_et_acts_multiAutocomplete);


        onBackPressed();
        return update_person_view;
    }

    private void onBackPressed() {
        update_person_view.setFocusableInTouchMode(true);
        update_person_view.requestFocus();
        update_person_view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Fragment fragment = new UpdatePersonListFragment();
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