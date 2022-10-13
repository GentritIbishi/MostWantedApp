package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.FAKE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.UNVERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;
import static fiek.unipr.mostwantedapp.utils.ContextHelper.checkContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchInvoicePendingFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchLocationReportsFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchManageLocationReportsFragment;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class HomeFragment extends Fragment {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private Context contextAttach;
    private PieChart admin_home_pieChart;
    private TextView admin_home_tv_num_report_verified, admin_home_tv_num_report_unverified, admin_home_tv_num_report_fake, admin_home_tv_gradeOfUser;
    private TextView tv_analytics_today, tv_percentage_today, tv_analytics_weekly, tv_percentage_weekly;
    private ImageView imageTrendingToday, imageTrendingWeekly;

    private String userID;

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
    private ListenerRegistration registration;

    private String fullName, urlOfProfile, name, grade;

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
        userID = firebaseAuth.getCurrentUser().getUid();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_dashboard_view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        initializeFields();

        loadInfoFromFirebase(firebaseAuth);
        getGrade(firebaseAuth);

        final SwipeRefreshLayout pullToRefreshInSearch = admin_dashboard_view.findViewById(R.id.admin_home_pullToRefreshProfileDashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();


        if(checkContext(getContext()))
        {
            getAndSetTotalReportFor24H();
            vsYesterday(getContext());
            getAndSetTotalReportForOneWeek();
            vsWeek(getContext());
            rightNowDateTime.setText(DateHelper.getDateTimeStyle());
            getGrade(firebaseAuth);
            setupPieChart();
            setPieChart();
        }

        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(firebaseAuth != null){
                    if(checkContext(getContext()))
                    {
                        loadInfoFromFirebase(firebaseAuth);
                        setupPieChart();
                        setPieChart();
                        getAndSetTotalReportFor24H();
                        vsYesterday(getContext());
                        getAndSetTotalReportForOneWeek();
                        vsWeek(getContext());
                    }
                }

                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        loadInfoFromFirebase(firebaseAuth);

        final LinearLayout l_admin_myAccount = admin_dashboard_view.findViewById(R.id.l_admin_myAccount);
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
                Fragment fragment = new RegisterFragment();
                loadFragment(fragment);
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
                Fragment fragment = new SearchLocationReportsFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_manage_reports = admin_dashboard_view.findViewById(R.id.l_admin_manage_reports);
        l_admin_manage_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchManageLocationReportsFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_update = admin_dashboard_view.findViewById(R.id.l_admin_update);
        l_admin_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UpdateFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_manage_invoices = admin_dashboard_view.findViewById(R.id.l_admin_manage_invoices);
        l_admin_manage_invoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new InvoiceFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_payouts = admin_dashboard_view.findViewById(R.id.l_admin_payouts);
        l_admin_payouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PayoutsFragment();
                loadFragment(fragment);
            }
        });

        return admin_dashboard_view;
    }

    @Override
    public void onAttach(@NonNull Context contextAttach) {
        super.onAttach(contextAttach);
        this.contextAttach = contextAttach;
    }

    private void initializeFields() {
        imageOfAccount = admin_dashboard_view.findViewById(R.id.imageOfAccount);
        imageOfDashboard = admin_dashboard_view.findViewById(R.id.imageOfDashboard);
        admin_home_pieChart = admin_dashboard_view.findViewById(R.id.admin_home_pieChart);
        rightNowDateTime = admin_dashboard_view.findViewById(R.id.rightNowDateTime);
        hiDashboard = admin_dashboard_view.findViewById(R.id.hiDashboard);
        admin_home_tv_num_report_verified = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_verified);
        admin_home_tv_num_report_unverified = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_unverified);
        admin_home_tv_num_report_fake = admin_dashboard_view.findViewById(R.id.admin_home_tv_num_report_fake);
        admin_home_tv_gradeOfUser = admin_dashboard_view.findViewById(R.id.admin_home_tv_gradeOfUser);
        tv_percentage_today = admin_dashboard_view.findViewById(R.id.tv_percentage_today);
        tv_analytics_today = admin_dashboard_view.findViewById(R.id.tv_analytics_today);
        tv_analytics_weekly = admin_dashboard_view.findViewById(R.id.tv_analytics_weekly);
        tv_percentage_weekly = admin_dashboard_view.findViewById(R.id.tv_percentage_weekly);
        imageTrendingToday = admin_dashboard_view.findViewById(R.id.imageTrendingToday);
        imageTrendingWeekly = admin_dashboard_view.findViewById(R.id.imageTrendingWeekly);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(getContext())){
            firebaseFirestore
                    .collection(USERS)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                fullName = task.getResult().getString("fullName");
                                urlOfProfile = task.getResult().getString("urlOfProfile");
                                if(!urlOfProfile.isEmpty()){
                                    Picasso.get().load(urlOfProfile).into(imageOfAccount);
                                    Picasso.get().load(urlOfProfile).into(imageOfDashboard);
                                }else {
                                    imageOfAccount.setImageResource(R.drawable.ic_profile_picture_default);
                                    imageOfDashboard.setImageResource(R.drawable.ic_profile_picture_default);
                                }
                            }
                        }
                    });
        }
    }

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if(CheckInternet.isConnected(getContext())) {
            firebaseFirestore.collection(LOCATION_REPORTS)
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
        admin_home_pieChart.setDrawHoleEnabled(false);
        admin_home_pieChart.setUsePercentValues(true);
        admin_home_pieChart.setEntryLabelTextSize(14);
        admin_home_pieChart.setEntryLabelColor(Color.WHITE);
        admin_home_pieChart.getDescription().setEnabled(false);
        admin_home_pieChart.setExtraOffsets(5, 10, 5, 5);
        admin_home_pieChart.setDragDecelerationFrictionCoef(0.15f);
        admin_home_pieChart.setTransparentCircleRadius(61f);

        Legend l = admin_home_pieChart.getLegend();
        l.setTextColor(mContext.getResources().getColor(R.color.text));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(int newCountVERIFIED, int newCountUNVERIFIED, int newCountFAKE) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        try {
            String reports_verified = String.valueOf(getContext().getText(R.string.reports_verified));
            String reports_unverified = String.valueOf(getContext().getText(R.string.reports_pending));
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

    private void loadInfoAndSayHiFromFirebase(FirebaseAuth firebaseAuth, Context context) {
        if(CheckInternet.isConnected(getContext())){
            firebaseFirestore.collection(USERS)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                name = task.getResult().getString("name");
                                fullName = task.getResult().getString("fullName");
                                if(name != null){
                                    hiDashboard.setText(context.getText(R.string.hi)+" "+name);
                                }else {
                                    hiDashboard.setText(context.getText(R.string.dashboard));
                                }
                            }
                        }
                    });
        }
    }

    private void getGrade(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(getContext())){
            firebaseFirestore.collection(USERS)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                grade = task.getResult().getString("grade");
                                if(!StringHelper.empty(grade))
                                {
                                    admin_home_tv_gradeOfUser.setText(grade);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            if(checkContext(getContext())){
                loadInfoAndSayHiFromFirebase(firebaseAuth, getContext());
                loadInfoFromFirebase(firebaseAuth);
            }
        }
    }

    private void getAndSetTotalReportFor24H() {
        String date = DateHelper.getDate();
        String start = date+" "+"00:00:00";
        String end = date+" "+"23:59:59";
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereGreaterThan("date_time", start)
                .whereLessThan("date_time", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int num_of_reports_today = task.getResult().size();
                            tv_analytics_today.setText(num_of_reports_today+"");
                        }
                    }
                });
    }

    private void vsYesterday(Context context) {
        String today = DateHelper.getDate();
        String start_today = today+" "+"00:00:00";
        String end_today = today+" "+"23:59:59";

        String yesterday = DateHelper.getYesterday();
        String start = yesterday+" "+"00:00:00";
        String end = yesterday+" "+"23:59:59";
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereGreaterThan("date_time", start)
                .whereLessThan("date_time", end)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //sa reporte kan qen yesterday edhe me krahasu me today edhe me qit perqindjen
                            int yesterdayReports = task.getResult().size();

                            firebaseFirestore.collection(LOCATION_REPORTS)
                                    .whereGreaterThan("date_time", start_today)
                                    .whereLessThan("date_time", end_today)
                                    .orderBy("date_time", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                //ki me bo set per 24h sa reporte ne dite jan bo
                                                int todayReports = task.getResult().size();
                                                double num = (double) yesterdayReports/todayReports;
                                                double percentage = (double) num * 100;

                                                if(percentage > 0) {
                                                    imageTrendingToday.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                                    tv_percentage_today.setText(percentage+"%");
                                                    tv_percentage_today.setTextColor(context.getResources().getColor(R.color.neon_green));
                                                }else if(percentage < 0) {
                                                    imageTrendingToday.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                                    tv_percentage_today.setText(percentage+"%");
                                                    tv_percentage_today.setTextColor(context.getResources().getColor(R.color.neon_red));
                                                }else if(percentage == 0) {
                                                    imageTrendingToday.setImageResource(R.drawable.ic_baseline_trending_flat_24);
                                                    tv_percentage_today.setText(percentage+"%");
                                                    tv_percentage_today.setTextColor(context.getResources().getColor(R.color.bluelight));
                                                }
                                            }else {
                                                System.out.println("ERROR INSIDE VS YESTERDAY: "+task.getException());
                                            }
                                        }
                                    });
                        }else {
                            System.out.println("ERROR OUTSIDE VS YESTERDAY: "+task.getException());
                        }
                    }
                });
    }

    private void vsWeek(Context context) {
        String start_lastWeek = DateHelper.getFirstDayOfLastWeek()+" "+"00:00:00";
        String start_thisWeek = DateHelper.getFirstDayOfThisWeek()+" "+"00:00:00";

        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereGreaterThanOrEqualTo("date_time", start_lastWeek)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int lastWeekReports = task.getResult().size();
                            firebaseFirestore.collection(LOCATION_REPORTS)
                                    .whereGreaterThanOrEqualTo("date_time", start_thisWeek)
                                    .orderBy("date_time", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                //ki me bo set per 24h sa reporte ne dite jan bo
                                                int thisWeekReport = task.getResult().size();
                                                double num = (double) lastWeekReports/thisWeekReport;
                                                double percentage = (double) num * 100;

                                                if(percentage > 0) {
                                                    imageTrendingWeekly.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                                    tv_percentage_weekly.setText(percentage+"%");
                                                    tv_percentage_weekly.setTextColor(context.getResources().getColor(R.color.neon_green));
                                                }else if(percentage < 0) {
                                                    imageTrendingWeekly.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                                    tv_percentage_weekly.setText(percentage+"%");
                                                    tv_percentage_weekly.setTextColor(context.getResources().getColor(R.color.neon_red));
                                                }else if(percentage == 0) {
                                                    imageTrendingWeekly.setImageResource(R.drawable.ic_baseline_trending_flat_24);
                                                    tv_percentage_weekly.setText(percentage+"%");
                                                    tv_percentage_weekly.setTextColor(context.getResources().getColor(R.color.bluelight));
                                                }
                                            }else {
                                                System.out.println("ERROR INSIDE VS WEEK: "+task.getException());
                                            }
                                        }
                                    });
                        }else {
                            System.out.println("ERROR OUTSIDE VS WEEK: "+task.getException());
                        }
                    }
                });
    }

    private void getAndSetTotalReportForOneWeek() {
        String start_thisWeek = DateHelper.getFirstDayOfThisWeek()+" "+"00:00:00";

        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereGreaterThanOrEqualTo("date_time", start_thisWeek)
                .orderBy("date_time")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int num_of_reports = queryDocumentSnapshots.size();
                        tv_analytics_weekly.setText(num_of_reports+"");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}