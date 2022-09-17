package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.helpers.Constants.NOTIFICATION_USER;
import static fiek.unipr.mostwantedapp.helpers.Constants.SEEN;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.report.modified.ModifiedReportNotificationAdapter;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.maps.report.ReportInfoActivity;
import fiek.unipr.mostwantedapp.models.NotificationReportUser;

public class NotificationFragment extends Fragment implements RecyclerViewInterface {

    private View notification_fragment_view;
    private RecyclerView user_lvReportNotification;
    private ModifiedReportNotificationAdapter reportNotificationAdapter;
    private ArrayList<NotificationReportUser> reportArrayList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public NotificationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notification_fragment_view = inflater.inflate(R.layout.fragment_notification_user, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview(firebaseAuth.getUid());

        final SwipeRefreshLayout pullToRefreshInHome = notification_fragment_view.findViewById(R.id.user_pullToRefreshInNotification);
        pullToRefreshInHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                reportArrayList.clear();
                loadDatainListview(firebaseAuth.getUid());
                pullToRefreshInHome.setRefreshing(false);
            }
        });

        return notification_fragment_view;
    }

    private void InitializeFields() {
        user_lvReportNotification = notification_fragment_view.findViewById(R.id.user_lvReportNotification);
        reportArrayList = new ArrayList<>();
        reportNotificationAdapter = new ModifiedReportNotificationAdapter(getContext(), reportArrayList, this);
        user_lvReportNotification.setAdapter(reportNotificationAdapter);
        user_lvReportNotification.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadDatainListview(String notificationReportUID) {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection(NOTIFICATION_USER)
                .whereEqualTo("notificationReportUid", firebaseAuth.getUid())
                .orderBy("notificationDateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                NotificationReportUser report = d.toObject(NotificationReportUser.class);

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
                        Toast.makeText(getContext(), "Error_"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error"+e.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(getContext(), ReportInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle viewBundle = new Bundle();

        viewBundle.putString("notificationReportDateTime", reportArrayList.get(position).getNotificationReportDateTime());
        viewBundle.putString("notificationReportType", reportArrayList.get(position).getNotificationType());
        viewBundle.putString("notificationReportTitle", reportArrayList.get(position).getNotificationReportTitle());
        viewBundle.putString("notificationReportStatusChangedTo", reportArrayList.get(position).getNotificationReportNewStatus());
        viewBundle.putString("notificationDateTimeChanged", reportArrayList.get(position).getNotificationDateTime());
        viewBundle.putString("notificationReportBody", reportArrayList.get(position).getNotificationReportDescription());
        viewBundle.putString("notificationReportUID", reportArrayList.get(position).getNotificationReportUid());
        setSeenNotification(SEEN, reportArrayList.get(position).getNotificationId());

        intent.putExtras(viewBundle);
        startActivity(intent);
    }

    private void setSeenNotification(String status, String notificationReportId) {
        Map<String, Object> data = new HashMap<>();
        data.put("notificationStatus", status);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NOTIFICATION_USER)
                .document(notificationReportId)
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), getContext().getText(R.string.saved_successfully)+"", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getContext().getText(R.string.failed_to_save)+" ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}