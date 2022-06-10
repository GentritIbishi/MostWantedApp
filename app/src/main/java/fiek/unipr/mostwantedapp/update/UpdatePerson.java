package fiek.unipr.mostwantedapp.update;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;

public class UpdatePerson extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    Location mlocation;
    private String informer_fullName;
    Double latitude, longitude;
    String fullName, address, eyeColor, hairColor, phy_appearance, acts, urlOfProfile, status;
    Integer age, height, weight;
    ImageView imgPerson_update, img_setNewProfile, img_delete, img_up, img_send_location;
    EditText et_person_fullName_update, et_person_address_update, et_person_age_update, et_person_height_update, et_person_weight_update, et_person_eyeColor_update,
            et_person_hairColor_update, et_person_phy_appearance_update, et_person_acts_update, et_person_status_update;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_person);

        imgPerson_update = findViewById(R.id.imgPerson_update);
        img_send_location = findViewById(R.id.img_send_location);
        et_person_fullName_update = findViewById(R.id.et_person_fullName_update);
        et_person_address_update = findViewById(R.id.et_person_address_update);
        et_person_age_update = findViewById(R.id.et_person_age_update);
        et_person_height_update = findViewById(R.id.et_person_height_update);
        et_person_weight_update = findViewById(R.id.et_person_weight_update);
        et_person_eyeColor_update = findViewById(R.id.et_person_eyeColor_update);
        et_person_hairColor_update = findViewById(R.id.et_person_hairColor_update);
        et_person_phy_appearance_update = findViewById(R.id.et_person_phy_appearance_update);
        et_person_acts_update = findViewById(R.id.et_person_acts_update);
        et_person_status_update = findViewById(R.id.et_person_status_update);
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Date date = Calendar.getInstance().getTime();
        disableEditText(et_person_fullName_update);
        disableEditText(et_person_acts_update);
        disableEditText(et_person_address_update);
        disableEditText(et_person_age_update);
        disableEditText(et_person_eyeColor_update);
        disableEditText(et_person_hairColor_update);
        disableEditText(et_person_height_update);
        disableEditText(et_person_phy_appearance_update);
        disableEditText(et_person_weight_update);
        disableEditText(et_person_status_update);

        Bundle personInfos = getIntent().getExtras();
        if (personInfos != null) {
            fullName = personInfos.get("fullName").toString();
            acts = personInfos.get("acts").toString();
            address = personInfos.get("address").toString();
            age = personInfos.getInt("age");
            address = personInfos.get("address").toString();
            eyeColor = personInfos.get("eyeColor").toString();
            height = personInfos.getInt("height");
            phy_appearance = personInfos.get("phy_appearance").toString();
            status = personInfos.get("status").toString();
            urlOfProfile = personInfos.get("urlOfProfile").toString();
            weight = personInfos.getInt("weight");
            hairColor = personInfos.get("hairColor").toString();
        } else {
            fullName = null;
            acts = null;
            address = null;
            age = null;
            address = null;
            eyeColor = null;
            height = null;
            phy_appearance = null;
            status = null;
            urlOfProfile = null;
            weight = null;
            hairColor = null;
        }

        StorageReference profileRef = storageReference.child("persons/" + fullName + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(imgPerson_update);
            }
        });

        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Looper looper = null;

        img_send_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UpdatePerson.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdatePerson.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                    ActivityCompat.requestPermissions(UpdatePerson.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                    try {
                        DocumentReference locationReports = firebaseFirestore.collection("wanted_persons").document(fullName).collection("location_reports").document(getTimeDate());
                        Map<String, Object> location = new HashMap<>();
                        location.put("latitude", mlocation.getLatitude());
                        location.put("longitude", mlocation.getLongitude());

                        locationReports.set(location).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UpdatePerson.this, "Saved on location reports!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdatePerson.this, "Not saved on location reports!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //qyty ruhet lastestLocation report

                        documentReference = firebaseFirestore.collection("wanted_persons").document(fullName);
                        documentReference.update("latitude", mlocation.getLatitude());
                        documentReference.update("longitude", mlocation.getLongitude());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdatePerson.this, R.string.thank_location, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 5000);

                }
            }
        });

    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mlocation = location;
            Log.d("Location Changes", location.toString());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            DocumentReference docRef = firebaseFirestore.collection("wanted_persons").document(fullName);
            docRef.update("latitude", latitude);
            docRef.update("longitude", longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };

    private void setProfile() {

        et_person_fullName_update.setText(fullName);
        et_person_acts_update.setText(acts);
        et_person_address_update.setText(address);
        et_person_eyeColor_update.setText(eyeColor);
        et_person_height_update.setText(String.valueOf(height));
        et_person_age_update.setText(String.valueOf(age));
        et_person_phy_appearance_update.setText(phy_appearance);
        et_person_status_update.setText(status);
        et_person_hairColor_update.setText(hairColor);
        et_person_weight_update.setText(String.valueOf(weight));
        Picasso.get().load(urlOfProfile).transform(new CircleTransform()).into(imgPerson_update);

    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }



    @Override
    protected void onStart() {
        super.onStart();
        setProfile();
    }

    private void test() {
        if(firebaseAuth != null){
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();
            checkIfUserExist(uid);

            //kina me bo check nese useri eshte regular user me emer mbiemer bla bla
            //ose useri eshte i logirat me phone
            // ose useri eshte i logirat si anonymous

            // ja shton lokacionit emrin qe e ke marr informer_fullName

           // DocumentReference locationReport = firebaseFirestore.collection("wanted_persons").document(fullName).collection("location_reports").document(getTimeDate());
//            Report report = new Report(null, getTimeDate(), user.getUid(), informer_fullName, ReportStatus.UNVERIFIED, mlocation.getLongitude(), mlocation.getLatitude());

        }

    }


    private void checkIfUserExist(String uid) {
        firebaseFirestore.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult() != null){
                            Toast.makeText(UpdatePerson.this, "User exist!", Toast.LENGTH_SHORT).show();
                            getFullNameOfUser(task.getResult().getString("fullName"));
                        }else {
                            Toast.makeText(UpdatePerson.this, R.string.user_not_exist, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getFullNameOfUser(String full_name) {
        informer_fullName = full_name;
        return informer_fullName;
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