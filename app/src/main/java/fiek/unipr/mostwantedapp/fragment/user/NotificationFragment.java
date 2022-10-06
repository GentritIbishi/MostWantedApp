package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_USER;
import static fiek.unipr.mostwantedapp.utils.Constants.SEEN;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import fiek.unipr.mostwantedapp.models.Notifications;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class NotificationFragment extends Fragment implements RecyclerViewInterface {

    private Context mContext;
    private View notification_fragment_view;
    private RecyclerView user_lvReportNotification;
    private LinearLayout notification_user_list_view1, notification_user_list_view2;
    private TextView tv_notification_user_userListEmpty;
    private ViewSwitcher notification_user_list_switcher;
    private ModifiedReportNotificationAdapter reportNotificationAdapter;
    private ArrayList<Notifications> reportArrayList;
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
        mContext = getContext();
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
        notification_user_list_view1 = notification_fragment_view.findViewById(R.id.notification_user_list_view1);
        notification_user_list_view2 = notification_fragment_view.findViewById(R.id.notification_user_list_view2);
        tv_notification_user_userListEmpty = notification_fragment_view.findViewById(R.id.tv_notification_user_userListEmpty);
        notification_user_list_switcher = notification_fragment_view.findViewById(R.id.notification_user_list_switcher);
        reportArrayList = new ArrayList<>();
        reportNotificationAdapter = new ModifiedReportNotificationAdapter(mContext, reportArrayList, this);
        user_lvReportNotification.setAdapter(reportNotificationAdapter);
        user_lvReportNotification.setLayoutManager(new LinearLayoutManager(mContext));
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
                            if(notification_user_list_switcher.getCurrentView() == notification_user_list_view2){
                                notification_user_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Notifications report = d.toObject(Notifications.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                reportArrayList.add(report);
                            }

                            reportNotificationAdapter.notifyDataSetChanged();
                        } else {
                            if(notification_user_list_switcher.getCurrentView() == notification_user_list_view1){
                                notification_user_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Error_"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error"+e.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        InformationReportFragment informationReportFragment = new InformationReportFragment();
        Bundle viewBundle = new Bundle();

        viewBundle.putString("notificationId", reportArrayList.get(position).getNotificationId());
        viewBundle.putString("notificationDateTime", reportArrayList.get(position).getNotificationDateTime());
        viewBundle.putString("notificationType", reportArrayList.get(position).getNotificationType());
        viewBundle.putString("notificationReportId", reportArrayList.get(position).getNotificationReportId());
        viewBundle.putString("notificationReportUid", reportArrayList.get(position).getNotificationReportUid());
        viewBundle.putString("notificationReportDateTime", reportArrayList.get(position).getNotificationReportDateTime());
        viewBundle.putString("notificationReportTitle", reportArrayList.get(position).getNotificationReportTitle());
        viewBundle.putString("notificationReportDescription", reportArrayList.get(position).getNotificationReportDescription());
        viewBundle.putString("notificationReportInformerPerson", reportArrayList.get(position).getNotificationReportInformerPerson());
        viewBundle.putString("notificationReportWantedPerson", reportArrayList.get(position).getNotificationReportWantedPerson());
        viewBundle.putString("notificationReportPrizeToWin", reportArrayList.get(position).getNotificationReportPrizeToWin());
        viewBundle.putString("notificationReportNewStatus", reportArrayList.get(position).getNotificationReportNewStatus());
        viewBundle.putString("notificationForUserId", reportArrayList.get(position).getNotificationForUserId());

        setSeenNotification(reportArrayList.get(position).getNotificationId());

        informationReportFragment.setArguments(viewBundle);
        loadFragment(informationReportFragment);
    }

    private void setSeenNotification(String notificationReportId) {
        Map<String, Object> data = new HashMap<>();
        data.put("notificationStatus", SEEN);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NOTIFICATION_USER)
                .document(notificationReportId)
                .update(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.user_fragmentContainer, fragment)
                .commit();
    }

}