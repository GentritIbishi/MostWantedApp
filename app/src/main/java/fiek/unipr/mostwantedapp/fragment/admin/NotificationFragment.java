package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.report.report.ReportNotificationAdapter;
import fiek.unipr.mostwantedapp.utils.MyButtonClickListener;
import fiek.unipr.mostwantedapp.utils.MySwipeHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Report;


public class NotificationFragment extends Fragment implements RecyclerViewInterface {

    private View notification_fragment_view;
    private RecyclerView lvReportNotification;
    private LinearLayout notification_admin_list_view1, notification_admin_list_view2;
    private TextView tv_notification_admin_userListEmpty;
    private ViewSwitcher notification_admin_list_switcher;
    private ReportNotificationAdapter reportNotificationAdapter;
    private ArrayList<Report> reportArrayList;
    private FirebaseFirestore firebaseFirestore;
    private TextView tv_empty;

    public NotificationFragment() {}

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

        swipeDeleteOnRecyclerList(lvReportNotification, reportArrayList);

        return notification_fragment_view;

    }

    private void InitializeFields() {
        lvReportNotification = notification_fragment_view.findViewById(R.id.lvReportNotification);
        notification_admin_list_view1 = notification_fragment_view.findViewById(R.id.notification_admin_list_view1);
        notification_admin_list_view2 = notification_fragment_view.findViewById(R.id.notification_admin_list_view2);
        notification_admin_list_switcher = notification_fragment_view.findViewById(R.id.notification_admin_list_switcher);
        tv_notification_admin_userListEmpty = notification_fragment_view.findViewById(R.id.tv_notification_admin_userListEmpty);
        reportArrayList = new ArrayList<>();
        reportNotificationAdapter = new ReportNotificationAdapter(getContext(), reportArrayList, this);
        lvReportNotification.setAdapter(reportNotificationAdapter);
        lvReportNotification.setLayoutManager(new LinearLayoutManager(getContext()));
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
                            if(notification_admin_list_switcher.getCurrentView() == notification_admin_list_view2){
                                notification_admin_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Report report = d.toObject(Report.class);
                                reportArrayList.add(report);
                            }

                            reportNotificationAdapter.notifyDataSetChanged();
                        } else {
                            if(notification_admin_list_switcher.getCurrentView() == notification_admin_list_view1){
                                notification_admin_list_switcher.showNext();
                            }
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

    private void swipeDeleteOnRecyclerList(RecyclerView lvReportNotification, ArrayList<Report> reportArrayList) {
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), lvReportNotification, 200)
        {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(),
                        getContext().getString(R.string.delete),
                        40,
                        0,
                        getResources().getColor(R.color.red_fixed),
                        new MyButtonClickListener(){

                            @Override
                            public void onClick(int pos) {
                                String docIdToDelete = reportArrayList.get(pos).getDocId();
                                firebaseFirestore.collection("locations_reports")
                                        .document(docIdToDelete)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.this_report_is_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                reportArrayList.clear();
                                                loadDatainListview();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.error_report_failed_to_delete), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }));
            }
        };
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