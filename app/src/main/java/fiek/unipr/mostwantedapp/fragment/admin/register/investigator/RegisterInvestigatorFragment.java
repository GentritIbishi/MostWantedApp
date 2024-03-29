package fiek.unipr.mostwantedapp.fragment.admin.register.investigator;

import static fiek.unipr.mostwantedapp.utils.Constants.CM;
import static fiek.unipr.mostwantedapp.utils.Constants.INVESTIGATORS;
import static fiek.unipr.mostwantedapp.utils.Constants.KG;
import static fiek.unipr.mostwantedapp.utils.StringHelper.empty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.DateInputMask;
import fiek.unipr.mostwantedapp.models.Investigator;

public class RegisterInvestigatorFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View register_investigator_view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MaterialAutoCompleteTextView investigator_et_age, investigator_et_gender,
            investigator_et_height, investigator_et_weight,
            investigator_et_eyeColor, investigator_et_hairColor,
            investigator_et_phy_appearance;
    private TextInputEditText investigator_et_firstName, investigator_et_lastName,
            investigator_et_address, investigator_et_parentName, investigator_et_birthday;
    private Button registerInvestigator;
    private ProgressBar investigator_progressBar;
    private String[] WEIGHT_ARRAY = null;
    private String[] HEIGHT_ARRAY = null;
    private String[] AGE_ARRAY = null;

    public RegisterInvestigatorFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        register_investigator_view = inflater.inflate(R.layout.fragment_register_investigator, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        setWeightInMeasureArray(KG);
        setHeightInMeasureArray(CM);
        setAgeArray();

        registerInvestigator = register_investigator_view.findViewById(R.id.registerInvestigator);
        registerInvestigator.setOnClickListener(this);

        investigator_et_firstName = register_investigator_view.findViewById(R.id.investigator_et_firstName);
        investigator_et_lastName = register_investigator_view.findViewById(R.id.investigator_et_lastName);
        investigator_et_parentName = register_investigator_view.findViewById(R.id.investigator_et_parentName);
        investigator_et_address = register_investigator_view.findViewById(R.id.investigator_et_address);
        investigator_et_birthday = register_investigator_view.findViewById(R.id.investigator_et_birthday);
        new DateInputMask(investigator_et_birthday);

        investigator_et_age = register_investigator_view.findViewById(R.id.investigator_et_age);
        ArrayAdapter<String> age_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, AGE_ARRAY);
        investigator_et_age.setAdapter(age_adapter);

        investigator_et_gender = register_investigator_view.findViewById(R.id.investigator_et_gender);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        investigator_et_gender.setAdapter(gender_adapter);

        investigator_et_height = register_investigator_view.findViewById(R.id.investigator_et_height);
        ArrayAdapter<String> height_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        investigator_et_height.setAdapter(height_adapter);

        investigator_et_weight = register_investigator_view.findViewById(R.id.investigator_et_weight);
        ArrayAdapter<String> weight_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        investigator_et_weight.setAdapter(weight_adapter);

        investigator_et_eyeColor = register_investigator_view.findViewById(R.id.investigator_et_eyeColor);
        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        investigator_et_eyeColor.setAdapter(eye_color_adapter);

        investigator_et_hairColor = register_investigator_view.findViewById(R.id.investigator_et_hairColor);
        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        investigator_et_hairColor.setAdapter(hair_color_adapter);

        investigator_et_phy_appearance = register_investigator_view.findViewById(R.id.investigator_et_phy_appearance);
        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        investigator_et_phy_appearance.setAdapter(phy_appearance_adapter);

        investigator_progressBar = register_investigator_view.findViewById(R.id.investigator_progressBar);

        return register_investigator_view;
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
            AGE_ARRAY[i] = i+" "+mContext.getText(R.string.age);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.registerInvestigator:
                registerInvestigator.setEnabled(false);
                investigator_progressBar.setVisibility(View.VISIBLE);
                register();
                break;
        }
    }

    private void setEmptyField() {
        investigator_et_firstName.setText("");
        investigator_et_lastName.setText("");
        investigator_et_parentName.setText("");
        investigator_et_address.setText("");
        investigator_et_age.setText("");
        investigator_et_gender.setText("");
        investigator_et_height.setText("");
        investigator_et_weight.setText("");
        investigator_et_eyeColor.setText("");
        investigator_et_hairColor.setText("");
        investigator_et_phy_appearance.setText("");
        investigator_et_birthday.setText("");
    }

    private void register() {
        String firstName = investigator_et_firstName.getText().toString().trim();
        String lastName = investigator_et_lastName.getText().toString().trim();
        String parentName = investigator_et_parentName.getText().toString().trim();
        String fullName = firstName+" "+"("+parentName+")"+" "+lastName;
        String birthday = investigator_et_birthday.getText().toString();
        String address = investigator_et_address.getText().toString().trim();
        String age = investigator_et_age.getText().toString().trim();
        String gender = investigator_et_gender.getText().toString().trim();
        String height = investigator_et_height.getText().toString().trim();
        String weight = investigator_et_weight.getText().toString().trim();
        String eyeColor = investigator_et_eyeColor.getText().toString().trim();
        String hairColor = investigator_et_hairColor.getText().toString().trim();
        String phy_appearance = investigator_et_phy_appearance.getText().toString().trim();

        if (firstName.isEmpty()) {
            investigator_et_firstName.setError(getText(R.string.error_first_name_required));
            investigator_et_firstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            investigator_et_lastName.setError(getText(R.string.error_last_name_required));
            investigator_et_lastName.requestFocus();
            return;
        }

        if (parentName.isEmpty()) {
            investigator_et_parentName.setError(getText(R.string.error_parent_name_required));
            investigator_et_parentName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            investigator_et_address.setError(getText(R.string.error_address_required));
            investigator_et_address.requestFocus();
            return;
        }

        if (birthday.isEmpty()) {
            investigator_et_birthday.setError(getText(R.string.error_birthday_required));
            investigator_et_birthday.requestFocus();
            return;
        }

        if (empty(age)) {
            investigator_et_age.setError(getText(R.string.error_age_required));
            investigator_et_age.requestFocus();
            return;
        }

        if (empty(gender)) {
            investigator_et_gender.setError(getText(R.string.error_gender_required));
            investigator_et_gender.requestFocus();
            return;
        }

        if (empty(height)) {
            investigator_et_height.setError(getText(R.string.error_height_required));
            investigator_et_height.requestFocus();
            return;
        }

        if (empty(weight)) {
            investigator_et_weight.setError(getText(R.string.error_weight_required));
            investigator_et_weight.requestFocus();
            return;
        }

        if (eyeColor.isEmpty()) {
            investigator_et_eyeColor.setError(getText(R.string.error_eyeColor_required));
            investigator_et_eyeColor.requestFocus();
            return;
        }

        if (hairColor.isEmpty()) {
            investigator_et_hairColor.setError(getText(R.string.error_hairColor_required));
            investigator_et_hairColor.requestFocus();
            return;
        }

        if (phy_appearance.isEmpty()) {
            investigator_et_phy_appearance.setError(getText(R.string.error_phy_appearance_required));
            investigator_et_phy_appearance.requestFocus();
            return;
        }else {

            investigator_progressBar.setVisibility(View.VISIBLE);

            CollectionReference collectionReference = firebaseFirestore.collection(INVESTIGATORS);

            String investigator_id = collectionReference.document().getId();

            Investigator investigator = new Investigator(investigator_id, firstName, lastName, parentName, fullName, birthday, address, eyeColor, hairColor, phy_appearance,
                    null, DateHelper.getDateTime(), age, gender, height, weight);


            firebaseFirestore.collection(INVESTIGATORS).document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Toast.makeText(mContext, mContext.getText(R.string.this_investigator_with_name) + " " + fullName + " "+ mContext.getText(R.string.exists_in_database_please_add_example), Toast.LENGTH_LONG).show();
                        investigator_progressBar.setVisibility(View.GONE);
                        registerInvestigator.setEnabled(true);
                    } else {
                        firebaseFirestore.collection(INVESTIGATORS).document(investigator_id).set(investigator).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, mContext.getText(R.string.this_investigator_with_name) + " " + fullName + " " + mContext.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                Bundle viewBundle = new Bundle();
                                viewBundle.putString("investigator_id", investigator_id);
                                viewBundle.putString("fullName", fullName);
                                SetProfileInvestigatorFragment setProfileInvestigatorFragment = new SetProfileInvestigatorFragment();
                                setProfileInvestigatorFragment.setArguments(viewBundle);
                                loadFragment(setProfileInvestigatorFragment);
                                registerInvestigator.setEnabled(true);
                                investigator_progressBar.setVisibility(View.INVISIBLE);
                                setEmptyField();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, mContext.getText(R.string.investigator_failed_to_register), Toast.LENGTH_LONG).show();
                                registerInvestigator.setEnabled(true);
                                investigator_progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            });

        }
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}