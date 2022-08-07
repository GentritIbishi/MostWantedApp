package fiek.unipr.mostwantedapp.fragment.user;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.PersonListAdapter;
import fiek.unipr.mostwantedapp.maps.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.NotificationAdmin;
import fiek.unipr.mostwantedapp.models.NotificationAdminState;
import fiek.unipr.mostwantedapp.models.NotificationReportUser;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.Report;

public class HomeFragment extends Fragment {

    private View home_fragment_view;
    private ListView lvPersons;
    private PersonListAdapter personListAdapter;
    private ArrayList<Person> personArrayList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String fullName;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private boolean locationPermissionGranted = false;
    private Double latitude, longitude;

    public HomeFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocationPermission();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        realTimeCheckReportStatus(firebaseAuth.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        home_fragment_view = inflater.inflate(R.layout.fragment_home_user, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout pullToRefreshInHome = home_fragment_view.findViewById(R.id.pullToRefreshInHome);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                personArrayList.clear();
                loadDatainListview();
                pullToRefreshInHome.setRefreshing(false);
            }
        });
        return home_fragment_view;
    }

    private void InitializeFields() {
        lvPersons = home_fragment_view.findViewById(R.id.lvPersons);
        personArrayList = new ArrayList<>();
        personListAdapter = new PersonListAdapter(getActivity().getApplicationContext(), personArrayList);
        lvPersons.setAdapter(personListAdapter);
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Person person = d.toObject(Person.class);

                                fullName = person.getFullName();

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }

                            personListAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity().getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity().getApplicationContext(), "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void realTimeCheckReportStatus(String uID) {
        firebaseFirestore.collection("locations_reports")
                .whereEqualTo("uID", uID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            String notificationReportDateTime = dc.getDocument().getString("date_time");
                            String notificationReportBody = dc.getDocument().getString("description");
                            String notificationReportTitle = dc.getDocument().getString("title");
                            String notificationReportStatusChangedTo = dc.getDocument().getString("status");

                            switch (dc.getType()) {
                                case MODIFIED:
                                    saveNotificationInFirestoreModified(uID, notificationReportDateTime, notificationReportBody, notificationReportTitle
                                    , String.valueOf(NotificationAdminState.MODIFIED), notificationReportStatusChangedTo, getDateTime());
                                    break;
                            }
                        }
                    }
                });

    }

    private void saveNotificationInFirestoreModified(String notificationReportUID, String notificationReportDateTime, String notificationReportBody, String notificationReportTitle, String notificationReportType,
                                                     String notificationReportStatusChangedTo, String notificationDateTimeChanged) {
        NotificationReportUser objNotificationReportUser = new NotificationReportUser(notificationReportDateTime, notificationReportBody, notificationReportTitle, notificationReportType, notificationReportStatusChangedTo, notificationReportUID, notificationDateTimeChanged);
        firebaseFirestore.collection("notifications_user")
                .whereEqualTo("notificationReportTitle", notificationReportTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() == 0){
                            firebaseFirestore.collection("notifications_user").document(notificationDateTimeChanged).set(objNotificationReportUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //not exist make notification and save for next time
                                    Uri modified_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), notificationReportTitle);
                                    notificationBuilder.setContentTitle(notificationReportTitle);
                                    notificationBuilder.setContentText(notificationReportBody);
                                    notificationBuilder.setSmallIcon(R.drawable.ic_app);
                                    notificationBuilder.setSound(modified_defaultSoundUri);
                                    notificationBuilder.setAutoCancel(true);

                                    SharedPreferences prefs = getActivity().getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
                                    int notificationReportStatusModified = prefs.getInt("notificationReportStatusModified", 1);

                                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notificationReportStatusModified, notificationBuilder.build());

                                    SharedPreferences.Editor editor = prefs.edit();
                                    notificationReportStatusModified++;
                                    editor.putInt("notificationReportStatusModified", notificationReportStatusModified);
                                    editor.commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted = true;
            }else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
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
                    getDeviceLocation();
                }
            }
        }
    }

    private void getDeviceLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if(locationPermissionGranted){
                Task location = mfusedLocationProviderClient.getLastLocation();
                if(location != null) {
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Location currentLocation = (Location) task.getResult();
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();

                            }else {
                                Toast.makeText(getContext(), "No location device found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getContext(), R.string.please_turn_on_location_on_your_phone, Toast.LENGTH_SHORT).show();
                }
            }
        }catch (SecurityException e) {
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getDateTime() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}