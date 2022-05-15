package fiek.unipr.mostwantedapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import fiek.unipr.mostwantedapp.helpers.PersonImage;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Person;

public class RegisterPerson extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "regPersonPreference";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    private StorageReference storageReference;
    private EditText et_fullName, et_address, et_age, et_height, et_weight, et_eyeColor, et_hairColor, et_phy_appearance, et_acts, et_status;
    private Button registerPerson;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        registerPerson = findViewById(R.id.registerPerson);
        registerPerson.setOnClickListener(this);

        et_fullName = findViewById(R.id.et_fullName);
        et_address = findViewById(R.id.et_address);
        et_status = findViewById(R.id.et_status);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        et_eyeColor = findViewById(R.id.et_eyeColor);
        et_hairColor = findViewById(R.id.et_hairColor);
        et_phy_appearance = findViewById(R.id.et_phy_appearance);
        et_acts = findViewById(R.id.et_acts);
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
                registerPerson();
                break;
        }
    }

    private void registerPerson() {
        Integer checkNull = null;
        String fullName = et_fullName.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        Integer age = Integer.parseInt(et_age.getText().toString().trim());
        Integer height = Integer.parseInt(et_height.getText().toString().trim());
        Integer weight = Integer.parseInt(et_weight.getText().toString().trim());
        String eyeColor = et_eyeColor.getText().toString().trim();
        String hairColor = et_hairColor.getText().toString().trim();
        String phy_appearance = et_phy_appearance.getText().toString().trim();
        String acts = et_acts.getText().toString().trim();
        String status = et_status.getText().toString().trim();

        if(fullName.isEmpty())
        {
            et_fullName.setError(getText(R.string.error_fullname_required));
            et_fullName.requestFocus();
            return;
        }

        if(address.isEmpty())
        {
            et_address.setError(getText(R.string.error_address_required));
            et_address.requestFocus();
            return;
        }

        if(age.equals(checkNull))
        {
            et_age.setError(getText(R.string.error_age_required));
            et_age.requestFocus();
            return;
        }

        if(height.equals(checkNull))
        {
            et_height.setError(getText(R.string.error_height_required));
            et_height.requestFocus();
            return;
        }

        if(weight.equals(checkNull))
        {
            et_weight.setError(getText(R.string.error_weight_required));
            et_weight.requestFocus();
            return;
        }

        if(eyeColor.isEmpty())
        {
            et_eyeColor.setError(getText(R.string.error_eyeColor_required));
            et_eyeColor.requestFocus();
            return;
        }

        if(hairColor.isEmpty())
        {
            et_hairColor.setError(getText(R.string.error_hairColor_required));
            et_hairColor.requestFocus();
            return;
        }

        if(phy_appearance.isEmpty())
        {
            et_phy_appearance.setError(getText(R.string.error_phy_appearance_required));
            et_phy_appearance.requestFocus();
            return;
        }

        if(acts.isEmpty())
        {
            et_acts.setError(getText(R.string.error_acts_required));
            et_acts.requestFocus();
            return;
        }

        if(status.isEmpty())
        {
            et_status.setError(getText(R.string.error_status_required));
            et_status.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Person person = new Person(fullName, address, eyeColor, hairColor, phy_appearance, acts, null, status, age, height, weight);

        firebaseFirestore.collection("wanted_persons").document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    Toast.makeText(RegisterPerson.this, "This person with this name: "+fullName+" exists in database, please add ex: Filan Fisteku 1", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    firebaseFirestore.collection("wanted_persons").document(fullName).set(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterPerson.this, RegisterPerson.this.getText(R.string.this_person_with_this)+" "+fullName+" "+RegisterPerson.this.getText(R.string.was_registered_successfully), Toast.LENGTH_LONG).show();
                            Intent registerPerson = new Intent(RegisterPerson.this, PersonImage.class);
                            Bundle personBundle = new Bundle();
                            personBundle.putString("personFullName", fullName);
                            registerPerson.putExtras(personBundle);
                            progressBar.setVisibility(View.GONE);
                            startActivity(registerPerson);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterPerson.this, R.string.person_failed_to_register, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

}