package fiek.unipr.mostwantedapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import fiek.unipr.mostwantedapp.helpers.PersonImage;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Person;

public class RegisterPersonActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "regPersonPreference";
    public static final Double LATITUDE_DEFAULT = 0.00000;
    public static final Double LONGITUDE_DEFAULT = 0.00000;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    private StorageReference storageReference;
    private MaterialAutoCompleteTextView et_age, et_height, et_weight, et_eyeColor, et_hairColor, et_phy_appearance, et_acts, et_status;
    private EditText et_firstName, et_lastName, et_address, et_parentName;
    private Button registerPerson;
    private ProgressBar progressBar;
    private static final Integer AGE_ARRAY[] = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
            86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102,
            103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
            117, 118, 119, 120 };
    private static final Integer HEIGHT_IN_CM_ARRAY[] = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
            86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102,
            103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
            117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130,
            131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
            145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158,
            159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172,
            173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186,
            187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200,
            201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214,
            215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228,
            229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242,
            243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256,
            257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270,
            271, 272};

    private static final Integer WEIGHT_IN_KG_ARRAY[] = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
            86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102,
            103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
            117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130,
            131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
            145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158,
            159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172,
            173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186,
            187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        registerPerson = findViewById(R.id.registerPerson);
        registerPerson.setOnClickListener(this);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_parentName = findViewById(R.id.et_parentName);
        et_address = findViewById(R.id.et_address);

        et_age = findViewById(R.id.et_age);
        ArrayAdapter<Integer> age_adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, AGE_ARRAY);
        et_age.setAdapter(age_adapter);

        et_height = findViewById(R.id.et_height);
        ArrayAdapter<Integer> height_adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, HEIGHT_IN_CM_ARRAY);
        et_height.setAdapter(height_adapter);

        et_weight = findViewById(R.id.et_weight);
        ArrayAdapter<Integer> weight_adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, WEIGHT_IN_KG_ARRAY);
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
        ArrayAdapter<String> status_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.status));
        et_status.setAdapter(status_adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = findViewById(R.id.progressBar);

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
        } else {

            progressBar.setVisibility(View.VISIBLE);

            Person person = new Person(firstName, lastName, parentName, fullName, address, eyeColor, hairColor, phy_appearance, acts, null, status, age, height, weight, LONGITUDE_DEFAULT, LATITUDE_DEFAULT);

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
                                Intent setImageOfPerson = new Intent(RegisterPersonActivity.this, PersonImage.class);
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

}