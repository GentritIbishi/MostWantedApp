package fiek.unipr.mostwantedapp.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsInformerBinding;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.models.Image;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;

public class MapsInformerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private ActivityMapsInformerBinding binding;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String PHONE_USER = "PHONE_USER";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PICK_IMAGE = 15;
    private int upload_count = 0;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private GoogleMap mMap;
    private boolean locationPermissionGranted = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DocumentReference documentReference;

    private String wanted_person, acts, address, eyeColor, hairColor, phy_appearance, status, urlOfProfile, informer_person_urlOfProfile;
    private String dateNtime;
    private Integer age, height, weight;
    private Double latitude, longitude;
    private String description = "No description!";
    private String informer_person = "ANONYMOUS";
    private MarkerOptions markerOptionsDefault;
    private Marker marker;
    private Bundle mapsInformerBundle;
    private ProgressDialog progressDialog;
    private Double default_latitude=42.667542, default_longitude=21.166191;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    Map<String, Object> imageListMap = new HashMap<>();
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsInformerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getText(R.string.report_is_saving) + "");

        binding.LocationReport.setVisibility(View.VISIBLE);
        binding.OtherReports.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mapsInformerBundle = new Bundle();
        getFromBundle(mapsInformerBundle);

        getLocationPermission();

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lat long i din ni check if not null eshte mire
                if (latitude != null && longitude != null) {
                    binding.LocationReport.setVisibility(View.GONE);
                    binding.OtherReports.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MapsInformerActivity.this, R.string.location_not_set_please_set_new_location, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        binding.btReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btReport.setEnabled(false);
                binding.reportProgressBar.setVisibility(View.VISIBLE);

                progressDialog.show();
                binding.alert.setText(getApplicationContext().getText(R.string.if_loading_takes_too_long_please_press_the_button_again));
                save(informer_person, wanted_person, longitude, latitude, getTimeDate());
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

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth != null) {

            if (firebaseAuth.getCurrentUser().isAnonymous()) {
                loadInfoAnonymousFirebase();
            } else if (!empty(firebaseAuth.getCurrentUser().getPhoneNumber())) {
                loadInfoPhoneFirebase();
            } else {
                loadInfoFromFirebase(firebaseAuth);
            }

        }
    }

    public static String getTimeDate() { // without parameter argument
        try {
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted) {
                Task location = mfusedLocationProviderClient.getLastLocation();
                if (location != null) {
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location currentLocation = (Location) task.getResult();
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                setMarkerLocation(latitude, longitude);
                                mMap.setMyLocationEnabled(true);
                            } else {
                                Toast.makeText(MapsInformerActivity.this, getApplicationContext().getText(R.string.no_location_found_tap_on_map_for_manual_location), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, R.string.please_turn_on_location_on_your_phone, Toast.LENGTH_SHORT).show();
                    setMarkerLocation(default_latitude, default_longitude);
                }
            } else {
                setMarkerLocation(default_latitude, default_longitude);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setMarkerLocation(Double latitude, Double longitude) {
        moveCamera(new LatLng(default_latitude, default_longitude),
                DEFAULT_ZOOM);
        markerOptionsDefault = new MarkerOptions().position(new LatLng(default_latitude, default_longitude))
                .title(wanted_person + " " + getApplicationContext().getText(R.string.position)).icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptionsDefault);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                initMap();
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            initMap();
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
                }else {
                    locationPermissionGranted = true;
                    initMap();
                }
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
                .title(wanted_person +" "+ getApplicationContext().getText(R.string.position)).icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptionsDefault);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if(data.getClipData() != null) {
                int totalitem = data.getClipData().getItemCount();
                for(int i=0; i<totalitem;i++){
                    ImageUri = data.getClipData().getItemAt(i).getUri();
                    ImageList.add(ImageUri);
                    binding.alert.setText(this.getText(R.string.you_have_selected)+" "+ImageList.size()+" "+this.getText(R.string.images));
                }
            }else if(data.getData() != null) {
                ImageUri = data.getData();
                ImageList.add(ImageUri);
                binding.alert.setText(this.getText(R.string.you_have_selected)+" "+ ImageList.size() +" "+this.getText(R.string.images));
            }
        }
    }

    private void setLastSeenLocation(Double latitude, Double longitude, String wanted_person) {
        DocumentReference locationReports = firebaseFirestore.collection("wanted_persons").document(wanted_person);

        locationReports.update("latitude", latitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsInformerActivity.this, R.string.latitude_failed_to_update, Toast.LENGTH_SHORT).show();
            }
        });

        locationReports.update("longitude", longitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsInformerActivity.this, R.string.longitude_failed_to_update, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            //logged in with phone
            informer_person = phone;
        }
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            informer_person = ANONYMOUS;
        }
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(this)){
            return false;
        }else {
            return true;
        }
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        informer_person = task.getResult().getString("fullName");
                    }
                }
            });
        }
    }

    private void storeLink (String dateNtime, Uri uri, Integer count) {
        imageListMap.put("image"+ count, uri.toString());
        firebaseFirestore.collection("locations_reports")
                .document(dateNtime)
                        .update("images", imageListMap).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsInformerActivity.this, "ERROR UPDATING Images!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.setMessage(getApplicationContext().getString(R.string.thank_you_for_collaboration));
    }

    private void save(String informer_person, String wanted_person, Double longitude, Double latitude, String dateNtime) {
            if(informer_person.equals(ANONYMOUS))
            {
                DocumentReference docRef = firebaseFirestore
                        .collection("locations_reports")
                        .document(dateNtime);
                Report report = new Report(binding.etReportTitle.getText().toString(), binding.etDescription.getText().toString(),
                        dateNtime,
                        firebaseAuth.getCurrentUser().getUid(),
                        informer_person,
                        wanted_person,
                        ANONYMOUS,
                        ReportStatus.UNVERIFIED,
                        longitude,
                        latitude,
                        null);

                docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful() && task !=null) {
                            // Toast.makeText(MapsInformerActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                            setLastSeenLocation(latitude, longitude, wanted_person);
                            if(ImageList.size() != 0){
                                for(upload_count = 0; upload_count < ImageList.size(); upload_count++){
                                    Uri IndividualImage = ImageList.get(upload_count);
                                    StorageReference fileRef = storageReference.child("wanted_persons/locations_reports/"+dateNtime+"/Image"+upload_count+".jpg");
                                    int finalI = upload_count;
                                    fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    storeLink(dateNtime, uri, finalI);
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
                                progressDialog.dismiss();
                                finish();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }else {
                                progressDialog.setMessage(getApplicationContext().getString(R.string.thank_you_for_collaboration)+"");
                                progressDialog.dismiss();
                                finish();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsInformerActivity.this, getApplicationContext().getText(R.string.failed_to_save_report)+" "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else if(informer_person.startsWith("+"))
            {
                DocumentReference docRef = firebaseFirestore
                        .collection("locations_reports")
                        .document(dateNtime);
                Report report = new Report(binding.etReportTitle.getText().toString(), binding.etDescription.getText().toString(),
                        dateNtime,
                        firebaseAuth.getCurrentUser().getUid(),
                        informer_person,
                        wanted_person,
                        PHONE_USER,
                        ReportStatus.UNVERIFIED,
                        longitude,
                        latitude,
                        null);

                docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful() && task !=null) {
                            setLastSeenLocation(latitude, longitude, wanted_person);
                            if(ImageList.size() != 0){
                                for(int upload_count = 0; upload_count < ImageList.size(); upload_count++){
                                    Uri IndividualImage = ImageList.get(upload_count);
                                    StorageReference fileRef = storageReference.child("wanted_persons/locations_reports/"+dateNtime+"/Image"+upload_count+".jpg");
                                    int finalI = upload_count;
                                    fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    storeLink(dateNtime, uri, finalI);
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MapsInformerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                progressDialog.dismiss();
                                finish();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }else {
                                progressDialog.setMessage(getApplicationContext().getString(R.string.thank_you_for_collaboration)+"");
                                progressDialog.dismiss();
                                finish();
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsInformerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error krejt"+e.getMessage());
                        finish();
                    }
                });
            }else if(!informer_person.equals(ANONYMOUS) && !informer_person.startsWith("+"))
            {
                firebaseFirestore.collection("users")
                        .whereEqualTo("fullName", informer_person)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && task.getResult().size() != 0)
                                {
                                    informer_person_urlOfProfile = task.getResult().getDocuments().get(0).getString("urlOfProfile");

                                    DocumentReference docRef = firebaseFirestore
                                            .collection("locations_reports")
                                            .document(dateNtime);
                                    Report report = new Report(binding.etReportTitle.getText().toString(), binding.etDescription.getText().toString(),
                                            dateNtime,
                                            firebaseAuth.getCurrentUser().getUid(),
                                            informer_person,
                                            wanted_person,
                                            informer_person_urlOfProfile,
                                            ReportStatus.UNVERIFIED,
                                            longitude,
                                            latitude,
                                            null);

                                    docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful() && task !=null) {
                                                setLastSeenLocation(latitude, longitude, wanted_person);
                                                if(ImageList.size() != 0){
                                                    for(int upload_count = 0; upload_count < ImageList.size(); upload_count++){
                                                        Uri IndividualImage = ImageList.get(upload_count);
                                                        StorageReference fileRef = storageReference.child("wanted_persons/locations_reports/"+dateNtime+"/Image"+upload_count+".jpg");
                                                        int finalI = upload_count;
                                                        fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        storeLink(dateNtime, uri, finalI);
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
                                                    ImageList.clear();
                                                    imageListMap.clear();
                                                    progressDialog.dismiss();
                                                    finish();
                                                    binding.reportProgressBar.setVisibility(View.INVISIBLE);
                                                }else {
                                                    progressDialog.setMessage(getApplicationContext().getString(R.string.thank_you_for_collaboration)+"");
                                                    progressDialog.dismiss();
                                                    finish();
                                                    binding.reportProgressBar.setVisibility(View.INVISIBLE);
                                                }
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
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }

}