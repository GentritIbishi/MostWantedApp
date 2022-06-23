package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;


public class ProfileFragment extends Fragment {

    private View admin_profile_dashboard_view;
    private PieChart admin_pieChart;
    private TextView tv_num_report_verified, tv_num_report_unverified, tv_num_report_fake, tv_gradeOfUser;

    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";
    public static final String FAKE = "FAKE";
    public static final String ANONYMOUS = "ANONYMOUS";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String uID;
    private String fullName, grade;

    public ProfileFragment() {
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
        admin_profile_dashboard_view = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        admin_pieChart = admin_profile_dashboard_view.findViewById(R.id.admin_pieChart);
        tv_num_report_verified = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_verified);
        tv_num_report_unverified = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_unverified);
        tv_num_report_fake = admin_profile_dashboard_view.findViewById(R.id.tv_num_report_fake);
        tv_gradeOfUser = admin_profile_dashboard_view.findViewById(R.id.tv_gradeOfUser);

        final SwipeRefreshLayout pullToRefreshInSearch = admin_profile_dashboard_view.findViewById(R.id.admin_pullToRefreshProfileDashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        uID = firebaseUser.getUid();

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
        entries.add(new PieEntry((float) newCountUNVERIFIED, String.valueOf(getActivity().getText(R.string.reports_unverified))));
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

}