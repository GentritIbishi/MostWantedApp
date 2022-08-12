package fiek.unipr.mostwantedapp.fragment.user;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.PersonListAdapter;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.models.NotificationAdminState;
import fiek.unipr.mostwantedapp.models.NotificationReportUser;
import fiek.unipr.mostwantedapp.models.Person;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String HOME_USER_PREF = "HOME_USER_PREF";
    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";
    public static final String ANONYMOUS = "ANONYMOUS";
    public String YOUR_REPORT_IN_DATETIME_TRANSLATEABLE;
    public String HAS_NEW_STATUS_RIGHT_NOW;
    public String STATUS_OF_REPORT_HAS_CHANGED_TO;
    public static final String FAKE = "FAKE";
    private View home_fragment_view;
    private ListView lvPersons;
    private PersonListAdapter personListAdapter;
    private ArrayList<Person> personArrayList;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;
    private Double balance;

    private TextView user_home_tv_num_report_verified, user_home_tv_num_report_unverified, user_home_tv_num_report_fake, user_home_tv_gradeOfUser;
    private TextView user_rightNowDateTime, user_hiDashboard, user_tv_balance;
    private CircleImageView user_imageOfDashboard;
    private PieChart user_home_pieChart;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private boolean locationPermissionGranted = false;
    private Double latitude, longitude;
    private String notificationTitle;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        YOUR_REPORT_IN_DATETIME_TRANSLATEABLE = String.valueOf(getContext().getText(R.string.your_report_in_datetime));
        HAS_NEW_STATUS_RIGHT_NOW = String.valueOf(getContext().getText(R.string.has_new_status_right_now));
        STATUS_OF_REPORT_HAS_CHANGED_TO = String.valueOf(getContext().getText(R.string.status_of_report_has_changed_to));
    }

    @Override
    public void onStart() {
        super.onStart();
        createNotificationChannel();
        if(firebaseAuth != null){
            //getLocationPermission();
            loadInfoFromFirebase(firebaseAuth);
            loadInfoAnonymousFirebase();
            loadInfoPhoneFirebase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        home_fragment_view = inflater.inflate(R.layout.fragment_home_user, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout user_home_pullToRefreshProfileDashboard = home_fragment_view.findViewById(R.id.user_home_pullToRefreshProfileDashboard);
        user_home_pullToRefreshProfileDashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                personArrayList.clear();
                loadDatainListview();

                loadInfoFromFirebase(firebaseAuth);
                loadInfoPhoneFirebase();
                loadInfoAnonymousFirebase();

                setupPieChart();
                setPieChart();
                checkingForNewReport(firebaseAuth.getUid());

                user_home_pullToRefreshProfileDashboard.setRefreshing(false);
            }
        });

        realTimeCheckingForNewReport();

        user_rightNowDateTime.setText(getDate());
        getGrade(firebaseAuth);
        setupPieChart();
        setPieChart();

        return home_fragment_view;
    }

    private void realTimeCheckingForNewReport() {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                checkingForNewReport(firebaseAuth.getUid());
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void InitializeFields() {
        lvPersons = home_fragment_view.findViewById(R.id.lvPersons);
        personArrayList = new ArrayList<>();
        personListAdapter = new PersonListAdapter(getActivity().getApplicationContext(), personArrayList);
        lvPersons.setAdapter(personListAdapter);
        user_rightNowDateTime = home_fragment_view.findViewById(R.id.user_rightNowDateTime);
        user_hiDashboard = home_fragment_view.findViewById(R.id.user_hiDashboard);
        user_imageOfDashboard = home_fragment_view.findViewById(R.id.user_imageOfDashboard);
        user_home_tv_num_report_verified = home_fragment_view.findViewById(R.id.user_home_tv_num_report_verified);
        user_home_tv_num_report_unverified = home_fragment_view.findViewById(R.id.user_home_tv_num_report_unverified);
        user_home_tv_num_report_fake = home_fragment_view.findViewById(R.id.user_home_tv_num_report_fake);
        user_home_tv_gradeOfUser = home_fragment_view.findViewById(R.id.user_home_tv_gradeOfUser);
        user_home_pieChart = home_fragment_view.findViewById(R.id.user_home_pieChart);
        user_tv_balance = home_fragment_view.findViewById(R.id.user_tv_balance);
    }

    private void getGrade(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            firebaseFirestore.collection("users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                grade = task.getResult().getString("grade");
                                user_home_tv_gradeOfUser.setText(grade);
                            }
                        }
                    });
        }
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons")
                .limit(5)
                .orderBy("registration_date", Query.Direction.ASCENDING)
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

    private void checkingForNewReport(String uID) {
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
                            String notificationReportId = dc.getDocument().getString("docId");
                            String notificationReportUid = dc.getDocument().getString("uID");
                            String notificationReportDateTime = dc.getDocument().getString("date_time");
                            String notificationReportTitle = dc.getDocument().getString("title");
                            String notificationReportDescription = dc.getDocument().getString("description");
                            String notificationReportInformerPerson = dc.getDocument().getString("informer_person");
                            String notificationReportWantedPerson = dc.getDocument().getString("wanted_person");
                            String notificationReportPrizeToWin = dc.getDocument().getString("prizeToWin");
                            String notificationReportNewStatus = dc.getDocument().getString("status");
                            String notificationType = String.valueOf(NotificationAdminState.MODIFIED);

                            switch (dc.getType()) {
                                case MODIFIED:
                                    saveNotificationInFirestoreModified(getDateTime(),
                                            notificationType,
                                            notificationReportId,
                                            notificationReportUid,
                                            notificationReportDateTime,
                                            notificationReportTitle,
                                            notificationReportDescription,
                                            notificationReportInformerPerson,
                                            notificationReportWantedPerson,
                                            notificationReportPrizeToWin,
                                            notificationReportNewStatus
                                            );
                                    break;
                            }
                        }
                    }
                });

    }

    private void saveNotificationInFirestoreModified
            (
            String notificationDateTime,
            String notificationType,
            String notificationReportId,
            String notificationReportUid,
            String notificationReportDateTime,
            String notificationReportTitle,
            String notificationReportDescription,
            String notificationReportInformerPerson,
            String notificationReportWantedPerson,
            String notificationReportPrizeToWin,
            String notificationReportNewStatus
            )
    {

        CollectionReference collRef = firebaseFirestore.collection("notifications_user");
        String notificationId = collRef.document().getId();
        NotificationReportUser objNotificationReportUser = new NotificationReportUser
                (
                notificationId,
                notificationDateTime,
                notificationType,
                notificationReportId,
                notificationReportUid,
                notificationReportDateTime,
                notificationReportTitle,
                notificationReportDescription,
                notificationReportInformerPerson,
                notificationReportWantedPerson,
                notificationReportPrizeToWin,
                notificationReportNewStatus
                );

        firebaseFirestore.collection("notifications_user")
                .document(notificationId)
                .set(objNotificationReportUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Uri modified_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), "ID_MODIFIED_REPORT");
                    notificationBuilder.setContentTitle(YOUR_REPORT_IN_DATETIME_TRANSLATEABLE+" "+notificationReportDateTime+" "+HAS_NEW_STATUS_RIGHT_NOW);
                    notificationBuilder.setContentText(STATUS_OF_REPORT_HAS_CHANGED_TO+" "+
                            notificationReportNewStatus);
                    notificationBuilder.setSmallIcon(R.drawable.ic_app);
                    notificationBuilder.setSound(modified_defaultSoundUri);
                    notificationBuilder.setPriority(importance);
                    notificationBuilder.setAutoCancel(true);

                    int notificationReportStatusModified = sharedPreferences.getInt("notificationReportStatusModified", 1);

                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationReportStatusModified, notificationBuilder.build());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
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

    private void test(String statusStateChange, String prizeToWin) {
        if(statusStateChange.equals(VERIFIED)) {
            if(prizeToWin != null) {
                String[] segments = prizeToWin.split(" ");
                String firstSegment = segments[0];
                Integer prize = Integer.valueOf(firstSegment.trim());
                setCoinsToUser(firebaseAuth.getUid(),prize);
            }
        }
    }

    private void setCoinsToUser(String uid, Integer balance) {
        Map<String, Object> data = new HashMap<>();
        data.put("balance", balance);
        data.put("coins", balance);
        firebaseFirestore.collection("users")
                .document(uid)
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), getContext().getText(R.string.you_have_earn)+": "+balance+" "+"coins", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        user_tv_balance.setText(balance);
                    }
                });
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
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

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if(checkConnection()) {
            firebaseFirestore.collection("locations_reports")
                    .whereEqualTo("uID", firebaseAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int newCountVERIFIED = 0;
                            int newCountUNVERIFIED = 0;
                            int newCountFAKE = 0;

                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                                String status = doc.getString("status");

                                if (status.equals(VERIFIED)) {
                                    newCountVERIFIED++;
                                }

                                if (status.equals(UNVERIFIED)) {
                                    newCountUNVERIFIED++;
                                }

                                if (status.equals(FAKE)) {
                                    newCountFAKE++;
                                }
                            }

                            //set counters in cardview
                            user_home_tv_num_report_verified.setText(String.valueOf(newCountVERIFIED));
                            user_home_tv_num_report_unverified.setText(String.valueOf(newCountUNVERIFIED));
                            user_home_tv_num_report_fake.setText(String.valueOf(newCountFAKE));

                            String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                            if(!empty(phone))
                            {
                                //logged in with phone
                                // A B C D E
                                if(newCountVERIFIED>10 && newCountVERIFIED<=20){
                                    user_home_tv_gradeOfUser.setText("D");
                                }else if(newCountVERIFIED>20 && newCountVERIFIED<=30){
                                    user_home_tv_gradeOfUser.setText("C");
                                }else if(newCountVERIFIED>30 && newCountVERIFIED<=50){
                                    user_home_tv_gradeOfUser.setText("B");
                                }else if(newCountVERIFIED>50){
                                    user_home_tv_gradeOfUser.setText("A");
                                }else if(newCountVERIFIED<=10){
                                    user_home_tv_gradeOfUser.setText("E");
                                }
                            }

                            if(firebaseAuth.getCurrentUser().isAnonymous()){
                                user_home_tv_gradeOfUser.setText("E");
                            }



                            loadPieChartData(newCountVERIFIED, newCountUNVERIFIED, newCountFAKE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPieChartData(int newCountVERIFIED, int newCountUNVERIFIED, int newCountFAKE) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        try {
            String reports_verified = String.valueOf(getContext().getText(R.string.reports_verified));
            String reports_unverified = String.valueOf(getContext().getText(R.string.reports_unverified));
            String reports_fake = String.valueOf(getContext().getText(R.string.reports_fake));

            entries.add(new PieEntry((float) newCountVERIFIED, reports_verified));
            entries.add(new PieEntry((float) newCountUNVERIFIED, reports_unverified));
            entries.add(new PieEntry((float) newCountFAKE, reports_fake));
        }catch (Exception e){
            e.getMessage();
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for(int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for(int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(user_home_pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        user_home_pieChart.setData(data);
        user_home_pieChart.invalidate();
        user_home_pieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void setupPieChart() {
        user_home_pieChart.setDrawHoleEnabled(true);
        user_home_pieChart.setUsePercentValues(true);
        user_home_pieChart.setEntryLabelTextSize(12);
        user_home_pieChart.setEntryLabelColor(Color.BLACK);
        user_home_pieChart.setCenterText(getText(R.string.report));
        user_home_pieChart.setCenterTextSize(16f);
        user_home_pieChart.getDescription().setEnabled(false);

        Legend l = user_home_pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            user_hiDashboard.setText(getActivity().getText(R.string.hi)+" "+ANONYMOUS);
            user_imageOfDashboard.setImageResource(R.drawable.ic_anonymous);
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
                        fullName = task.getResult().getString("fullName");
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        balance = task.getResult().getDouble("balance");
                        if(balance != null){
                            int balanceInt = (int) Math.round(balance);
                                user_tv_balance.setText(String.valueOf(balanceInt));
                        }else {
                            user_tv_balance.setText("N/A");
                        }

                        //set Image, verified if is email verified, name
                        //setVerifiedBadge(firebaseAuth.getCurrentUser());
                        if(urlOfProfile != null){
                            Picasso.get().load(urlOfProfile).into(user_imageOfDashboard);
                        }

                        if(fullName != null){
                            user_hiDashboard.setText(getActivity().getText(R.string.hi)+" "+fullName);
                        }
                    }
                }
            });
        }
    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            //logged in with phone
            user_hiDashboard.setText(getActivity().getText(R.string.hi)+" "+phone);
            setSharedPreference(phone);
            user_imageOfDashboard.setImageResource(R.drawable.ic_phone_login);
        }
    }

    public void setSharedPreference(String phone) {
        SharedPreferences settings = getActivity().getSharedPreferences(HOME_USER_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    public static String getDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(getContext())){
            return false;
        }else {
            return true;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "ID_MODIFIED_REPORT";
            CharSequence name = "NotificationReportUser";
            String description = "This channel is for user, that send notification when status of report change in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}