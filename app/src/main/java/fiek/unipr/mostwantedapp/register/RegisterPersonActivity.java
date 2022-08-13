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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.profile.SetProfilePersonActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Person;

public class RegisterPersonActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KG = "KG";
    public static final String CM = "CM";
    public static final String AGE = "AGE";
    public static final String EURO = "EURO";
    public static final Double LATITUDE_DEFAULT = 0.00000;
    public static final Double LONGITUDE_DEFAULT = 0.00000;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private MaterialAutoCompleteTextView et_age, et_height, et_weight, et_eyeColor, et_hairColor, et_phy_appearance, et_acts, et_status, et_prize;
    private TextInputEditText et_firstName, et_lastName, et_address, et_parentName;
    private Button registerPerson;
    private ProgressBar progressBar;
    private String[] WEIGHT_ARRAY = null;
    private String[] HEIGHT_ARRAY = null;
    private String[] AGE_ARRAY = null;
    private String[] PRIZE_ARRAY = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        setWeightInMeasureArray(KG);
        setHeightInMeasureArray(CM);
        setAgeArray();
        setPrizeInYourCurrency(EURO);

        registerPerson = findViewById(R.id.registerPerson);
        registerPerson.setOnClickListener(this);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_parentName = findViewById(R.id.et_parentName);
        et_address = findViewById(R.id.et_address);

        et_age = findViewById(R.id.et_age);
        ArrayAdapter<String> age_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, AGE_ARRAY);
        et_age.setAdapter(age_adapter);

        et_prize = findViewById(R.id.et_prize);
        ArrayAdapter<String> prize_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, PRIZE_ARRAY);
        et_prize.setAdapter(prize_adapter);

        et_height = findViewById(R.id.et_height);
        ArrayAdapter<String> height_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, HEIGHT_ARRAY);
        et_height.setAdapter(height_adapter);

        et_weight = findViewById(R.id.et_weight);
        ArrayAdapter<String> weight_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, WEIGHT_ARRAY);
        et_weight.setAdapter(weight_adapter);

        et_eyeColor = findViewById(R.id.et_eyeColor);
        ArrayAdapter<String> eye_color_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eye_color));
        et_eyeColor.setAdapter(eye_color_adapter);

        et_hairColor = findViewById(R.id.et_hairColor);
        ArrayAdapter<String> hair_color_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hair_color));
        et_hairColor.setAdapter(hair_color_adapter);

        et_phy_appearance = findViewById(R.id.et_phy_appearance);
        ArrayAdapter<String> phy_appearance_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.physical_appearance));
        et_phy_appearance.setAdapter(phy_appearance_adapter);

        et_acts = findViewById(R.id.et_acts);
        ArrayAdapter<String> acts_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.acts));
        et_acts.setAdapter(acts_adapter);

        et_status = findViewById(R.id.et_status);
        ArrayAdapter<String> status_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.status_of_person));
        et_status.setAdapter(status_adapter);

        progressBar = findViewById(R.id.progressBar);

    }

    private void setPrizeInYourCurrency(String euro) {
        PRIZE_ARRAY = new String[2001];
        for(int i=500; i<1000000; i=i+500) {
            PRIZE_ARRAY[i] = i+" "+euro;
        }
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
        et_height.setText("");
        et_weight.setText("");
        et_eyeColor.setText("");
        et_hairColor.setText("");
        et_phy_appearance.setText("");
        et_acts.setText("");
        et_status.setText("");
    }

    private void registerPerson() {
        Integer checkNull = null;
        String firstName = et_firstName.getText().toString().trim();
        String lastName = et_lastName.getText().toString().trim();
        String parentName = et_parentName.getText().toString().trim();
        String fullName = firstName+" ("+parentName+") "+lastName;
        String address = et_address.getText().toString().trim();
        Integer age = Integer.parseInt(et_age.getText().toString().trim());
        Integer height = Integer.parseInt(et_height.getText().toString().trim());
        Integer weight = Integer.parseInt(et_weight.getText().toString().trim());
        String eyeColor = et_eyeColor.getText().toString().trim();
        String hairColor = et_hairColor.getText().toString().trim();
        String phy_appearance = et_phy_appearance.getText().toString().trim();
        String acts = et_acts.getText().toString().trim();
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

        if (address.isEmpty()) {
            et_address.setError(getText(R.string.error_address_required));
            et_address.requestFocus();
            return;
        }

        if (age.equals(checkNull)) {
            et_age.setError(getText(R.string.error_age_required));
            et_age.requestFocus();
            return;
        }

        if (height.equals(checkNull)) {
            et_height.setError(getText(R.string.error_height_required));
            et_height.requestFocus();
            return;
        }

        if (weight.equals(checkNull)) {
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

        if (acts.isEmpty()) {
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

            Person person = new Person(firstName, lastName, parentName, fullName, address, eyeColor, hairColor, phy_appearance, acts, null, status, prize, getTimeDate(), age, height, weight, LONGITUDE_DEFAULT, LATITUDE_DEFAULT);

            firebaseFirestore.collection("wanted_persons").document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        Toast.makeText(RegisterPersonActivity.this, RegisterPersonActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " "+ RegisterPersonActivity.this.getText(R.string.exists_in_database_please_add_example), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        registerPerson.setEnabled(true);
                    } else {
                        firebaseFirestore.collection("wanted_persons").document(fullName).set(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterPersonActivity.this, RegisterPersonActivity.this.getText(R.string.this_person_with_this) + " " + fullName + " " + RegisterPersonActivity.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                                Intent setImageOfPerson = new Intent(RegisterPersonActivity.this, SetProfilePersonActivity.class);
                                Bundle personBundle = new Bundle();
                                personBundle.putString("personFullName", fullName);
                                setImageOfPerson.putExtras(personBundle);
                                registerPerson.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(setImageOfPerson);
                                setEmptyField();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterPersonActivity.this, R.string.person_failed_to_register, Toast.LENGTH_LONG).show();
                                registerPerson.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
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