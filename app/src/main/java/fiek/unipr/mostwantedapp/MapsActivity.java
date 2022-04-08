package fiek.unipr.mostwantedapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fiek.unipr.mostwantedapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String newLatitude, newLongitude, fullName, status, urlOfProfile;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            Intent in = getIntent();
            if(in != null)
            {
                newLatitude = in.getStringExtra("latitude");
                newLongitude = in.getStringExtra("longitude");
                fullName = in.getStringExtra("fullName");
                status = in.getStringExtra("status");
                urlOfProfile = in.getStringExtra("urlOfProfile");

            }else {
                newLongitude = newLongitude;
                newLatitude = newLatitude;
            }
        }catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Add a marker in Sydney and move the camera
            LatLng latLng = new LatLng(Double.parseDouble(newLatitude), Double.parseDouble(newLongitude));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position of "+fullName));
            mMap.setMaxZoomPreference(17);
            mMap.setMinZoomPreference(15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }catch (Exception e) {
            Log.e("Error:", e.getMessage().toString());
        }
    }
}