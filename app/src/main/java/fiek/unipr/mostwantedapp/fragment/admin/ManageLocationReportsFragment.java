package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.ManageLocationReportsAdapter;
import fiek.unipr.mostwantedapp.adapter.ReportNotificationAdapter;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;

public class ManageLocationReportsFragment extends Fragment {

    private View location_reports_view;
    private ListView lvLocationReports_manage;
    private ManageLocationReportsAdapter manageLocationReportsAdapter;
    private ArrayList<Report> reportArrayList;
    private FirebaseFirestore firebaseFirestore;
    private String Description, Date_time, uID, informer_person, wanted_person, docId;
    private Map<String, Object> images = new HashMap<>();
    private ReportStatus status = ReportStatus.UNVERIFIED;
    private EditText locationReports_search_filter;
    private Double longitude, latitude;

    public ManageLocationReportsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        location_reports_view = inflater.inflate(R.layout.fragment_manage_location_reports, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout locationReports_pullToRefreshInSearch = location_reports_view.findViewById(R.id.locationReports_pullToRefreshInSearch);
        locationReports_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                reportArrayList.clear();
                loadDatainListview();
                locationReports_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        locationReports_search_filter.requestFocus();

        locationReports_search_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return location_reports_view;
    }

    private void InitializeFields() {
        locationReports_search_filter = location_reports_view.findViewById(R.id.locationReports_search_filter);
        lvLocationReports_manage = location_reports_view.findViewById(R.id.lvLocationReports_manage);
        reportArrayList = new ArrayList<>();
        manageLocationReportsAdapter = new ManageLocationReportsAdapter(getActivity().getApplicationContext(), reportArrayList);
        lvLocationReports_manage.setAdapter(manageLocationReportsAdapter);
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("locations_reports")
                .orderBy("date_time", Query.Direction.DESCENDING)
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
                                Report report = d.toObject(Report.class);

                                Date_time = report.getDate_time();
                                Description = report.getDescription();
                                informer_person = report.getInformer_person();
                                latitude = report.getLatitude();
                                longitude = report.getLongitude();
                                status = report.getStatus();
                                docId = report.getDocId();
                                uID = report.getuID();
                                wanted_person = report.getWanted_person();
                                images = report.getImages();


                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }

                            manageLocationReportsAdapter.notifyDataSetChanged();
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

    public void filter(String s) {
        reportArrayList.clear();
        firebaseFirestore.collection("locations_reports")
                .orderBy("docId")
                .startAt(s)
                .endAt(s + "\uf8ff")
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
                                Report report = d.toObject(Report.class);

                                 docId = report.getDocId();

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }
                            manageLocationReportsAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity().getApplicationContext(), R.string.please_type_name_like_hint, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}