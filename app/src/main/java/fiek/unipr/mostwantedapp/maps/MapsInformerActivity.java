package fiek.unipr.mostwantedapp.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsInformerBinding;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;
import fiek.unipr.mostwantedapp.update.UpdatePerson;

public class MapsInformerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private ActivityMapsInformerBinding binding;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PROFILE_INFORMER_PREFS = "profileInformerPreferences";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int IMAGE_CODE = 15;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private GoogleMap mMap;
    private boolean locationPermissionGranted = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private String wanted_person, acts, address, eyeColor, hairColor, phy_appearance, status, urlOfProfile;
    private String dateNtime;
    private Integer age, height, weight;
    private Double latitude, longitude;
    private String description= "No description!";
    private String informer_person = "ANONYMOUS";
    private MarkerOptions markerOptionsDefault;
    private Marker marker;
    private Map<String, Object> images = new HashMap<>();
    private Bundle mapsInformerBundle;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if (locationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }else {
            Toast.makeText(this, "No permit!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsInformerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.LocationReport.setVisibility(View.VISIBLE);
        binding.OtherReports.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mapsInformerBundle = new Bundle();
        getFromBundle(mapsInformerBundle);

        getUIDorPHONE();
        getInformerFullName(firebaseAuth.getCurrentUser().getUid());

        getLocationPermission();

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lat long i din ni check if not null eshte mire
                if(latitude != null && longitude != null){
                    binding.LocationReport.setVisibility(View.GONE);
                    binding.OtherReports.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(MapsInformerActivity.this, R.string.location_not_set_please_set_new_location, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), IMAGE_CODE);
            }
        });

        binding.btReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btReport.setEnabled(false);
                binding.reportProgressBar.setVisibility(View.VISIBLE);

                if(dateNtime != null) {
                    //ktu merren komplet infot edhe bohet regjistrimi
                    DocumentReference docRef = firebaseFirestore
                            .collection("locations_reports")
                            .document(dateNtime);

                    Report report = new Report(binding.etDescription.getText().toString(),
                            dateNtime,
                            firebaseAuth.getCurrentUser().getUid(),
                            informer_person,
                            wanted_person,
                            ReportStatus.UNVERIFIED,
                            longitude,
                            latitude,
                            images);

                    docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful() && task !=null) {
                                //task succesfull
                                //report succesfull finish activity qoje user te homefragment
                                //ni progress bar duhet ktu me bindirat
                                setLastSeenLocation(latitude, longitude, wanted_person);
                                finish();
                                Toast.makeText(MapsInformerActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }else {
                                //task not successfull dhe null
                                Toast.makeText(MapsInformerActivity.this, R.string.failed_to_register, Toast.LENGTH_SHORT).show();
                                binding.btReport.setEnabled(true);
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else {
                    dateNtime = getTimeDate();
                    //ktu merren komplet infot edhe bohet regjistrimi
                    DocumentReference docRef = firebaseFirestore
                            .collection("locations_reports")
                            .document(dateNtime);

                    //thirri metodat per me set emrin, cila bon e bon set
                    getUIDorPHONE();
                    getInformerFullName(firebaseAuth.getCurrentUser().getUid());

                    Report report = new Report(binding.etDescription.getText().toString(),
                            dateNtime,
                            firebaseAuth.getCurrentUser().getUid(),
                            informer_person,
                            wanted_person,
                            ReportStatus.UNVERIFIED,
                            longitude,
                            latitude,
                            images);

                    docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful() && task !=null) {
                                //task succesfull
                                Toast.makeText(MapsInformerActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                                finish();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsInformerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });

        binding.btSkipDecsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnLocationSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.LocationReport.setVisibility(View.GONE);
                binding.OtherReports.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getFromBundle(Bundle bundle) {

        try {
            bundle = getIntent().getExtras();
            wanted_person = bundle.getString("fullName");
            acts = bundle.getString("acts");
            address = bundle.getString("address");
            age = bundle.getInt("age");
            eyeColor = bundle.getString("eyeColor");
            hairColor = bundle.getString("hairColor");
            height = bundle.getInt("height");
            phy_appearance = bundle.getString("phy_appearance");
            status = bundle.getString("status");
            urlOfProfile = bundle.getString("urlOfProfile");
            weight = bundle.getInt("weight");

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void getInformerFullName(String uID) {
        firebaseFirestore.collection("users").document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null && documentSnapshot.exists()) {
                            informer_person = documentSnapshot.getString("fullName");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsInformerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUIDorPHONE() {
        if(firebaseAuth != null){
            if(firebaseAuth.getCurrentUser().getPhoneNumber() !=null && !TextUtils.isEmpty(firebaseAuth.getCurrentUser().getPhoneNumber())){
                informer_person = firebaseAuth.getCurrentUser().getPhoneNumber();
                SharedPreferences settings = getSharedPreferences(PROFILE_INFORMER_PREFS, 0);
                informer_person = settings.getString("phone", null);
            }else if(firebaseAuth.getCurrentUser().isAnonymous()){
                    informer_person = "ANONYMOUS";
            }
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

    private void getDeviceLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(locationPermissionGranted){
                Task location = mfusedLocationProviderClient.getLastLocation();
                if(location != null) {
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Location currentLocation = (Location) task.getResult();
                                //bohen set lokacionet per future
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                //move camera te lokacioni jon
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                                markerOptionsDefault = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                        .title(wanted_person +" "+ R.string.position).icon(BitmapDescriptorFactory.defaultMarker());
                                mMap.addMarker(markerOptionsDefault);

                            }else {
                                Toast.makeText(MapsInformerActivity.this, "No location found, tap on map for manual location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(this, R.string.please_turn_on_location_on_your_phone, Toast.LENGTH_SHORT).show();
                }
            }
        }catch (SecurityException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsInformerActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        locationPermissionGranted = false;
                        return;
                    }
                }
                locationPermissionGranted = true;
                initMap();
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        binding.tvLongTap.setVisibility(View.GONE);
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        mMap.clear();
        markerOptionsDefault = new MarkerOptions().position(latLng)
                .title(wanted_person +" "+ R.string.position).icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptionsDefault);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_CODE && resultCode == RESULT_OK) {

            if(data.getClipData() != null) {

                int totalitem = data.getClipData().getItemCount();
                for(int i=0; i<totalitem;i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    uploadImageToFirebase(imageUri, totalitem);
                    binding.tvReportImage.setText(totalitem +" "+getText(R.string.image_uploaded_v2));
                }
            }else {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri, int totalitem) {
        //upload image to storage in firebase

        dateNtime = getTimeDate();
        for(int i = 0; i < totalitem; i++) {
            StorageReference fileRef = storageReference.child("wanted_persons/locations_reports/"+dateNtime+"/Image"+i+".jpg");
            int finalI = i;
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.put("image"+ finalI, uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapsInformerActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setLastSeenLocation(Double latitude, Double longitude, String wanted_person) {
        DocumentReference locationReports = firebaseFirestore.collection("wanted_persons").document(wanted_person);

        locationReports.update("latitude", latitude).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MapsInformerActivity.this, R.string.latitude_updated_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsInformerActivity.this, R.string.latitude_failed_to_update, Toast.LENGTH_SHORT).show();
            }
        });

        locationReports.update("longitude", longitude).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MapsInformerActivity.this, R.string.longitude_updated_successfully, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsInformerActivity.this, R.string.longitude_failed_to_update, Toast.LENGTH_SHORT).show();
            }
        });

    }

}