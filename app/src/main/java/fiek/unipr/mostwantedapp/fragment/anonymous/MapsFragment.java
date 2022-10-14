package fiek.unipr.mostwantedapp.fragment.anonymous;

import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.LATITUDE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static fiek.unipr.mostwantedapp.utils.Constants.LONGITUDE_DEFAULT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.GpsTracker;

public class MapsFragment extends Fragment {

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
    private String description = "No description!";
    private String informer_person = "ANONYMOUS";
    private MarkerOptions markerOptionsDefault;
    private Bundle mapsInformerBundle;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Map<String, Object> imageListMap = new HashMap<>();
    private Uri ImageUri;
    private boolean locationPermissionGranted = false;

    private TextView tvLongTapAnonymous;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    tvLongTapAnonymous.setVisibility(View.GONE);
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    mMap.clear();
                    markerOptionsDefault = new MarkerOptions().position(latLng)
                            .title(wanted_person +" "+ getContext().getText(R.string.position)).icon(BitmapDescriptorFactory.defaultMarker());
                    mMap.addMarker(markerOptionsDefault);
                }
            });
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
                                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "MAPS INFORMER EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.anonymousMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        tvLongTapAnonymous = view.findViewById(R.id.tvLongTapAnonymous);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
    }

    @Override
    public void onStart() {
        getLocationPermission();
        super.onStart();
    }

    private void setMarkerLocation(Double latitude, Double longitude) {
        moveCamera(new LatLng(latitude, longitude),
                DEFAULT_ZOOM);
        markerOptionsDefault = new MarkerOptions().position(new LatLng(latitude, latitude))
                .title(wanted_person + " " + getContext().getText(R.string.position)).icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptionsDefault);
    }

    private void getLocationPermission()
    {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //if not FINE
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }else
            {
                //if fine
                locationPermissionGranted = true;
                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    //if not coarse
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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

}