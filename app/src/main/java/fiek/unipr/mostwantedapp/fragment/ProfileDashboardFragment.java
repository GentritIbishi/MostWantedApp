package fiek.unipr.mostwantedapp.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CheckInternet;


public class ProfileDashboardFragment extends Fragment {

    View profile_dashboard_view;
    TextView tv_totalCasesReportedVERIFIED, tv_totalCasesReportedUNVERIFIED;
    PieChart pieChart;

    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private String uID;
    private String fullName;
    private int countVERIFIED;
    private int countUNVERIFIED;

    public ProfileDashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profile_dashboard_view = inflater.inflate(R.layout.fragment_profile_dashboard, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();

        loadInfoFromFirebase(firebaseAuth);

        pieChart = profile_dashboard_view.findViewById(R.id.pieChart);
        tv_totalCasesReportedVERIFIED = profile_dashboard_view.findViewById(R.id.tv_totalCasesReportedVERIFIED);
        tv_totalCasesReportedUNVERIFIED = profile_dashboard_view.findViewById(R.id.tv_totalCasesReportedUNVERIFIED);
        final SwipeRefreshLayout pullToRefreshInSearch = profile_dashboard_view.findViewById(R.id.pullToRefreshProfileDashboard);

        pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                loadInfoFromFirebase(firebaseAuth);
                countVERIFIED = 0;
                countUNVERIFIED = 0;
                totalCasesReportedUNVERIFIED();
                totalCasesReportedVERIFIED();
                pieChart.startAnimation();

                pullToRefreshInSearch.setRefreshing(false);
            }
        });

        //setting datas
        totalCasesReportedUNVERIFIED();
        totalCasesReportedVERIFIED();
        pieChart.startAnimation();

        return profile_dashboard_view;
    }

    private void totalCasesReportedVERIFIED() {
        firebaseFirestore.collection("locations_reports")
                .whereEqualTo("status", VERIFIED)
                .whereEqualTo("informer_person", fullName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().isEmpty()){
                            Toast.makeText(getContext(), R.string.empty, Toast.LENGTH_SHORT).show();
                        }else {
                            countVERIFIED = 0;
                            countVERIFIED = queryDocumentSnapshots.getDocuments().size();
                            tv_totalCasesReportedVERIFIED.setText(Integer.toString(countVERIFIED));
                            setVerified(countVERIFIED);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void totalCasesReportedUNVERIFIED() {
        firebaseFirestore.collection("locations_reports")
                .whereEqualTo("status", UNVERIFIED)
                .whereEqualTo("informer_person", fullName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().isEmpty()){
                            Toast.makeText(getContext(), R.string.empty, Toast.LENGTH_SHORT).show();
                        }else {
                            countUNVERIFIED = 0;
                            countUNVERIFIED = queryDocumentSnapshots.getDocuments().size();
                            tv_totalCasesReportedUNVERIFIED.setText(Integer.toString(countUNVERIFIED));
                            setUnverified(countUNVERIFIED);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setVerified(Integer num) {

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "VERIFIED",
                        num,
                        Color.parseColor("#66BB6A")));
    }

    private void setUnverified(Integer num) {
        pieChart.addPieSlice(
                new PieModel(
                        "UNVERIFIED",
                        num,
                        Color.parseColor("#EF5350")));

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
                            }else {
                                Toast.makeText(getContext(), R.string.empty, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
}