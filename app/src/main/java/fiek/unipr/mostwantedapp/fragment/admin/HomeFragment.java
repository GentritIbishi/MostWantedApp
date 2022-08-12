package fiek.unipr.mostwantedapp.fragment.admin;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;
import fiek.unipr.mostwantedapp.models.NotificationAdmin;
import fiek.unipr.mostwantedapp.models.NotificationAdminState;
import fiek.unipr.mostwantedapp.register.RegisterPersonActivity;
import fiek.unipr.mostwantedapp.register.RegisterUsersActivity;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private PieChart admin_home_pieChart;
    private TextView admin_home_tv_num_report_verified, admin_home_tv_num_report_unverified, admin_home_tv_num_report_fake, admin_home_tv_gradeOfUser;
    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";
    public static final String FAKE = "FAKE";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

    private String uID;


    private View admin_dashboard_view;
    private CircleImageView imageOfAccount, imageOfDashboard;
    private TextView rightNowDateTime, hiDashboard;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String user_anonymousID = null;

    private Integer balance;
    private String fullName, urlOfProfile, name, lastname, email, googleID, grade, parentName, address, phone, personal_number;
    private Uri photoURL;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();
        getGrade(firebaseAuth);
        loadInfoAnonymousFirebase();
        loadInfoFromFirebase(firebaseAuth);
        loadInfoPhoneFirebase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_dashboard_view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        imageOfAccount = admin_dashboard_view.findViewById(R.id.imageOfAccount);
        imageOfDashboard = admin_dashboard_view.findViewById(R.id.imageOfDashboard);

        admin_home_pieChart = admin_dashboard_view.findViewById(R.id.admin_home_pieChart);
        rightNowDateTime = admin_dashboard_view.findViewById(R.id.rightNowDateTime);
        hiDashboard = admin_dashboard_view.findViewById(R.id.hiDashboard);
        admin_home_tv_num_report_verified = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_verified);
        admin_home_tv_num_report_unverified = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_unverified);
        admin_home_tv_num_report_fake = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_fake);
        admin_home_tv_gradeOfUser = admin_dashboard_view.findViewById(R.id.admin_home_tv_gradeOfUser);

        final SwipeRefreshLayout pullToRefreshInSearch = admin_dashboard_view.findViewById(R.id.admin_home_pullToRefreshProfileDashboard);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        rightNowDateTime.setText(getDate());
        getGrade(firebaseAuth);
        setupPieChart();
        setPieChart();

        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(firebaseAuth != null){
                    loadInfoFromFirebase(firebaseAuth);
                }

                setupPieChart();
                setPieChart();

                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        loadInfoAnonymousFirebase();
        loadInfoFromFirebase(firebaseAuth);
        loadInfoPhoneFirebase();

        final LinearLayout l_admin_myAccount = admin_dashboard_view.findViewById(R.id.l_admin_myAccount);
        final GridLayout gridLayout = admin_dashboard_view.findViewById(R.id.gridLayout);
        final View registerUsers = admin_dashboard_view.findViewById(R.id.registerUsers);

        final ConstraintLayout constraintPU = registerUsers.findViewById(R.id.constraintPU);
        final ConstraintLayout constraintWP = registerUsers.findViewById(R.id.constraintWP);
        final ConstraintLayout cons_dashboard_top = admin_dashboard_view.findViewById(R.id.cons_dashboard_top);
        final ConstraintLayout cons_analytics = admin_dashboard_view.findViewById(R.id.cons_analytics);

        constraintPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ni regjister te ri mas miri, me role Super-Admin, Admin, User(informer)
                Intent intent = new Intent(getContext(), RegisterUsersActivity.class);
                startActivity(intent);
            }
        });

        constraintWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterPersonActivity.class);
                startActivity(intent);
            }
        });


        l_admin_myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ProfileFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_register = admin_dashboard_view.findViewById(R.id.l_admin_register);
        l_admin_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridLayout.setVisibility(View.GONE);
                registerUsers.setVisibility(View.VISIBLE);
                cons_dashboard_top.setVisibility(View.GONE);
                cons_analytics.setVisibility(View.GONE);
            }
        });

        final LinearLayout l_admin_analytics = admin_dashboard_view.findViewById(R.id.l_admin_analytics);
        l_admin_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AnalyticsFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_locationReports = admin_dashboard_view.findViewById(R.id.l_admin_locationReports);
        l_admin_locationReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LocationReportsFragment();
                loadFragment(fragment);
            }
        });

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                realTimeCheckForNewReportNotification();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);


        return admin_dashboard_view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadInfoAnonymousFirebase() {
        if(firebaseAuth.getCurrentUser().isAnonymous()){
            imageOfAccount.setImageResource(R.drawable.ic_anonymous);
            imageOfDashboard.setImageResource(R.drawable.ic_anonymous);
        }
    }

    private void loadInfoPhoneFirebase() {
        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(!empty(phone))
        {
            imageOfAccount.setImageResource(R.drawable.ic_phone_login);
            imageOfDashboard.setImageResource(R.drawable.ic_phone_login);
        }
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            firebaseFirestore
                    .collection("users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                fullName = task.getResult().getString("fullName");
                                urlOfProfile = task.getResult().getString("urlOfProfile");
                                if(urlOfProfile != null){
                                    if(imageOfAccount != null){
                                        Picasso.get().load(urlOfProfile).into(imageOfAccount);
                                    }
                                    Picasso.get().load(urlOfProfile).into(imageOfDashboard);
                                }
                            }
                        }
                    });
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

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if(checkConnection()) {
            firebaseFirestore.collection("locations_reports")
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
                            admin_home_tv_num_report_verified.setText(String.valueOf(newCountVERIFIED));
                            admin_home_tv_num_report_unverified.setText(String.valueOf(newCountUNVERIFIED));
                            admin_home_tv_num_report_fake.setText(String.valueOf(newCountFAKE));
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

    private void setupPieChart() {
        admin_home_pieChart.setDrawHoleEnabled(true);
        admin_home_pieChart.setUsePercentValues(true);
        admin_home_pieChart.setEntryLabelTextSize(12);
        admin_home_pieChart.setEntryLabelColor(Color.BLACK);
        admin_home_pieChart.setCenterText(getText(R.string.report));
        admin_home_pieChart.setCenterTextSize(16f);
        admin_home_pieChart.getDescription().setEnabled(false);

        Legend l = admin_home_pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
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
        data.setValueFormatter(new PercentFormatter(admin_home_pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        admin_home_pieChart.setData(data);
        admin_home_pieChart.invalidate();
        admin_home_pieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void loadInfoAndSayHiFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            firebaseFirestore.collection("users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                name = task.getResult().getString("name");
                                fullName = task.getResult().getString("fullName");
                                hiDashboard.setText(getActivity().getText(R.string.hi)+" "+name);
                            }
                        }
                    });
        }
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
                                admin_home_tv_gradeOfUser.setText(grade);
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            createNotificationChannelAdded();
            createNotificationChannelModified();
            createNotificationChannelRemoved();
            loadInfoAndSayHiFromFirebase(firebaseAuth);
            loadInfoAnonymousFirebase();
            loadInfoFromFirebase(firebaseAuth);
            loadInfoPhoneFirebase();
        }
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

    public static String getDateTime() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    private void realTimeCheckForNewReportNotification() {
        firebaseFirestore.collection("locations_reports")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            String notificationBody = dc.getDocument().getString("description");
                            String notificationTitle = dc.getDocument().getString("title");
                            switch (dc.getType()) {
                                case ADDED:
                                    saveNotificationInFirestoreAdded(getDateTime(), notificationTitle, notificationBody, String.valueOf(NotificationAdminState.ADDED));
                                    break;
                                case MODIFIED:
                                    saveNotificationInFirestoreModified(getDateTime(), notificationTitle, notificationBody, String.valueOf(NotificationAdminState.MODIFIED));
                                    break;
                                case REMOVED:
                                    saveNotificationInFirestoreRemoved(getDateTime(), notificationTitle, notificationBody, String.valueOf(NotificationAdminState.REMOVED));
                                    break;
                            }
                        }

                    }
                });
    }


    private void saveNotificationInFirestoreAdded(String notificationTime, String notificationTitle, String notificationBody, String notificationType) {
        NotificationAdmin objNotificationAdmin = new NotificationAdmin(notificationTime, notificationBody, notificationTitle, notificationType);
        firebaseFirestore.collection("notifications_admin")
                .whereEqualTo("notificationTitle", notificationTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.getResult().size() == 0){
                           firebaseFirestore.collection("notifications_admin").document().set(objNotificationAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   //not exist make notification and save for next time
                                   Uri new_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                   NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), "NEW_REPORT_ADDED");
                                   notificationBuilder.setContentTitle(notificationTitle);
                                   notificationBuilder.setContentText(notificationBody);
                                   notificationBuilder.setSmallIcon(R.drawable.ic_app);
                                   notificationBuilder.setPriority(IMPORTANCE);
                                   notificationBuilder.setSound(new_defaultSoundUri);
                                   notificationBuilder.setAutoCancel(true);

                                   sharedPreferences = getActivity().getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
                                   int notificationNumber1 = sharedPreferences.getInt("notificationNumber1", 1);

                                   NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                   notificationManager.notify(notificationNumber1, notificationBuilder.build());

                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                   notificationNumber1++;
                                   editor.putInt("notificationNumber1", notificationNumber1);
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

    private void saveNotificationInFirestoreModified(String notificationTime, String notificationTitle, String notificationBody, String notificationType) {
        NotificationAdmin objNotificationAdmin = new NotificationAdmin(notificationTime, notificationBody, notificationTitle, notificationType);
        firebaseFirestore.collection("notifications_admin")
                .whereEqualTo("notificationTitle", notificationTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() == 0){
                            firebaseFirestore.collection("notifications_admin").document().set(objNotificationAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //not exist make notification and save for next time
                                    Uri modified_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), "NEW_REPORT_MODIFIED");
                                    notificationBuilder.setContentTitle(notificationTitle);
                                    notificationBuilder.setContentText(notificationBody);
                                    notificationBuilder.setSmallIcon(R.drawable.ic_app);
                                    notificationBuilder.setPriority(IMPORTANCE);
                                    notificationBuilder.setSound(modified_defaultSoundUri);
                                    notificationBuilder.setAutoCancel(true);

                                    sharedPreferences = getActivity().getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
                                    int notificationNumber2 = sharedPreferences.getInt("notificationNumber2", 2);

                                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notificationNumber2, notificationBuilder.build());

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    notificationNumber2++;
                                    editor.putInt("notificationNumber2", notificationNumber2);
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

    private void saveNotificationInFirestoreRemoved(String notificationTime, String notificationTitle, String notificationBody, String notificationType) {
        NotificationAdmin objNotificationAdmin = new NotificationAdmin(notificationTime, notificationBody, notificationTitle, notificationType);
        firebaseFirestore.collection("notifications_admin")
                .whereEqualTo("notificationTitle", notificationTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() == 0){
                            firebaseFirestore.collection("notifications_admin").document().set(objNotificationAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //not exist make notification and save for next time
                                    Uri removed_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), "NEW_REPORT_REMOVED");
                                    notificationBuilder.setContentTitle(notificationTitle);
                                    notificationBuilder.setContentText(notificationBody);
                                    notificationBuilder.setSmallIcon(R.drawable.ic_app);
                                    notificationBuilder.setPriority(IMPORTANCE);
                                    notificationBuilder.setSound(removed_defaultSoundUri);
                                    notificationBuilder.setAutoCancel(true);

                                    sharedPreferences = getActivity().getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
                                    int notificationNumber3 = sharedPreferences.getInt("notificationNumber3", 3);

                                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notificationNumber3, notificationBuilder.build());

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    notificationNumber3++;
                                    editor.putInt("notificationNumber3", notificationNumber3);
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

    private void createNotificationChannelAdded() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "NEW_REPORT_ADDED";
            CharSequence name = "ADMIN NEW REPORT ADDED NOTIFICATION";
            String description = "This channel is for admin, that send notification when new report added in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelModified() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "NEW_REPORT_MODIFIED";
            CharSequence name = "ADMIN REPORT MODIFIED NOTIFICATION";
            String description = "This channel is for admin, that send notification when one report modified in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelRemoved() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "NEW_REPORT_REMOVED";
            CharSequence name = "ADMIN REPORT REMOVED NOTIFICATION";
            String description = "This channel is for admin, that send notification when one report removed from database!";
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