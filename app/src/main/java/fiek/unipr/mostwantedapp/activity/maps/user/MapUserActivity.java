package fiek.unipr.mostwantedapp.activity.maps.user;

import static fiek.unipr.mostwantedapp.utils.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.utils.Constants.APPEARANCE_MODE_PREFERENCE;
import static fiek.unipr.mostwantedapp.utils.Constants.DARK_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.LATITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.LONGITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.PHONE_USER;
import static fiek.unipr.mostwantedapp.utils.Constants.PICK_IMAGE;
import static fiek.unipr.mostwantedapp.utils.Constants.SYSTEM_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;
import static fiek.unipr.mostwantedapp.utils.StringHelper.empty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsInformerBinding;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.GpsTracker;

public class MapUserActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Context mContext;
    private ActivityMapsInformerBinding binding;
    private int upload_count = 0;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DocumentReference documentReference;

    private String personId, wanted_person, acts, address, eyeColor, hairColor, phy_appearance, status, prize, urlOfProfile, informer_person_urlOfProfile;
    private Integer age, height, weight;
    private Double latitude, longitude;
    private String informer_person = "ANONYMOUS";
    private MarkerOptions markerOptionsDefault;
    private Bundle mapsInformerBundle;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Map<String, Object> imageListMap = new HashMap<>();
    private Uri ImageUri;
    private boolean locationPermissionGranted = false;
    private int progress;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkAndSetModeMap(mContext);
        mMap.setOnMapClickListener(this);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted)
            {
                Task location = mfusedLocationProviderClient.getLastLocation();
                if (location != null)
                {
                    location.addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Location currentLocation = (Location) task.getResult();
                                if(currentLocation != null)
                                {
                                    latitude = currentLocation.getLatitude();
                                    longitude = currentLocation.getLongitude();
                                    setMarkerLocation(latitude, longitude);
                                }else
                                {
                                    setMarkerLocation(LATITUDE_DEFAULT, LONGITUDE_DEFAULT);
                                    gpsTracker.showSettingsAlert();
                                }
                                mMap.setMyLocationEnabled(true);
                            } else
                            {
                                Toast.makeText(MapUserActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else
                {
                    gpsTracker.showSettingsAlert();
                    setMarkerLocation(LATITUDE_DEFAULT, LONGITUDE_DEFAULT);
                }
            } else {
                setMarkerLocation(LATITUDE_DEFAULT, LONGITUDE_DEFAULT);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "MAPS INFORMER EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void checkAndSetModeMap(Context mContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String mode = sharedPreferences.getString(APPEARANCE_MODE_PREFERENCE, SYSTEM_MODE);
        if (mode.equals(DARK_MODE)) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_in_night));
            binding.constraintOtherReports.setBackgroundResource(R.drawable.bg_report_persistent_dark);
            binding.etReportTitleLayout.setBoxBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.etReportTitleLayout.setBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.etReportTitle.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.etReportTitle.setHintTextColor(mContext.getResources().getColor(R.color.white));
            binding.etDescriptionLayout.setBoxBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.etDescriptionLayout.setBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.etDescription.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.etDescription.setHintTextColor(mContext.getResources().getColor(R.color.white));
            binding.etReportTitleLayout.setHintTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
            binding.etDescriptionLayout.setHintTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
        }
    }

    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapUser);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapUserActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsInformerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.LocationReport.setVisibility(View.VISIBLE);
        binding.constraintOtherReports.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mContext = getApplicationContext();

        gpsTracker = new GpsTracker(MapUserActivity.this);

        mapsInformerBundle = new Bundle();
        getFromBundle(mapsInformerBundle);

        initMap();
        updateProgressBar();

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lat long i din ni check if not null eshte mire
                if (latitude != null && longitude != null) {
                    binding.btnContinue.setVisibility(View.GONE);
                    binding.constraintOtherReports.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MapUserActivity.this, R.string.location_not_set_please_set_new_location, Toast.LENGTH_SHORT).show();
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
                progress += 5;
                updateProgressBar();
                binding.btReport.setEnabled(false);
                binding.constraintOtherReports.setVisibility(View.GONE);
                binding.reportProgressBar.setVisibility(View.VISIBLE);
                binding.constrainProgress.setVisibility(View.VISIBLE);
                save(informer_person, personId, wanted_person, longitude, latitude, DateHelper.getDateTime());
            }
        });

        binding.btSkipDecsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getFromBundle(Bundle bundle) {

        try {
            bundle = getIntent().getExtras();
            personId = bundle.getString("personId");
            wanted_person = bundle.getString("fullName");
            acts = bundle.getString("acts");
            address = bundle.getString("address");
            age = bundle.getInt("age");
            eyeColor = bundle.getString("eyeColor");
            hairColor = bundle.getString("hairColor");
            height = bundle.getInt("height");
            phy_appearance = bundle.getString("phy_appearance");
            status = bundle.getString("status");
            prize = bundle.getString("prize");
            urlOfProfile = bundle.getString("urlOfProfile");
            weight = bundle.getInt("weight");

        } catch (Exception e) {
            e.getMessage();
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateProgressBar() {
        binding.progressBarReport.setProgress(progress);
        binding.tvProgressBarReport.setText(this.progress+"%");
    }


    @Override
    public void onStart() {
        getLocationPermission();
        if(firebaseAuth != null)
        {
            if(firebaseUser.isAnonymous())
            {
                loadInfoAnonymousFirebase();
            }else if(!empty(firebaseAuth.getCurrentUser().getPhoneNumber()))
            {
                loadInfoPhoneFirebase();
            }else
            {
                loadInfoFromFirebase(firebaseAuth);
            }

        }
        super.onStart();
    }

    private void setMarkerLocation(Double latitude, Double longitude) {
        moveCamera(new LatLng(latitude, longitude),
                DEFAULT_ZOOM);
        markerOptionsDefault = new MarkerOptions().position(new LatLng(latitude, latitude))
                .title(wanted_person + " " + getApplicationContext().getText(R.string.position)).icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptionsDefault);
    }

    private void getLocationPermission()
    {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //if not FINE
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }else
            {
                //if fine
                locationPermissionGranted = true;
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    //if not coarse
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
                else
                {
                    //if coarse
                    locationPermissionGranted = true;
                    initMap();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i=0; i < grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    initMap();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @SuppressLint("SetTextI18n")
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
                binding.alert.setText(this.getText(R.string.you_have_selected)+" "+ ImageList.size() +" "+this.getText(R.string.image));
            }
        }
    }

    private void setLastSeenLocation(Double latitude, Double longitude, String personId) {
        DocumentReference locationReports = firebaseFirestore.collection(WANTED_PERSONS).document(personId);

        locationReports.update("latitude", latitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapUserActivity.this, R.string.latitude_failed_to_update, Toast.LENGTH_SHORT).show();
            }
        });

        locationReports.update("longitude", longitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapUserActivity.this, R.string.longitude_failed_to_update, Toast.LENGTH_SHORT).show();
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

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(getApplicationContext())){
            documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
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

    private void storeLink (String docId, Uri uri, Integer count) {
        imageListMap.put("image"+ count, uri.toString());
        firebaseFirestore.collection(LOCATION_REPORTS)
                .document(docId)
                .update("images", imageListMap).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapUserActivity.this, "ERROR UPDATING Images!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void save(String informer_person, String personId, String wanted_person, Double longitude, Double latitude, String dateNtime) {
        CollectionReference collRef = firebaseFirestore.collection(LOCATION_REPORTS);
        String docId = collRef.document().getId();
        progress += 10;
        updateProgressBar();
        if(informer_person.equals(ANONYMOUS))
        {
            DocumentReference docRef = firebaseFirestore
                    .collection(LOCATION_REPORTS)
                    .document(docId);
            Report report = new Report(docId,
                    binding.etReportTitle.getText().toString(),
                    binding.etDescription.getText().toString(),
                    dateNtime,
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(),
                    personId,
                    informer_person,
                    wanted_person,
                    getAddress(getApplicationContext(), latitude, longitude),
                    ANONYMOUS,
                    prize,
                    ReportStatus.UNVERIFIED.toString(),
                    longitude,
                    latitude,
                    null);
            docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        setLastSeenLocation(latitude, longitude, personId);
                        if(ImageList.size() != 0)
                        {
                            for(upload_count = 0; upload_count < ImageList.size(); upload_count++)
                            {
                                Uri IndividualImage = ImageList.get(upload_count);
                                StorageReference fileRef = storageReference.child(WANTED_PERSONS+"/"+LOCATION_REPORTS+"/"+dateNtime+"/Image"+upload_count+".jpg");
                                int finalI = upload_count;
                                fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                storeLink(docId, uri, finalI);
                                                if(progress<90)
                                                {
                                                    progress+=10;
                                                    updateProgressBar();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MapUserActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        progress = 100;
                        updateProgressBar();
                        dismissProgressBar();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapUserActivity.this, getApplicationContext().getText(R.string.failed_to_save_report)+" "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(informer_person.startsWith("+"))
        {
            DocumentReference docRef = firebaseFirestore
                    .collection(LOCATION_REPORTS)
                    .document(docId);
            Report report = new Report(docId, binding.etReportTitle.getText().toString(), binding.etDescription.getText().toString(),
                    dateNtime,
                    firebaseAuth.getCurrentUser().getUid(),
                    personId,
                    informer_person,
                    wanted_person,
                    getAddress(getApplicationContext(), latitude, longitude),
                    PHONE_USER,
                    prize,
                    ReportStatus.UNVERIFIED.toString(),
                    longitude,
                    latitude,
                    null);

            docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful() && task !=null) {
                        setLastSeenLocation(latitude, longitude, personId);
                        if(ImageList.size() != 0){
                            for(int upload_count = 0; upload_count < ImageList.size(); upload_count++){
                                Uri IndividualImage = ImageList.get(upload_count);
                                StorageReference fileRef = storageReference.child(WANTED_PERSONS+"/"+LOCATION_REPORTS+"/"+dateNtime+"/Image"+upload_count+".jpg");
                                int finalI = upload_count;
                                fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                storeLink(docId, uri, finalI);
                                                if(progress<90)
                                                {
                                                    progress+=10;
                                                    updateProgressBar();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MapUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        progress = 100;
                        updateProgressBar();
                        dismissProgressBar();
                        binding.reportProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(!informer_person.equals(ANONYMOUS) && !informer_person.startsWith("+"))
        {
            firebaseFirestore.collection(USERS)
                    .whereEqualTo("fullName", informer_person)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult().size() != 0)
                            {
                                informer_person_urlOfProfile = task.getResult().getDocuments().get(0).getString("urlOfProfile");
                                DocumentReference docRef = firebaseFirestore
                                        .collection(LOCATION_REPORTS)
                                        .document(docId);
                                Report report = new Report(
                                        docId,
                                        binding.etReportTitle.getText().toString(),
                                        binding.etDescription.getText().toString(),
                                        dateNtime,
                                        firebaseAuth.getCurrentUser().getUid(),
                                        personId,
                                        informer_person,
                                        wanted_person,
                                        getAddress(getApplicationContext(), latitude, longitude),
                                        informer_person_urlOfProfile,
                                        prize,
                                        ReportStatus.UNVERIFIED.toString(),
                                        longitude,
                                        latitude,
                                        null);

                                docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            setLastSeenLocation(latitude, longitude, personId);
                                            if(ImageList.size() != 0){
                                                for(int upload_count = 0; upload_count < ImageList.size(); upload_count++){
                                                    Uri IndividualImage = ImageList.get(upload_count);
                                                    StorageReference fileRef = storageReference.child(WANTED_PERSONS+"/"+LOCATION_REPORTS+"/"+dateNtime+"/Image"+upload_count+".jpg");
                                                    int finalI = upload_count;
                                                    fileRef.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    storeLink(docId, uri, finalI);
                                                                    if(progress<90)
                                                                    {
                                                                        progress+=10;
                                                                        updateProgressBar();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(MapUserActivity.this, R.string.image_failed_to_uplaod, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                ImageList.clear();
                                                imageListMap.clear();
                                            }
                                            progress = 100;
                                            updateProgressBar();
                                            dismissProgressBar();
                                            binding.reportProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MapUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void dismissProgressBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.constrainProgress.setVisibility(View.GONE);
                clean();
                binding.reportProgressBar.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void clean() {
        binding.btnContinue.setVisibility(View.VISIBLE);
        binding.etReportTitle.setText("");
        binding.etDescription.setText("");
        binding.btReport.setEnabled(true);
    }

    public String getAddress(Context ctx, double latitude, double longitude) {
        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);
            }
        }catch (IOException e) {
            Toast.makeText(ctx, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return fullAdd;
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