package fiek.unipr.mostwantedapp.fragment.admin;

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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;


public class AnalyticsFragment extends Fragment {

    private static String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static String DATE_FORMAT = "dd-MM-yyyy";
    private View admin_profile_dashboard_view;
    private PieChart admin_pieChart;
    private BarChart barChartGender;
    private ArrayList barArraylist;
    private TextView tv_num_report_verified, tv_num_report_unverified, tv_num_report_fake, tv_gradeOfUser,
            tv_num_total_investigators, tv_num_total_person, tv_num_total_users, tv_num_location_reports,
            tv_percentage_today, tv_percentage_weekly;
    private ImageView imageTrendingToday, imageTrendingWeekly;

    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";
    public static final String FAKE = "FAKE";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String MALE = "MALE";
    public static final String FEMALE = "FEMALE";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String uID;
    private String fullName, grade;

    public AnalyticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();
        getGrade(firebaseAuth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_profile_dashboard_view = inflater.inflate(R.layout.fragment_analytics_admin, container, false);

        admin_pieChart = admin_profile_dashboard_view.findViewById(R.id.admin_pieChart);
        tv_num_report_verified = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_verified);
        tv_num_report_unverified = admin_profile_dashboard_view.findViewById(R.id.user_tv_balance);
        tv_num_report_fake = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_fake);
        tv_num_total_investigators = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_investigators);
        tv_num_total_person = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_person);
        tv_num_total_users = admin_profile_dashboard_view.findViewById(R.id.tv_num_total_users);
        tv_num_location_reports = admin_profile_dashboard_view.findViewById(R.id.tv_num_location_reports);
        tv_gradeOfUser = admin_profile_dashboard_view.findViewById(R.id.tv_gradeOfUser);
        barChartGender = admin_profile_dashboard_view.findViewById(R.id.barChartGender);
        imageTrendingToday = admin_profile_dashboard_view.findViewById(R.id.imageTrendingToday);
        imageTrendingWeekly = admin_profile_dashboard_view.findViewById(R.id.imageTrendingWeekly);
        tv_percentage_today = admin_profile_dashboard_view.findViewById(R.id.tv_percentage_today);
        tv_percentage_weekly = admin_profile_dashboard_view.findViewById(R.id.tv_percentage_weekly);

        final SwipeRefreshLayout pullToRefreshInSearch = admin_profile_dashboard_view.findViewById(R.id.admin_pullToRefreshProfileDashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        uID = firebaseUser.getUid();

        getGrade(firebaseAuth);
        setupPieChart();
        setPieChart();
        funAnalyticsGenderForWantedPerson();
        getAndSetTotalInvestigators();
        getAndSetTotalUsers();
        getAndSetTotalPerson();

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
                getAndSetTotalInvestigators();
                getAndSetTotalUsers();
                getAndSetTotalPerson();

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
        admin_pieChart.setDrawHoleEnabled(true);
        admin_pieChart.setUsePercentValues(true);
        admin_pieChart.setEntryLabelTextSize(12);
        admin_pieChart.setEntryLabelColor(Color.BLACK);
        admin_pieChart.setCenterText(getText(R.string.report));
        admin_pieChart.setCenterTextSize(16f);
        admin_pieChart.getDescription().setEnabled(false);

        Legend l = admin_pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
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
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(admin_pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        admin_pieChart.setData(data);
        admin_pieChart.invalidate();
        admin_pieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void loadInfoFromFirebase(FirebaseAuth firebaseAuth) {
        if(checkConnection()){
            firebaseFirestore.collection("users")
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

    private boolean checkConnection() {
        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(getContext())){
            Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private void funAnalyticsGenderForWantedPerson() {
        firebaseFirestore.collection("wanted_persons")
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

                        barArraylist = new ArrayList();
                        barArraylist.add(new BarEntry(2f, counterForMale, R.drawable.bt_edit_data));
                        barArraylist.add(new BarEntry(3f, counterForFemale, R.drawable.ic_phone_login));

                        //anychart here
                        BarDataSet barDataSet = new BarDataSet(barArraylist, getContext().getString(R.string.gender_for_wanted_person_statistic));
                        BarData barData = new BarData(barDataSet);
                        barChartGender.setData(barData);
                        //color bar data set
                        barDataSet.setColors(R.color.red, R.color.black);
                        //text color
                        barDataSet.setValueTextColor(Color.BLACK);
                        //settings text size
                        barDataSet.setValueTextSize(16f);
                        barChartGender.getDescription().setEnabled(true);



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "GenderAnalyticsError: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setNumberOfLocationsReports() {
        firebaseFirestore.collection("locations_reports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful() && task.getResult() != null){
                           Integer total_number_of_report_rn = task.getResult().size();
                           tv_num_location_reports.setText(total_number_of_report_rn+"");
                       }
                    }
                });
    }

    private void getAndSetTotalReportFor24H() {
        String date = getTimeDate();
        String start = date +"00:00:00";
        String end = date + "23:59:59";
        firebaseFirestore.collection("locations_reports")
                .whereGreaterThan("date_time", start)
                .whereLessThan("date_time", end)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //ki me bo set per 24h sa reporte ne dite jan bo
                        }
                    }
                });
    }

    private void vsYesterday() {
        String today = getTimeDate();
        String start_today = today +"00:00:00";
        String end_today = today + "23:59:59";

        String yesterday = getYesterday();
        String start = yesterday +"00:00:00";
        String end = yesterday + "23:59:59";
        firebaseFirestore.collection("locations_reports")
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

                            firebaseFirestore.collection("locations_reports")
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
                                                double percentage = (todayReports/yesterdayReports) * 100;
                                                tv_percentage_today.setText(percentage+"%");
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

    private void vsWeek() {
        String start_lastWeek = getFirstDayOfLastWeek() +"00:00:00";
        String end_lastWeek = getLastDayOfLastWeek() + "23:59:59";

        String start_thisWeek = getFirstDayOfThisWeek() +"00:00:00";
        String end_thisWeek = "23:59:59";

        firebaseFirestore.collection("locations_reports")
                .whereGreaterThan("date_time", start_lastWeek)
                .whereLessThan("date_time", end_lastWeek)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //sa reporte kan qen yesterday edhe me krahasu me today edhe me qit perqindjen
                            int lastMonthReports = task.getResult().size();

                            firebaseFirestore.collection("locations_reports")
                                    .whereGreaterThan("date_time", start_thisWeek)
                                    .whereLessThan("date_time", end_thisWeek)
                                    .orderBy("date_time", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                //ki me bo set per 24h sa reporte ne dite jan bo
                                                int thisMonthReports = task.getResult().size();
                                                double percentage = (thisMonthReports/lastMonthReports) * 100;
                                                if(percentage > 0){
                                                    tv_percentage_today.setText(percentage+"%");
                                                    imageTrendingWeekly.setImageResource(R.drawable.ic_baseline_trending_up_24);
                                                }else if(percentage < 0) {
                                                    tv_percentage_today.setText(percentage+"%");
                                                    imageTrendingWeekly.setImageResource(R.drawable.ic_baseline_trending_down_24);
                                                }
                                            }else {
                                                System.out.println("ERROR OUTSIDE VS Last Month: "+task.getException());
                                            }
                                        }
                                    });
                        }else {
                            System.out.println("ERROR OUTSIDE VS Last Month: "+task.getException());
                        }
                    }
                });
    }

    private void getAndSetTotalReportForOneWeek() {
        String date = getTimeDate();
        String start = date +"00:00:00";
        String end = getCalculatedDate(date, DATE_FORMAT, 7)+ "23:59:59";
        firebaseFirestore.collection("locations_reports")
                .whereGreaterThan("date_time", start)
                .whereLessThan("date_time", end)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //ki me bo set per 7days sa reporte ne dite jan bo
                        }
                    }
                });
    }

    private static int getCurrentWeek() {
        LocalDate date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.get(weekFields.weekOfWeekBasedYear());
    }

    public static Calendar firstDayOfThisWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        //current week
        c.add(Calendar.WEEK_OF_YEAR, getCurrentWeek());
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public static Calendar firstDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public static Calendar lastDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // first day of this week
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        // last day of previous week
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c;
    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    private static String getFirstDayOfLastWeek() {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String firstDayOfLastWeek = firstDayOfLastWeek(calendar).toString();
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(firstDayOfLastWeek);
        } catch(Exception e) {
            return "date";
        }
    }

    private static String getLastDayOfLastWeek() {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String lastDayOfLastWeek = lastDayOfLastWeek(calendar).toString();
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(lastDayOfLastWeek);
        } catch(Exception e) {
            return "date";
        }
    }

    private static String getFirstDayOfThisWeek() {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String firstDayOfThisWeek = firstDayOfThisWeek(calendar).toString();
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(firstDayOfThisWeek);
        } catch(Exception e) {
            return "date";
        }
    }

    public static String getLastMonthDate() { // without parameter argument
        try{
            LocalDate now = LocalDate.now(); // 2015-11-24
            LocalDate earlier = now.minusMonths(1); // 2015-10-24
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(earlier);
        } catch(Exception e) {
            return "date";
        }
    }

    public static String getThisMonth() { // without parameter argument
        try{
            LocalDate now = LocalDate.now(); // 2015-11-24
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(now);
        } catch(Exception e) {
            return "date";
        }
    }

    private static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterday() { // without parameter argument
        try{
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sfd.format(yesterday());
        } catch(Exception e) {
            return "date";
        }
    }

    public static String getCalculatedDate(String date,String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        if (!date.isEmpty()) {
            try {
                cal.setTime(s.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    private void getAndSetTotalUsers() {
        firebaseFirestore.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        firebaseFirestore.collection("investigators")
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
        firebaseFirestore.collection("wanted_persons")
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