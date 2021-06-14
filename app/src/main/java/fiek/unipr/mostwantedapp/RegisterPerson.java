package fiek.unipr.mostwantedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterPerson extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    ImageView imgOfPerson;
    private EditText et_fullName, et_address, et_age, et_height, et_weight, et_eyeColor, et_hairColor, et_phy_appearance, et_acts;
    private Button registerPerson;
    private TextView labelInfoRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        mAuth = FirebaseAuth.getInstance();

        registerPerson = (Button) findViewById(R.id.registerPerson);
        registerPerson.setOnClickListener(this);

        et_fullName = (EditText) findViewById(R.id.et_fullName);
        et_address = (EditText) findViewById(R.id.et_address);
        et_age = (EditText) findViewById(R.id.et_age);
        et_height = (EditText) findViewById(R.id.et_height);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_eyeColor = (EditText) findViewById(R.id.et_eyeColor);
        et_hairColor = (EditText) findViewById(R.id.et_hairColor);
        et_phy_appearance = (EditText) findViewById(R.id.et_phy_appearance);
        et_acts = (EditText) findViewById(R.id.et_acts);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);


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

        String fullName = et_fullName.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String age = et_age.getText().toString().trim();
        String height = et_height.getText().toString().trim();
        String weight = et_weight.getText().toString().trim();
        String eyeColor = et_eyeColor.getText().toString().trim();
        String hairColor = et_hairColor.getText().toString().trim();
        String phy_appearance = et_phy_appearance.getText().toString().trim();
        String acts = et_acts.getText().toString().trim();

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

        if(age.isEmpty())
        {
            et_age.setError(getText(R.string.error_age_required));
            et_age.requestFocus();
            return;
        }

        if(height.isEmpty())
        {
            et_height.setError(getText(R.string.error_height_required));
            et_height.requestFocus();
            return;
        }

        if(weight.isEmpty())
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

        //progressBar.setVisibility(View.VISIBLE);





    }
}