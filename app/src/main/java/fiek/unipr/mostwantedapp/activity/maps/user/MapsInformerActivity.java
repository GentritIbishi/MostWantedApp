package fiek.unipr.mostwantedapp.activity.maps.user;

import static fiek.unipr.mostwantedapp.utils.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.LATITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE_BACKGROUND;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE_COURSE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE_FINE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.LONGITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.PHONE_USER;
import static fiek.unipr.mostwantedapp.utils.Constants.PICK_IMAGE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;
import static fiek.unipr.mostwantedapp.utils.StringHelper.empty;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsInformerBinding;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;
import fiek.unipr.mostwantedapp.utils.DateHelper;

public class MapsInformerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private ActivityMapsInformerBinding binding;
    private int upload_count = 0;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DocumentReference documentReference;

    private String personId, wanted_person, acts, address, eyeColor, hairColor, phy_appearance, status, prize, urlOfProfile, informer_person_urlOfProfile;
    private Integer age, height, weight;
    private Double latitude, longitude;
    private String description = "No description!";
    private String informer_person = "ANONYMOUS";
    private MarkerOptions markerOptionsDefault;
    private Bundle mapsInformerBundle;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Map<String, Object> imageListMap = new HashMap<>();
    private Uri ImageUri;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsInformerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getLocationPermission();

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

        initMap();

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
                save(informer_person, personId, wanted_person, longitude, latitude, DateHelper.getDateTime());
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

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null)
        {
            if(firebaseAuth.getCurrentUser().isAnonymous())
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                                }
                                mMap.setMyLocationEnabled(true);
                            } else
                            {
                                Toast.makeText(MapsInformerActivity.this, getApplicationContext().getText(R.string.no_location_found_tap_on_map_for_manual_location), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else
                {
                    Toast.makeText(this, R.string.please_turn_on_location_on_your_phone, Toast.LENGTH_LONG).show();
                    setMarkerLocation(LATITUDE_DEFAULT, LONGITUDE_DEFAULT);
                }
            } else {
                setMarkerLocation(LATITUDE_DEFAULT, LONGITUDE_DEFAULT);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "MAPS INFORMER EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
                //nese nuk eshte FINE
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }else
            {
                //nese o fine
                locationPermissionGranted = true;
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    //nese nuk o course
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
                else
                {
                    //nese o course
                    locationPermissionGranted = true;
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        //nese nuk o background
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }else {
                        //nese o background
                        locationPermissionGranted = true;
                    }
                }
            }
            } catch (Exception e){
                e.printStackTrace();
            }
    }

    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsInformerActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        locationPermissionGranted = false;
                        return;
                    }

                } else {
                    locationPermissionGranted = true;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void goToApplicationDetailsSettings() {
        Toast.makeText(this, getResources().getText(R.string.you_need_permissions), Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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

    private void setLastSeenLocation(Double latitude, Double longitude, String personId) {
        DocumentReference locationReports = firebaseFirestore.collection(WANTED_PERSONS).document(personId);

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
                        Toast.makeText(MapsInformerActivity.this, "ERROR UPDATING Images!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.setMessage(getApplicationContext().getString(R.string.thank_you_for_collaboration));
    }

    private void save(String informer_person, String personId, String wanted_person, Double longitude, Double latitude, String dateNtime) {
        CollectionReference collRef = firebaseFirestore.collection(LOCATION_REPORTS);
        String docId = collRef.document().getId();
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
                    ANONYMOUS,
                    prize,
                    ReportStatus.UNVERIFIED,
                    longitude,
                    latitude,
                    null);

            docRef.set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful() && task !=null) {
                        // Toast.makeText(MapsInformerActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                        setLastSeenLocation(latitude, longitude, personId);
                        if(ImageList.size() != 0){
                            for(upload_count = 0; upload_count < ImageList.size(); upload_count++){
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
                    .collection(LOCATION_REPORTS)
                    .document(docId);
            Report report = new Report(docId, binding.etReportTitle.getText().toString(), binding.etDescription.getText().toString(),
                    dateNtime,
                    firebaseAuth.getCurrentUser().getUid(),
                    personId,
                    informer_person,
                    wanted_person,
                    PHONE_USER,
                    prize,
                    ReportStatus.UNVERIFIED,
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
                    finish();
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
                                        informer_person_urlOfProfile,
                                        prize,
                                        ReportStatus.UNVERIFIED,
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