package fiek.unipr.mostwantedapp.services;

import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_ADDED;
import static fiek.unipr.mostwantedapp.utils.Constants.IMPORTANCE;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_ADMIN;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_NUMBER_1;
import static fiek.unipr.mostwantedapp.utils.StringHelper.empty;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.fragment.admin.NotificationFragment;
import fiek.unipr.mostwantedapp.models.Notifications;

public class AdminSaveNotificationService extends Service {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private SharedPreferences sharedPreferences;
    private PendingIntent pendingIntent;
    private String notificationDateTime, notificationType, notificationReportId, notificationReportUid,
            notificationReportDateTime, notificationReportTitle, notificationReportDescription, notificationReportInformerPerson,
            notificationReportWantedPerson, notificationReportPrizeToWin, notificationReportNewStatus, notificationForUserId;
    private boolean allowNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        allowNotification = sharedPreferences.getBoolean("allowNotification", true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationDateTime = intent.getStringExtra("notificationDateTime");
        notificationType = intent.getStringExtra("notificationType");
        notificationReportId = intent.getStringExtra("notificationReportId");
        notificationReportUid = intent.getStringExtra("notificationReportUid");
        notificationReportDateTime = intent.getStringExtra("notificationReportDateTime");
        notificationReportTitle = intent.getStringExtra("notificationReportTitle");
        notificationReportDescription = intent.getStringExtra("notificationReportDescription");
        notificationReportInformerPerson = intent.getStringExtra("notificationReportInformerPerson");
        notificationReportWantedPerson = intent.getStringExtra("notificationReportWantedPerson");
        notificationReportPrizeToWin = intent.getStringExtra("notificationReportPrizeToWin");
        notificationReportNewStatus = intent.getStringExtra("notificationReportNewStatus");
        notificationForUserId = intent.getStringExtra("notificationForUserId");

        if (!empty(notificationDateTime) &&
                !empty(notificationType) &&
                !empty(notificationReportId) &&
                !empty(notificationReportUid) &&
                !empty(notificationReportDateTime) &&
                !empty(notificationReportTitle) &&
                !empty(notificationReportDescription) &&
                !empty(notificationReportInformerPerson) &&
                !empty(notificationReportWantedPerson) &&
                !empty(notificationReportPrizeToWin) &&
                !empty(notificationReportNewStatus) &&
                !empty(notificationForUserId))
        {
            Notifications objNotification = new Notifications(
                    notificationDateTime,
                    notificationType,
                    notificationReportId,
                    notificationReportUid,
                    notificationReportDateTime,
                    notificationReportTitle,
                    notificationReportDescription,
                    notificationReportInformerPerson,
                    notificationReportWantedPerson,
                    notificationReportPrizeToWin,
                    notificationReportNewStatus,
                    notificationForUserId);

            processNotification(objNotification, getApplicationContext());
        }

        return START_NOT_STICKY;
    }

    private void processNotification(Notifications objNotification, Context mContext) {

        Intent notificationIntentAdmin = new Intent(mContext, AdminDashboardActivity.class);
        pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntentAdmin, 0);

        check(mContext, objNotification, firebaseAuth.getUid(), CHANNEL_ID_ADDED, NOTIFICATION_NUMBER_1, 1);
    }

    private void createNotificationChannelAdded(String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ADMIN NEW REPORT ADDED NOTIFICATION";
            String description = "This channel is for admin, that send notification when new report added in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void check(Context mContext, Notifications objNotification, String userID, String CHANNEL_ID, String sharedPrefName, int default_number)
    {
        firebaseFirestore.collection(NOTIFICATION_ADMIN)
                .whereEqualTo("notificationReportId", objNotification.getNotificationReportId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            String notificationForUserId = queryDocumentSnapshots.getDocuments().get(i).getString("notificationForUserId");
                            String notificationType = queryDocumentSnapshots.getDocuments().get(i).getString("notificationType");

                            if (!empty(notificationForUserId) && !empty(notificationType)) {
                                if (notificationForUserId.equals(userID) && notificationType.equals(objNotification.getNotificationType())) {
                                    count++;
                                }
                            }
                        }

                        if (count == 0) {
                            createNotificationChannelAdded(CHANNEL_ID);
                            saveAndMakeNotification(
                                    mContext,
                                    objNotification,
                                    CHANNEL_ID,
                                    sharedPrefName,
                                    default_number);
                        }else {
                            //after do job stop self
                            stopSelf();
                        }

                    }
                });
    }

    private void saveAndMakeNotification(
            Context mContext, Notifications objNotification, String CHANNEL_ID, String sharedPrefName, int default_number)
    {
        CollectionReference collRef = firebaseFirestore.collection(NOTIFICATION_ADMIN);
        String notificationId = collRef.document().getId();

        objNotification.setNotificationId(notificationId);

        //save and make objNotification for added report
        firebaseFirestore.collection(NOTIFICATION_ADMIN)
                .document(notificationId)
                .set(objNotification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        try {

                            if (allowNotification)
                            {
                                Intent notificationIntent = new Intent(mContext, NotificationFragment.class);
                                pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

                                Uri new_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
                                notificationBuilder.setContentTitle(objNotification.getNotificationType() + ": " + objNotification.getNotificationReportTitle());
                                notificationBuilder.setContentText(objNotification.getNotificationReportDescription());
                                notificationBuilder.setSmallIcon(R.drawable.ic_app);
                                notificationBuilder.setPriority(IMPORTANCE);
                                notificationBuilder.setContentIntent(pendingIntent);
                                notificationBuilder.setSound(new_defaultSoundUri);
                                notificationBuilder.setAutoCancel(true);

                                int number = sharedPreferences.getInt(sharedPrefName, default_number);

                                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(number, notificationBuilder.build());

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                number++;
                                editor.putInt(sharedPrefName, number);
                                editor.apply();
                            }else {
                                //after do job stop self
                                stopSelf();
                            }

                        } catch (Exception e) {
                            Log.d("ERROR", e.getMessage());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR_SAVE", e.getMessage());
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
