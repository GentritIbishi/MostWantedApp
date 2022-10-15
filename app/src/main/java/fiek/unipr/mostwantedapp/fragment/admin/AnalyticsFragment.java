package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;
import static fiek.unipr.mostwantedapp.utils.Constants.FAKE;
import static fiek.unipr.mostwantedapp.utils.Constants.FEMALE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVESTIGATORS;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.MALE;
import static fiek.unipr.mostwantedapp.utils.Constants.UNVERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;


public class AnalyticsFragment extends Fragment {

    private Context mContext;
    private View admin_profile_dashboard_view;
    private PieChart admin_pieChart;
    private PieChart wp_gender_statistic_pieChart, inv_gender_statistic_pieChart, user_gender_statistic_pieChart;
    private TextView tv_num_report_verified, tv_num_report_unverified, tv_num_report_fake, tv_gradeOfUser,
            tv_num_total_investigators, tv_num_total_person, tv_num_total_users, tv_num_location_reports,
            tv_percent_today, tv_analytic_today, tv_percent_weekly, tv_analytic_weekly;
    private ImageView imageTrendToday, imageTrendWeekly;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String uID;
    private String fullName, grade;
    private String[] gender = {"MALE", "FEMALE"};

    public AnalyticsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_profile_dashboard_view = inflater.inflate(R.layout.fragment_analytics_admin, container, false);

        initializeFields();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        uID = firebaseAuth.getCurrentUser().getUid();

        getGrade(firebaseAuth);
        setupPieChart();
        setPieChart();
        funAnalyticsGenderForWantedPerson();
        funAnalyticsGenderForInvestigators();
        funAnalyticsGenderForUsers();
        getAndSetTotalInvestigators();
        getAndSetTotalUsers();
        getAndSetTotalPerson();

        getAndSetTotalReportFor24H();
        vsYesterday();
        getAndSetTotalReportForOneWeek();
        vsWeek();

        final SwipeRefreshLayout pullToRefreshInSearch = admin_profile_dashboard_view.findViewById(R.id.admin_pullToRefreshProfileDashboard);
        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(firebaseAuth != null){
                    loadInfoFromFirebase(firebaseAuth);
                }

                setNumberOfLocationsReports();
                setupPieChart();
                setPieChart();
                funAnalyticsGenderForWantedPerson();
                funAnalyticsGenderForInvestigators();
                funAnalyticsGenderForUsers();
                getAndSetTotalInvestigators();
                getAndSetTotalUsers();
                getAndSetTotalPerson();

                getAndSetTotalReportFor24H();
                vsYesterday();
                getAndSetTotalReportForOneWeek();
                vsWeek();

                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setNumberOfLocationsReports();
            }
        }, 200);

        return admin_profile_dashboard_view;
    }

    private void initializeFields() {
        admin_pieChart = admin_profile_dashboard_view.findViewById(R.id.admin_pieChart);
        tv_num_report_verified = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_verified);
        tv_num_report_unverified = admin_profile_dashboard_view.findViewById(R.id.user_tv_balance);
        tv_num_report_fake = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_fake);
        tv_num_total_investigators = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_investigators);
        tv_num_total_person = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_person);
        tv_num_total_users = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_users);
        tv_num_location_reports = admin_profile_dashboard_view.findViewById(R.id.tv_num_location_reports);
        tv_gradeOfUser = admin_profile_dashboard_view.findViewById(R.id.tv_gradeOfUser);
        wp_gender_statistic_pieChart = admin_profile_dashboard_view.findViewById(R.id.wp_gender_statistic_pieChart);
        inv_gender_statistic_pieChart = admin_profile_dashboard_view.findViewById(R.id.inv_gender_statistic_pieChart);
        user_gender_statistic_pieChart = admin_profile_dashboard_view.findViewById(R.id.user_gender_statistic_pieChart);
        imageTrendToday = admin_profile_dashboard_view.findViewById(R.id.imageTrendToday);
        tv_percent_today = admin_profile_dashboard_view.findViewById(R.id.tv_percent_today);
        tv_analytic_today = admin_profile_dashboard_view.findViewById(R.id.tv_analytic_today);
        imageTrendWeekly = admin_profile_dashboard_view.findViewById(R.id.imageTrendWeekly);
        tv_percent_weekly = admin_profile_dashboard_view.findViewById(R.id.tv_percent_weekly);
        tv_analytic_weekly = admin_profile_dashboard_view.findViewById(R.id.tv_analytic_weekly);

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
                            tv_num_report_verified.setText(String.valueOf(newCountVERIFIED));
                            tv_num_report_unverified.setText(String.valueOf(newCountUNVERIFIED));
                            tv_num_report_fake.setText(String.valueOf(newCountFAKE));
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
        admin_pieChart.setDrawHoleEnabled(false);
        admin_pieChart.setUsePercentValues(true);
        admin_pieChart.setEntryLabelTextSize(14);
        admin_pieChart.setEntryLabelColor(Color.WHITE);
        admin_pieChart.getDescription().setEnabled(false);
        admin_pieChart.setExtraOffsets(5, 10, 5, 5);
        admin_pieChart.setDragDecelerationFrictionCoef(0.15f);
        admin_pieChart.setTransparentCircleRadius(61f);

        Legend l = admin_pieChart.getLegend();
        l.setTextColor(mContext.getResources().getColor(R.color.text));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(int newCountVERIFIED, int newCountUNVERIFIED, int newCountFAKE) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) newCountVERIFIED, String.valueOf(getActivity().getText(R.string.reports_verified))));
        entries.add(new PieEntry((float) newCountUNVERIFIED, String.valueOf(getActivity().getText(R.string.reports_pending))));
        entries.add(new PieEntry((float) newCountFAKE, String.valueOf(getActivity().getText(R.string.reports_fake))));

        ArrayList<Integer> colors = new ArrayList<>();

        for(int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for(int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(1f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(22f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(admin_pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        admin_pieChart.setData(data);
        admin_pieChart.invalidate();
        admin_pieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(CheckInternet.isConnected(getContext())){
            firebaseFirestore.collection(USERS)
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                fullName = task.getResult().getString("fullName");
                            }
                        }
                    });
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
                            tv_analytic_today.setText(num_of_reports_today+"");
                        }
                    }
                });
    }

    private void vsYesterday() {
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
                                                //formula
                                                //   ((todayReports-yesterdayReports)/(todayReports+yesterdayReports)^2)*100
                                                int todayReports = task.getResult().size();
                                                double positive = todayReports+yesterdayReports;
                                                double doublePositive = Math.pow(positive,2);
                                                double negative = todayReports-yesterdayReports;
                                                double percentage = (negative/doublePositive)*100;

                                                if(percentage>0)
                                                {
                                                    imageTrendToday.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                                    tv_percent_today.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_today.setTextColor(getResources().getColor(R.color.neon_green));
                                                }else if(percentage<0)
                                                {
                                                    imageTrendToday.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                                    tv_percent_today.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_today.setTextColor(getResources().getColor(R.color.neon_red));
                                                }else if(percentage == 0)
                                                {
                                                    imageTrendToday.setImageResource(R.drawable.ic_baseline_trending_flat_24);
                                                    tv_percent_today.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_today.setTextColor(getResources().getColor(R.color.bluelight));
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
                        tv_analytic_weekly.setText(num_of_reports+"");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void vsWeek() {
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
                                                //formula ((V1-V2)/((V1+V2)^2))*100
                                                int thisWeekReport = task.getResult().size();
                                                double positive = thisWeekReport+lastWeekReports;
                                                double doublePositive = Math.pow(positive,2);
                                                double negative = thisWeekReport-lastWeekReports;
                                                double percentage = (negative/doublePositive)*100;

                                                if(percentage>0) {
                                                    imageTrendWeekly.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                                    tv_percent_weekly.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_weekly.setTextColor(getResources().getColor(R.color.neon_green));
                                                }else if(percentage<0) {
                                                    imageTrendWeekly.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                                    tv_percent_weekly.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_weekly.setTextColor(getResources().getColor(R.color.neon_red));
                                                }else if(percentage == 0) {
                                                    imageTrendWeekly.setImageResource(R.drawable.ic_baseline_trending_flat_24);
                                                    tv_percent_weekly.setText(new DecimalFormat("##.##").format(percentage)+"%");
                                                    tv_percent_weekly.setTextColor(getResources().getColor(R.color.bluelight));
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
                                tv_gradeOfUser.setText(grade);
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            loadInfoFromFirebase(firebaseAuth);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(firebaseAuth != null){
            loadInfoFromFirebase(firebaseAuth);
        }
    }

    private void funAnalyticsGenderForWantedPerson() {
        firebaseFirestore.collection(WANTED_PERSONS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int counterForMale = 0;
                        int counterForFemale = 0;

                        for (int i=0; i<queryDocumentSnapshots.size();i++){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            String gender = doc.getString("gender");

                            if(gender.equals(MALE)){
                                counterForMale++;
                            }else if(gender.equals(FEMALE)){
                                counterForFemale++;
                            }
                        }
                        //we can use counterForFemale we can use counterForMale

                        wp_gender_statistic_pieChart.setDrawHoleEnabled(false);
                        wp_gender_statistic_pieChart.setUsePercentValues(true);
                        wp_gender_statistic_pieChart.setEntryLabelTextSize(14);
                        wp_gender_statistic_pieChart.setHoleColor(Color.WHITE);
                        wp_gender_statistic_pieChart.setExtraOffsets(5, 10, 5, 5);
                        wp_gender_statistic_pieChart.setEntryLabelColor(Color.WHITE);
                        wp_gender_statistic_pieChart.getDescription().setEnabled(false);
                        wp_gender_statistic_pieChart.setDragDecelerationFrictionCoef(0.95f);
                        wp_gender_statistic_pieChart.setTransparentCircleRadius(61f);

                        Legend l = wp_gender_statistic_pieChart.getLegend();
                        l.setTextColor(mContext.getResources().getColor(R.color.text));
                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        l.setDrawInside(false);
                        l.setEnabled(true);

                        ArrayList<PieEntry> yValues = new ArrayList<>();
                        ArrayList<String> sValues = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_array)));

                        yValues.add(new PieEntry(counterForMale, sValues.get(0)));
                        yValues.add(new PieEntry(counterForFemale, sValues.get(1)));

                        PieDataSet dataSet = new PieDataSet(yValues, "");
                        dataSet.setSliceSpace(5f);
                        dataSet.setSelectionShift(1f);
                        dataSet.setValueTextColor(Color.WHITE);
                        dataSet.setValueTextSize(40f);
                        dataSet.setColors(getResources().getColor(R.color.graph_color_1),
                                getResources().getColor(R.color.graph_color_2));

                        PieData data = new PieData(dataSet);
                        data.setValueTextSize(15f);
                        data.setValueFormatter(new PercentFormatter(wp_gender_statistic_pieChart));
                        data.setValueTextColor(Color.WHITE);

                        wp_gender_statistic_pieChart.setData(data);
                        wp_gender_statistic_pieChart.invalidate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "GenderAnalyticsError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void funAnalyticsGenderForInvestigators() {
        firebaseFirestore.collection(INVESTIGATORS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int counterForMale = 0;
                        int counterForFemale = 0;

                        for (int i=0; i<queryDocumentSnapshots.size();i++){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            String gender = doc.getString("gender");

                            if(gender.equals(MALE)){
                                counterForMale++;
                            }else if(gender.equals(FEMALE)){
                                counterForFemale++;
                            }
                        }
                        //we can use counterForFemale we can use counterForMale

                        inv_gender_statistic_pieChart.setDrawHoleEnabled(false);
                        inv_gender_statistic_pieChart.setUsePercentValues(true);
                        inv_gender_statistic_pieChart.setEntryLabelTextSize(14);
                        inv_gender_statistic_pieChart.setHoleColor(Color.WHITE);
                        inv_gender_statistic_pieChart.setExtraOffsets(5, 10, 5, 5);
                        inv_gender_statistic_pieChart.setEntryLabelColor(Color.WHITE);
                        inv_gender_statistic_pieChart.getDescription().setEnabled(false);
                        inv_gender_statistic_pieChart.setDragDecelerationFrictionCoef(0.95f);
                        inv_gender_statistic_pieChart.setTransparentCircleRadius(61f);

                        Legend l = inv_gender_statistic_pieChart.getLegend();
                        l.setTextColor(mContext.getResources().getColor(R.color.text));
                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        l.setDrawInside(false);
                        l.setEnabled(true);

                        ArrayList<PieEntry> yValues = new ArrayList<>();
                        ArrayList<String> sValues = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_array)));

                        yValues.add(new PieEntry(counterForMale, sValues.get(0)));
                        yValues.add(new PieEntry(counterForFemale, sValues.get(1)));

                        PieDataSet dataSet = new PieDataSet(yValues, "");
                        dataSet.setSliceSpace(5f);
                        dataSet.setSelectionShift(1f);
                        dataSet.setValueTextColor(Color.WHITE);
                        dataSet.setValueTextSize(40f);
                        dataSet.setColors(getResources().getColor(R.color.graph_color_1),
                                getResources().getColor(R.color.graph_color_2));

                        PieData data = new PieData(dataSet);
                        data.setValueTextSize(15f);
                        data.setValueFormatter(new PercentFormatter(inv_gender_statistic_pieChart));
                        data.setValueTextColor(Color.WHITE);

                        inv_gender_statistic_pieChart.setData(data);
                        inv_gender_statistic_pieChart.invalidate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "GenderAnalyticsError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void funAnalyticsGenderForUsers() {
        firebaseFirestore.collection(USERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int counterForMale = 0;
                        int counterForFemale = 0;

                        for (int i=0; i<queryDocumentSnapshots.size();i++){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            String gender = doc.getString("gender");

                            if(gender.equals(MALE)){
                                counterForMale++;
                            }else if(gender.equals(FEMALE)){
                                counterForFemale++;
                            }
                        }
                        //we can use counterForFemale we can use counterForMale

                        user_gender_statistic_pieChart.setDrawHoleEnabled(false);
                        user_gender_statistic_pieChart.setUsePercentValues(true);
                        user_gender_statistic_pieChart.setEntryLabelTextSize(14);
                        user_gender_statistic_pieChart.setHoleColor(Color.WHITE);
                        user_gender_statistic_pieChart.setExtraOffsets(5, 10, 5, 5);
                        user_gender_statistic_pieChart.setEntryLabelColor(Color.WHITE);
                        user_gender_statistic_pieChart.getDescription().setEnabled(false);
                        user_gender_statistic_pieChart.setDragDecelerationFrictionCoef(0.95f);
                        user_gender_statistic_pieChart.setTransparentCircleRadius(61f);

                        Legend l = user_gender_statistic_pieChart.getLegend();
                        l.setTextColor(mContext.getResources().getColor(R.color.text));
                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        l.setDrawInside(false);
                        l.setEnabled(true);

                        ArrayList<PieEntry> yValues = new ArrayList<>();
                        ArrayList<String> sValues = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender_array)));

                        yValues.add(new PieEntry(counterForMale, sValues.get(0)));
                        yValues.add(new PieEntry(counterForFemale, sValues.get(1)));

                        PieDataSet dataSet = new PieDataSet(yValues, "");
                        dataSet.setSliceSpace(5f);
                        dataSet.setSelectionShift(1f);
                        dataSet.setValueTextColor(Color.WHITE);
                        dataSet.setValueTextSize(40f);
                        dataSet.setColors(getResources().getColor(R.color.graph_color_1),
                                getResources().getColor(R.color.graph_color_2));

                        PieData data = new PieData(dataSet);
                        data.setValueTextSize(15f);
                        data.setValueFormatter(new PercentFormatter(user_gender_statistic_pieChart));
                        data.setValueTextColor(Color.WHITE);

                        user_gender_statistic_pieChart.setData(data);
                        user_gender_statistic_pieChart.invalidate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "GenderAnalyticsError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setNumberOfLocationsReports() {
        firebaseFirestore.collection(LOCATION_REPORTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful() && task.getResult() != null){
                           Integer total_number_of_report_rn = task.getResult().size();
                           tv_num_location_reports.setText(total_number_of_report_rn+"");
                       }
                    }
                });
    }

    private void getAndSetTotalUsers() {
        firebaseFirestore.collection(USERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int total = queryDocumentSnapshots.size();
                        tv_num_total_users.setText(total+"");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAndSetTotalInvestigators() {
        firebaseFirestore.collection(INVESTIGATORS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int total = queryDocumentSnapshots.size();
                        tv_num_total_investigators.setText(total+"");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAndSetTotalPerson() {
        firebaseFirestore.collection(WANTED_PERSONS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int total = queryDocumentSnapshots.size();
                        tv_num_total_person.setText(total+"");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}