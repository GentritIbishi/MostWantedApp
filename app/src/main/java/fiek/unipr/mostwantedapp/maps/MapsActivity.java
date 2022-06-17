package fiek.unipr.mostwantedapp.maps;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String newLatitude, newLongitude, fullName, status, urlOfProfile, uID;
    private double latitude, longitude;

    private static String POLICE_STATION_RR_REXHEP_LUCI_TITLE = "POLICE STATION - Rr. Rexhep Luci, Prishtine 10000";
    private static double POLICE_STATION_RR_REXHEP_LUCI_LATITUDE = 42.6626792;
    private static double POLICE_STATION_RR_REXHEP_LUCI_LONGITUDE = 21.1572832;

    private static String POLICE_STATION_NR2_TITLE = "POLICE STATION Nr.2 - Hamez Jashari, Prishtine 10000";
    private static double POLICE_STATION_NR2_LATITUDE = 42.659114;
    private static double POLICE_STATION_NR2_LONGITUDE = 21.167420;

    private static String POLICE_STATION_SHESHI_I_LIRISE_TITLE = "POLICE STATION - Sheshi i Lirise, Fushe Kosove 12000";
    private static double POLICE_STATION_SHESHI_I_LIRISE_LATITUDE = 42.6347623;
    private static double POLICE_STATION_SHESHI_I_LIRISE_LONGITUDE = 21.0848419;

    private static String POLICE_STATION_E80_TITLE = "POLICE STATION - E80, Prishtine 10000";
    private static double POLICE_STATION_E80_LATITUDE = 42.681564;
    private static double POLICE_STATION_E80_LONGITUDE = 21.159672;

    private static String POLICE_STATION_BISLIM_BAJGORA_TITLE = "POLICE STATION - Bislim Bajgora, Mitrovice 40000";
    private static double POLICE_STATION_BISLIM_BAJGORA_LATITUDE = 42.890092;
    private static double POLICE_STATION_BISLIM_BAJGORA_LONGITUDE = 20.874400;

    private static String POLICE_STATION_KRALJA_MILUTINA_TITLE = "POLICE STATION - Kralja Milutina, Graçanice 10500";
    private static double POLICE_STATION_KRALJA_MILUTINA_LATITUDE = 42.601494;
    private static double POLICE_STATION_KRALJA_MILUTINA_LONGITUDE = 21.192242;

    private static String POLICE_STATION_RR_MULLA_IDRIZI_TITLE = "POLICE STATION - Rr. Mulla Idrizi, Gjilan 60000";
    private static double POLICE_STATION_RR_MULLA_IDRIZI_LATITUDE = 42.4631162;
    private static double POLICE_STATION_RR_MULLA_IDRIZI_LONGITUDE = 21.4696013;

    private static String POLICE_STATION_RR_JONI_TITLE = "POLICE STATION - Rr. JONI, Prizren 20000";
    private static double POLICE_STATION_RR_JONI_LATITUDE = 42.214859;
    private static double POLICE_STATION_RR_JONI_LONGITUDE = 20.732425;

    private static String POLICE_STATION_RR_WILLIAM_WALKER_TITLE = "POLICE STATION ALPHA - Rr. William Walker, Prizren 20000";
    private static double POLICE_STATION_RR_WILLIAM_WALKER_LATITUDE = 42.210322;
    private static double POLICE_STATION_RR_WILLIAM_WALKER_LONGITUDE = 20.730256;

    private static String POLICE_STATION_GJAKOVE_TITLE = "POLICE STATION - Gjakove 20000";
    private static double POLICE_STATION_GJAKOVE_LATITUDE = 42.375681;
    private static double POLICE_STATION_GJAKOVE_LONGITUDE = 20.442102;

    private static String POLICE_STATION_PERLINE_TITLE = "POLICE STATION - Perline 32000";
    private static double POLICE_STATION_PERLINE_LATITUDE = 42.609493;
    private static double POLICE_STATION_PERLINE_LONGITUDE = 20.575675;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setStationAsMarker(POLICE_STATION_RR_REXHEP_LUCI_LATITUDE, POLICE_STATION_RR_REXHEP_LUCI_LONGITUDE, POLICE_STATION_RR_REXHEP_LUCI_TITLE);
        setStationAsMarker(POLICE_STATION_NR2_LATITUDE, POLICE_STATION_NR2_LONGITUDE, POLICE_STATION_NR2_TITLE);
        setStationAsMarker(POLICE_STATION_SHESHI_I_LIRISE_LATITUDE, POLICE_STATION_SHESHI_I_LIRISE_LONGITUDE, POLICE_STATION_SHESHI_I_LIRISE_TITLE);
        setStationAsMarker(POLICE_STATION_E80_LATITUDE, POLICE_STATION_E80_LONGITUDE, POLICE_STATION_E80_TITLE);
        setStationAsMarker(POLICE_STATION_BISLIM_BAJGORA_LATITUDE, POLICE_STATION_BISLIM_BAJGORA_LONGITUDE, POLICE_STATION_BISLIM_BAJGORA_TITLE);
        setStationAsMarker(POLICE_STATION_KRALJA_MILUTINA_LATITUDE, POLICE_STATION_KRALJA_MILUTINA_LONGITUDE, POLICE_STATION_KRALJA_MILUTINA_TITLE);
        setStationAsMarker(POLICE_STATION_RR_MULLA_IDRIZI_LATITUDE, POLICE_STATION_RR_MULLA_IDRIZI_LONGITUDE, POLICE_STATION_RR_MULLA_IDRIZI_TITLE);
        setStationAsMarker(POLICE_STATION_RR_WILLIAM_WALKER_LATITUDE, POLICE_STATION_RR_WILLIAM_WALKER_LONGITUDE, POLICE_STATION_RR_WILLIAM_WALKER_TITLE);
        setStationAsMarker(POLICE_STATION_GJAKOVE_LATITUDE, POLICE_STATION_GJAKOVE_LONGITUDE, POLICE_STATION_GJAKOVE_TITLE);
        setStationAsMarker(POLICE_STATION_PERLINE_LATITUDE, POLICE_STATION_PERLINE_LONGITUDE, POLICE_STATION_PERLINE_TITLE);

        setLocations(fullName, newLatitude, newLongitude);
        try {
            // Add a marker in Sydney and move the camera
            LatLng latLng = new LatLng(Double.parseDouble(newLatitude), Double.parseDouble(newLongitude));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Last Seen: "+fullName));
            mMap.setMaxZoomPreference(17);
            mMap.setMinZoomPreference(8);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }catch (Exception e) {
            Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    }

    private void setLocations(String fullName, String newLatitude, String newLongitude) {
        //select all location reports where fullname is like this
        firebaseFirestore.collection("locations_reports")
                .whereEqualTo("wanted_person", fullName)
                .whereNotEqualTo("latitude", newLatitude)
                .limit(5)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            Double latitude = doc.getDouble("latitude");
                            Double longitude = doc.getDouble("longitude");

                            try {
                                LatLng latLng = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(i)));
                            }catch (Exception e){
                                Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setStationAsMarker(Double latitude, Double longitude, String police_station_title) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(police_station_title)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_police_google)));
    }
}