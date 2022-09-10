package fiek.unipr.mostwantedapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.DateInputMask;
import fiek.unipr.mostwantedapp.profile.SetProfileInvestigatorActivity;
import fiek.unipr.mostwantedapp.models.Investigator;

public class RegisterInvestigatorActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KG = "KG";
    public static final String CM = "CM";
    public static final String AGE = "AGE";
    public static final String EURO = "EURO";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MaterialAutoCompleteTextView investigator_et_age, investigator_et_gender,
            investigator_et_height, investigator_et_weight,
            investigator_et_eyeColor, investigator_et_hairColor,
            investigator_et_phy_appearance, investigator_et_acts,
            investigator_et_status, investigator_et_prize;
    private TextInputEditText investigator_et_firstName, investigator_et_lastName,
            investigator_et_address, investigator_et_parentName, investigator_et_birthday;
    private Button registerInvestigator;
    private ProgressBar investigator_progressBar;
    private String[] WEIGHT_ARRAY = null;
    private String[] HEIGHT_ARRAY = null;
    private String[] AGE_ARRAY = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_investigator);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        setWeightInMeasureArray(KG);
        setHeightInMeasureArray(CM);
        setAgeArray();

        registerInvestigator = findViewById(R.id.registerInvestigator);
        registerInvestigator.setOnClickListener(this);

        investigator_et_firstName = findViewById(R.id.investigator_et_firstName);
        investigator_et_lastName = findViewById(R.id.investigator_et_lastName);
        investigator_et_parentName = findViewById(R.id.investigator_et_parentName);
        investigator_et_address = findViewById(R.id.investigator_et_address);
        investigator_et_birthday = findViewById(R.id.investigator_et_birthday);
        new DateInputMask(investigator_et_birthday);

        investigator_et_age = findViewById(R.id.investigator_et_age);
        ArrayAdapter<String> age_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, AGE_ARRAY);
        investigator_et_age.setAdapter(age_adapter);

        investigator_et_gender = findViewById(R.id.investigator_et_gender);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_array));
        investigator_et_gender.setAdapter(gender_adapter);

        investigator_et_height = findViewById(R.id.investigator_et_height);
        ArrayAdapter<String> height_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        investigator_et_height.setAdapter(height_adapter);

        investigator_et_weight = findViewById(R.id.investigator_et_weight);
        ArrayAdapter<String> weight_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        investigator_et_weight.setAdapter(weight_adapter);

        investigator_et_eyeColor = findViewById(R.id.investigator_et_eyeColor);
        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        investigator_et_eyeColor.setAdapter(eye_color_adapter);

        investigator_et_hairColor = findViewById(R.id.investigator_et_hairColor);
        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        investigator_et_hairColor.setAdapter(hair_color_adapter);

        investigator_et_phy_appearance = findViewById(R.id.investigator_et_phy_appearance);
        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        investigator_et_phy_appearance.setAdapter(phy_appearance_adapter);

        investigator_progressBar = findViewById(R.id.investigator_progressBar);
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
            AGE_ARRAY[i] = i+" "+getApplicationContext().getText(R.string.age);
        }
    }

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
        Integer checkNull = null;
        String firstName = investigator_et_firstName.getText().toString().trim();
        String lastName = investigator_et_lastName.getText().toString().trim();
        String parentName = investigator_et_parentName.getText().toString().trim();
        String fullName = firstName+" ("+parentName+") "+lastName;
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

        if (age.equals(checkNull)) {
            investigator_et_age.setError(getText(R.string.error_age_required));
            investigator_et_age.requestFocus();
            return;
        }

        if (gender.equals(checkNull)) {
            investigator_et_gender.setError(getText(R.string.error_gender_required));
            investigator_et_gender.requestFocus();
            return;
        }

        if (height.equals(checkNull)) {
            investigator_et_height.setError(getText(R.string.error_height_required));
            investigator_et_height.requestFocus();
            return;
        }

        if (weight.equals(checkNull)) {
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

            CollectionReference collectionReference = firebaseFirestore.collection("investigators");

            String investigator_id = collectionReference.document().getId();

            Investigator investigator = new Investigator(investigator_id, firstName, lastName, parentName, fullName, birthday, address, eyeColor, hairColor, phy_appearance,
                    null, getTimeDate(), age, gender, height, weight);


            firebaseFirestore.collection("investigators").document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Toast.makeText(RegisterInvestigatorActivity.this, RegisterInvestigatorActivity.this.getText(R.string.this_investigator_with_name) + " " + fullName + " "+ RegisterInvestigatorActivity.this.getText(R.string.exists_in_database_please_add_example), Toast.LENGTH_LONG).show();
                        investigator_progressBar.setVisibility(View.GONE);
                        registerInvestigator.setEnabled(true);
                    } else {
                        firebaseFirestore.collection("investigators").document(investigator_id).set(investigator).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterInvestigatorActivity.this, RegisterInvestigatorActivity.this.getText(R.string.this_investigator_with_name) + " " + fullName + " " + RegisterInvestigatorActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                Intent setImageOfInvestigator = new Intent(RegisterInvestigatorActivity.this, SetProfileInvestigatorActivity.class);
                                Bundle registerInvestigatorBundle = new Bundle();
                                registerInvestigatorBundle.putString("investigator_id", investigator_id);
                                registerInvestigatorBundle.putString("fullName", fullName);
                                setImageOfInvestigator.putExtras(registerInvestigatorBundle);
                                registerInvestigator.setEnabled(true);
                                investigator_progressBar.setVisibility(View.INVISIBLE);
                                startActivity(setImageOfInvestigator);
                                setEmptyField();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterInvestigatorActivity.this, R.string.investigator_failed_to_register, Toast.LENGTH_LONG).show();
                                registerInvestigator.setEnabled(true);
                                investigator_progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            });

        }
    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}