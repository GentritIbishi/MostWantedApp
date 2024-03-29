package fiek.unipr.mostwantedapp.fragment.admin.register.person;

import static fiek.unipr.mostwantedapp.utils.Constants.AGE;
import static fiek.unipr.mostwantedapp.utils.Constants.CM;
import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.FREE;
import static fiek.unipr.mostwantedapp.utils.Constants.KG;
import static fiek.unipr.mostwantedapp.utils.Constants.LATITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.LONGITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;
import static fiek.unipr.mostwantedapp.utils.StringHelper.empty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
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

import java.util.Arrays;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.DateInputMask;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class RegisterPersonFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View register_person_view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MaterialAutoCompleteTextView et_age, et_gender, et_height, et_weight, et_eyeColor, et_hairColor, et_phy_appearance, et_status, et_prize;
    private TextInputEditText et_firstName, et_lastName, et_address, et_parentName, et_birthday;
    private MultiAutoCompleteTextView et_acts;
    private Button registerPerson;
    private ProgressBar progressBar;
    private String[] WEIGHT_ARRAY = null;
    private String[] HEIGHT_ARRAY = null;
    private String[] AGE_ARRAY = null;
    private String[] PRIZE_ARRAY = null;

    public RegisterPersonFragment() {}

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
        register_person_view = inflater.inflate(R.layout.fragment_register_person, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        setWeightInMeasureArray(KG);
        setHeightInMeasureArray(CM);
        setAgeArray();
        setPrizeInYourCurrency(EURO);

        registerPerson = register_person_view.findViewById(R.id.registerPerson);
        registerPerson.setOnClickListener(this);

        et_firstName = register_person_view.findViewById(R.id.et_firstName);
        et_lastName = register_person_view.findViewById(R.id.et_lastName);
        et_parentName = register_person_view.findViewById(R.id.et_parentName);
        et_address = register_person_view.findViewById(R.id.et_address);
        et_birthday = register_person_view.findViewById(R.id.et_birthday);
        new DateInputMask(et_birthday);

        et_age = register_person_view.findViewById(R.id.et_age);
        ArrayAdapter<String> age_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, AGE_ARRAY);
        et_age.setAdapter(age_adapter);

        et_gender = register_person_view.findViewById(R.id.et_gender);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        et_gender.setAdapter(gender_adapter);

        et_prize = register_person_view.findViewById(R.id.et_prize);
        ArrayAdapter<String> prize_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, PRIZE_ARRAY);
        et_prize.setAdapter(prize_adapter);

        et_height = register_person_view.findViewById(R.id.et_height);
        ArrayAdapter<String> height_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        et_height.setAdapter(height_adapter);

        et_weight = register_person_view.findViewById(R.id.et_weight);
        ArrayAdapter<String> weight_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        et_weight.setAdapter(weight_adapter);

        et_eyeColor = register_person_view.findViewById(R.id.et_eyeColor);
        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        et_eyeColor.setAdapter(eye_color_adapter);

        et_hairColor = register_person_view.findViewById(R.id.et_hairColor);
        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        et_hairColor.setAdapter(hair_color_adapter);

        et_phy_appearance = register_person_view.findViewById(R.id.et_phy_appearance);
        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        et_phy_appearance.setAdapter(phy_appearance_adapter);

        et_acts = register_person_view.findViewById(R.id.et_acts);
        ArrayAdapter<String> acts_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.acts));
        et_acts.setAdapter(acts_adapter);
        et_acts.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        et_status = register_person_view.findViewById(R.id.et_status);
        ArrayAdapter<String> status_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.status_of_person));
        et_status.setAdapter(status_adapter);

        progressBar = register_person_view.findViewById(R.id.progressBar);

        return register_person_view;
    }

    private void setPrizeInYourCurrency(String euro) {
        PRIZE_ARRAY = new String[2001];
        for(int i=0; i<2001; i++) {
            PRIZE_ARRAY[i] = i*500+" "+euro;
        }
        PRIZE_ARRAY[0] = FREE;
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
            AGE_ARRAY[i] = i+" "+AGE;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.registerPerson:
                registerPerson.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                registerPerson();
                break;
        }
    }

    private void setEmptyField() {
        et_firstName.setText("");
        et_lastName.setText("");
        et_parentName.setText("");
        et_address.setText("");
        et_age.setText("");
        et_gender.setText("");
        et_height.setText("");
        et_weight.setText("");
        et_eyeColor.setText("");
        et_hairColor.setText("");
        et_phy_appearance.setText("");
        et_acts.setText("");
        et_status.setText("");
        et_birthday.setText("");
        et_prize.setText("");
    }

    private void registerPerson() {
        String firstName = et_firstName.getText().toString().trim();
        String lastName = et_lastName.getText().toString().trim();
        String parentName = et_parentName.getText().toString().trim();
        String fullName = firstName+" "+"("+parentName+")"+" "+lastName;
        String birthday = et_birthday.getText().toString();
        String address = et_address.getText().toString().trim();
        String age = et_age.getText().toString().trim();
        String gender = et_gender.getText().toString().trim();
        String height = et_height.getText().toString().trim();
        String weight = et_weight.getText().toString().trim();
        String eyeColor = et_eyeColor.getText().toString().trim();
        String hairColor = et_hairColor.getText().toString().trim();
        String phy_appearance = et_phy_appearance.getText().toString().trim();
        String act = StringHelper.removeLastChar(et_acts.getText().toString().trim());
        // Trafficking in human beings, Narcotics trafficking, Narcotics trafficking, Robbery,
        String[] array = act.split(",");
        List<String> acts = Arrays.asList(array);
        String status = et_status.getText().toString().trim();
        String prize = et_prize.getText().toString().trim();


        if (firstName.isEmpty()) {
            et_firstName.setError(getText(R.string.error_first_name_required));
            et_firstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            et_lastName.setError(getText(R.string.error_last_name_required));
            et_lastName.requestFocus();
            return;
        }

        if (parentName.isEmpty()) {
            et_parentName.setError(getText(R.string.error_parent_name_required));
            et_parentName.requestFocus();
            return;
        }

        if (birthday.isEmpty()) {
            et_birthday.setError(getText(R.string.error_birthday_required));
            et_birthday.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            et_address.setError(getText(R.string.error_address_required));
            et_address.requestFocus();
            return;
        }

        if (empty(age)) {
            et_age.setError(getText(R.string.error_age_required));
            et_age.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(gender)) {
            et_gender.setError(getText(R.string.error_gender_required));
            et_gender.requestFocus();
            return;
        }

        if (empty(height)) {
            et_height.setError(getText(R.string.error_height_required));
            et_height.requestFocus();
            return;
        }

        if (empty(weight)) {
            et_weight.setError(getText(R.string.error_weight_required));
            et_weight.requestFocus();
            return;
        }

        if (eyeColor.isEmpty()) {
            et_eyeColor.setError(getText(R.string.error_eyeColor_required));
            et_eyeColor.requestFocus();
            return;
        }

        if (hairColor.isEmpty()) {
            et_hairColor.setError(getText(R.string.error_hairColor_required));
            et_hairColor.requestFocus();
            return;
        }

        if (phy_appearance.isEmpty()) {
            et_phy_appearance.setError(getText(R.string.error_phy_appearance_required));
            et_phy_appearance.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(act)) {
            et_acts.setError(getText(R.string.error_acts_required));
            et_acts.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            et_status.setError(getText(R.string.error_status_required));
            et_status.requestFocus();
            return;
        }
        if (prize.isEmpty()) {
            et_prize.setError(getText(R.string.error_prize_required));
            et_prize.requestFocus();
            return;
        }else {

            progressBar.setVisibility(View.VISIBLE);

            CollectionReference collRef = firebaseFirestore.collection(WANTED_PERSONS);
            String personId = collRef.document().getId();

            Person person = new Person(personId,
                    firstName, lastName, parentName,
                    fullName, birthday, address, eyeColor,
                    hairColor, phy_appearance,
                    null, status, prize,
                    DateHelper.getDateTime(), age, gender,
                    height, weight, acts, LONGITUDE_DEFAULT, LATITUDE_DEFAULT);

            firebaseFirestore.collection(WANTED_PERSONS).document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Toast.makeText(mContext, mContext.getText(R.string.this_person_with_this) + " " + fullName + " "+ mContext.getText(R.string.exists_in_database_please_add_example), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        registerPerson.setEnabled(true);
                    } else {
                        firebaseFirestore.collection(WANTED_PERSONS).document(personId).set(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, mContext.getText(R.string.this_person_with_this) + " " + fullName + " " + mContext.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                Bundle viewBundle = new Bundle();
                                viewBundle.putString("personId", personId);
                                viewBundle.putString("fullName", fullName);
                                SetProfilePersonFragment setProfilePersonFragment = new SetProfilePersonFragment();
                                setProfilePersonFragment.setArguments(viewBundle);
                                loadFragment(setProfilePersonFragment);
                                registerPerson.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
                                setEmptyField();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, mContext.getText(R.string.person_failed_to_register), Toast.LENGTH_LONG).show();
                                registerPerson.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
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