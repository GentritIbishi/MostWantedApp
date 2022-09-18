package fiek.unipr.mostwantedapp.fragment.admin.search;

import static fiek.unipr.mostwantedapp.helpers.Constants.LOCATION_REPORTS;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.report.location.ManageLocationReportListAdapter;
import fiek.unipr.mostwantedapp.fragment.admin.SingleReportFragment;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Report;

public class SearchManageLocationReportsFragment extends Fragment implements RecyclerViewInterface {

    private View location_reports_view;
    private RecyclerView lvLocationReports_manage;
    private ManageLocationReportListAdapter manageLocationReportListAdapter;
    private ArrayList<Report> reportArrayList;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText locationReports_search_filter;

    public SearchManageLocationReportsFragment() {}

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
        manageLocationReportListAdapter = new ManageLocationReportListAdapter(getContext(), reportArrayList, this);
        lvLocationReports_manage.setAdapter(manageLocationReportListAdapter);
        lvLocationReports_manage.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection(LOCATION_REPORTS)
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
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }

                            manageLocationReportListAdapter.notifyDataSetChanged();
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
        firebaseFirestore.collection(LOCATION_REPORTS)
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
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }
                            manageLocationReportListAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(int position) {
        SingleReportFragment singleReportFragment = new SingleReportFragment();
        Bundle viewBundle = new Bundle();
        viewBundle.putString("date_time", reportArrayList.get(position).getDate_time());
        viewBundle.putString("title", reportArrayList.get(position).getTitle());
        viewBundle.putString("docId", reportArrayList.get(position).getDocId());
        viewBundle.putString("description", reportArrayList.get(position).getDescription());
        viewBundle.putString("informer_person", reportArrayList.get(position).getInformer_person());
        viewBundle.putString("status", reportArrayList.get(position).getStatus().toString());
        viewBundle.putString("informer_person_urlOfProfile", reportArrayList.get(position).getInformer_person_urlOfProfile());
        viewBundle.putDouble("latitude", reportArrayList.get(position).getLatitude());
        viewBundle.putDouble("longitude", reportArrayList.get(position).getLongitude());
        viewBundle.putString("uID", reportArrayList.get(position).getuID());
        viewBundle.putString("wanted_person", reportArrayList.get(position).getWanted_person());

        if(reportArrayList.get(position).getImages() == null || reportArrayList.get(position).getImages().equals(null) || reportArrayList.get(position).getImages().isEmpty()) {
            viewBundle.putInt("totalImages", 0);
            viewBundle.putStringArray("images", null);
        }else {
            viewBundle.putInt("totalImages", reportArrayList.get(position).getImages().size());

            int totalImages = reportArrayList.get(position).getImages().size();

            Map<String, Object> images;
            images = (Map<String, Object>) reportArrayList.get(position).getImages();

            String[] arrImages = new String[totalImages];

            for(int i = 0; i<totalImages; i++) {
                arrImages[i] = images.get("image"+i).toString();
            }

            viewBundle.putStringArray("images", arrImages);
        }
        singleReportFragment.setArguments(viewBundle);
        loadFragment(singleReportFragment);
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .commit();
    }
}