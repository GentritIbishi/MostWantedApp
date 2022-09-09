package fiek.unipr.mostwantedapp.fragment.admin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import fiek.unipr.mostwantedapp.adapter.ReportNotificationAdapter;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.models.ReportStatus;


public class NotificationFragment extends Fragment {

    private View notification_fragment_view;
    private ListView lvReportNotification;
    private ReportNotificationAdapter reportNotificationAdapter;
    private ArrayList<Report> reportArrayList;
    private FirebaseFirestore firebaseFirestore;
    private String Description, Date_time, uID, informer_person, wanted_person;
    private Map<String, Object> images = new HashMap<>();
    private ReportStatus status = ReportStatus.UNVERIFIED;
    private Double longitude, latitude;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNotificationPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notification_fragment_view = inflater.inflate(R.layout.fragment_notification_admin, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout pullToRefreshInHome = notification_fragment_view.findViewById(R.id.pullToRefreshInNotification);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                reportArrayList.clear();
                loadDatainListview();
                pullToRefreshInHome.setRefreshing(false);
            }
        });
        return notification_fragment_view;

    }

    private void InitializeFields() {
        lvReportNotification = notification_fragment_view.findViewById(R.id.lvReportNotification);
        reportArrayList = new ArrayList<>();
        reportNotificationAdapter = new ReportNotificationAdapter(getActivity().getApplicationContext(), reportArrayList);
        lvReportNotification.setAdapter(reportNotificationAdapter);
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
                                uID = report.getuID();
                                wanted_person = report.getWanted_person();
                                images = report.getImages();


                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }

                            reportNotificationAdapter.notifyDataSetChanged();
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

    private void checkNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}