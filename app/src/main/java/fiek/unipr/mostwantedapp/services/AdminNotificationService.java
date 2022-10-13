package fiek.unipr.mostwantedapp.services;

import static android.content.ContentValues.TAG;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.models.NotificationState;
import fiek.unipr.mostwantedapp.models.Notifications;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.ServiceManager;

public class AdminNotificationService extends Service {

    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ListenerRegistration registration;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        mContext = getApplicationContext();
        ServiceManager.startServiceAdmin(mContext);
        listener();
    }

    private void listener() {
        Query query = firebaseFirestore.collection(LOCATION_REPORTS);
        registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            String notificationReportId = dc.getDocument().getString("docId");
                            String notificationReportUid = dc.getDocument().getString("uID");
                            String notificationReportDateTime = dc.getDocument().getString("date_time");
                            String notificationReportTitle = dc.getDocument().getString("title");
                            String notificationReportDescription = dc.getDocument().getString("description");
                            String notificationReportInformerPerson = dc.getDocument().getString("informer_person");
                            String notificationReportWantedPerson = dc.getDocument().getString("wanted_person");
                            String notificationReportPrizeToWin = dc.getDocument().getString("prizeToWin");
                            String notificationReportNewStatus = dc.getDocument().getString("status");

                            Notifications notificationsAdded = new Notifications(
                                    DateHelper.getDateTime(),
                                    String.valueOf(NotificationState.ADDED),
                                    notificationReportId,
                                    notificationReportUid,
                                    notificationReportDateTime,
                                    notificationReportTitle,
                                    notificationReportDescription,
                                    notificationReportInformerPerson,
                                    notificationReportWantedPerson,
                                    notificationReportPrizeToWin,
                                    notificationReportNewStatus,
                                    firebaseAuth.getUid()
                            );

                            switch (dc.getType()) {
                                case ADDED:
                                    final Intent intentAdded = new Intent(AdminNotificationService.this, AdminSaveNotificationService.class);
                                    ServiceCaller(intentAdded, notificationsAdded);
                                    break;
                            }
                        }
                    }
                });
    }

    private void ServiceCaller(Intent intent, Notifications notifications) {
        stopService(intent);
        intent.putExtra("notificationDateTime", notifications.getNotificationDateTime());
        intent.putExtra("notificationType", notifications.getNotificationType());
        intent.putExtra("notificationReportId", notifications.getNotificationReportId());
        intent.putExtra("notificationReportUid", notifications.getNotificationReportUid());
        intent.putExtra("notificationReportDateTime", notifications.getNotificationReportDateTime());
        intent.putExtra("notificationReportTitle", notifications.getNotificationReportTitle());
        intent.putExtra("notificationReportDescription", notifications.getNotificationReportDescription());
        intent.putExtra("notificationReportInformerPerson", notifications.getNotificationReportInformerPerson());
        intent.putExtra("notificationReportWantedPerson", notifications.getNotificationReportWantedPerson());
        intent.putExtra("notificationReportPrizeToWin", notifications.getNotificationReportPrizeToWin());
        intent.putExtra("notificationReportNewStatus", notifications.getNotificationReportNewStatus());
        intent.putExtra("notificationForUserId", notifications.getNotificationForUserId());
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("receiver_admin");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
