package fiek.unipr.mostwantedapp.services;

import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_ADDED;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_MODIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_REMOVED;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_USER_MODIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.IMPORTANCE;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_ADMIN;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_NUMBER_1;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_NUMBER_2;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_NUMBER_3;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_NUMBER_4;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_USER;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.app.Notification;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.models.Notifications;
import fiek.unipr.mostwantedapp.models.NotificationState;

public class ServiceNotification extends Service {

    private String userID, notificationDateTime, notificationType, notificationReportId, notificationReportUid,
            notificationReportDateTime, notificationReportTitle, notificationReportDescription, notificationReportInformerPerson,
            notificationReportWantedPerson, notificationReportPrizeToWin, notificationReportNewStatus, notificationForUserId;
    private SharedPreferences sharedPreferences;
    private PendingIntent pendingIntent;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        userID = firebaseAuth.getUid();
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

        startMyOwnForeground();

        return START_STICKY;
    }

    private void startMyOwnForeground() {
            firebaseFirestore.collection(USERS)
                    .document(userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String role = documentSnapshot.getString("role");
                            try {
                                Intent notificationIntentAdmin = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                Intent notificationIntentUser = new Intent(getApplicationContext(), UserDashboardActivity.class);

                                if(role != null)
                                {
                                    if(role.equals("Admin"))
                                    {
                                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntentAdmin, 0);
                                        if(notificationType.equals(String.valueOf(NotificationState.ADDED)))
                                        {
                                            createAdminNotificationChannelAdded();
                                            makeNotificationAdmin(notificationDateTime,
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
                                                    notificationForUserId,
                                                    CHANNEL_ID_ADDED, NOTIFICATION_NUMBER_1, 1);
                                        }else if(notificationType.equals(String.valueOf(NotificationState.MODIFIED)))
                                        {
                                            createAdminNotificationChannelModified();
                                            makeNotificationAdmin(notificationDateTime,
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
                                                    notificationForUserId,
                                                    CHANNEL_ID_MODIFIED, NOTIFICATION_NUMBER_2, 2);
                                        }else if(notificationType.equals(String.valueOf(NotificationState.REMOVED)))
                                        {
                                            createAdminNotificationChannelRemoved();
                                            makeNotificationAdmin(notificationDateTime,
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
                                                    notificationForUserId,
                                                    CHANNEL_ID_REMOVED, NOTIFICATION_NUMBER_3, 3);
                                        }
                                    }else if(role.equals("Informer"))
                                    {
                                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntentUser, 0);
                                        createUserNotificationChannelModified();
                                        makeNotificationUser(notificationDateTime, notificationType,
                                                notificationReportId, notificationReportUid, notificationReportDateTime,
                                                notificationReportTitle, notificationReportDescription, notificationReportInformerPerson,
                                                notificationReportWantedPerson, notificationReportPrizeToWin, notificationReportNewStatus,
                                                userID,
                                                CHANNEL_ID_USER_MODIFIED, NOTIFICATION_NUMBER_4, 4);
                                    }
                                }else {
                                    Log.d("ROLE", "NULL");
                                }

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
    }

    private void makeNotificationAdmin(String notificationDateTime,
                                       String notificationType,
                                       String notificationReportId,
                                       String notificationReportUid,
                                       String notificationReportDateTime,
                                       String notificationReportTitle,
                                       String notificationReportDescription,
                                       String notificationReportInformerPerson,
                                       String notificationReportWantedPerson,
                                       String notificationReportPrizeToWin,
                                       String notificationReportNewStatus,
                                       String notificationForUserId,
                                       String CHANNEL_ID, String sharedPrefName, int default_number) {

        CollectionReference collRef = firebaseFirestore.collection(NOTIFICATION_ADMIN);
        String notificationId = collRef.document().getId();
        Notifications notifications = new Notifications(notificationId,
                notificationDateTime, notificationType, notificationReportId, notificationReportUid,
                notificationReportDateTime, notificationReportTitle, notificationReportDescription,
                notificationReportInformerPerson, notificationReportWantedPerson, notificationReportPrizeToWin,
                notificationReportNewStatus, notificationForUserId);
        // check if notifications for that user exist in database
        Log.d("FILLON", "FILLON");
        checkAdmin(getApplicationContext(), notificationId, notifications, userID, CHANNEL_ID, sharedPrefName, default_number);
        Log.d("MBARON", "MBARON");

    }

    private void saveAndMakeNotificationAdmin(
            Context context, String notificationId,
            Notifications notifications, String CHANNEL_ID, String sharedPrefName, int default_number) {
        //save and make notifications for added report
        firebaseFirestore.collection(NOTIFICATION_ADMIN)
                .document(notificationId)
                .set(notifications)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SAVED", "SUCCESS SAVED"+ notifications.getNotificationType());

                        try {
                            Intent notificationIntent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                            Uri new_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
                            notificationBuilder.setContentTitle(notificationType+": "+notificationReportTitle);
                            notificationBuilder.setContentText(notificationReportDescription);
                            notificationBuilder.setSmallIcon(R.drawable.ic_app);
                            notificationBuilder.setPriority(IMPORTANCE);
                            notificationBuilder.setContentIntent(pendingIntent);
                            notificationBuilder.setSound(new_defaultSoundUri);
                            notificationBuilder.setAutoCancel(true);

                            sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext());
                            int number = sharedPreferences.getInt(sharedPrefName, default_number);

                            startForeground(number, notificationBuilder.build());

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(number, notificationBuilder.build());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            number++;
                            editor.putInt(sharedPrefName, number);
                            editor.apply();

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR_SAVE", e.getMessage());
                    }
                });
    }


    private void checkAdmin(Context context, String notificationId,
                            Notifications notifications, String userID, String CHANNEL_ID, String sharedPrefName, int default_number) {
        firebaseFirestore.collection(NOTIFICATION_ADMIN)
                .whereEqualTo("notificationReportId", notifications.getNotificationReportId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++)
                        {
                            String notificationForUserId = queryDocumentSnapshots.getDocuments().get(i).getString("notificationForUserId");
                            String notificationType = queryDocumentSnapshots.getDocuments().get(i).getString("notificationType");

                            if(notificationForUserId.equals(userID) && notificationType.equals(notifications.getNotificationType()))
                            {
                                count++;
                            }
                        }
                        //qyty e dim sakt count
                        System.out.println("COUNT_VALUE "+count);
                        if(count==0)
                        {
                            saveAndMakeNotificationAdmin(
                                    context,
                                    notificationId,
                                    notifications,
                                    CHANNEL_ID,
                                    sharedPrefName,
                                    default_number);
                        }
                    }
                });
    }

    private void makeNotificationUser(String notificationDateTime,
                                      String notificationType,
                                      String notificationReportId,
                                      String notificationReportUid,
                                      String notificationReportDateTime,
                                      String notificationReportTitle,
                                      String notificationReportDescription,
                                      String notificationReportInformerPerson,
                                      String notificationReportWantedPerson,
                                      String notificationReportPrizeToWin,
                                      String notificationReportNewStatus,
                                      String notificationForUserId,
                                      String CHANNEL_ID, String sharedPrefName, int default_number) {

        CollectionReference collRef = firebaseFirestore.collection(NOTIFICATION_USER);
        String notificationId = collRef.document().getId();
        Notifications notifications = new Notifications(notificationId,
                notificationDateTime, notificationType, notificationReportId, notificationReportUid,
                notificationReportDateTime, notificationReportTitle, notificationReportDescription,
                notificationReportInformerPerson, notificationReportWantedPerson, notificationReportPrizeToWin,
                notificationReportNewStatus, notificationForUserId);
        // check if notifications for that user exist in database
        Log.d("FILLON", "FILLON");
        checkUser(getApplicationContext(), notificationId, notifications, firebaseAuth.getUid(), CHANNEL_ID, sharedPrefName, default_number);
        Log.d("MBARON", "MBARON");

    }

    private void saveAndMakeNotificationUser(
            Context context, String notificationId,
            Notifications notifications, String CHANNEL_ID, String sharedPrefName, int default_number) {
        //save and make notifications for added report
        firebaseFirestore.collection(NOTIFICATION_USER)
                .document(notificationId)
                .set(notifications)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SAVED", "SUCCESS SAVED"+ notifications.getNotificationType());

                        try {
                            Intent notificationIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                            Uri new_defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
                            notificationBuilder.setContentTitle(notificationType+": "+notificationReportTitle);
                            notificationBuilder.setContentText(notificationReportDescription);
                            notificationBuilder.setSmallIcon(R.drawable.ic_app);
                            notificationBuilder.setPriority(IMPORTANCE);
                            notificationBuilder.setContentIntent(pendingIntent);
                            notificationBuilder.setSound(new_defaultSoundUri);
                            notificationBuilder.setAutoCancel(true);

                            sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext());
                            int number = sharedPreferences.getInt(sharedPrefName, default_number);

                            startForeground(number, notificationBuilder.build());

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(number, notificationBuilder.build());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            number++;
                            editor.putInt(sharedPrefName, number);
                            editor.apply();

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR_SAVE", e.getMessage());
                    }
                });
    }


    private void checkUser(Context context, String notificationId,
                           Notifications notifications, String userID, String CHANNEL_ID, String sharedPrefName, int default_number) {
        firebaseFirestore.collection(NOTIFICATION_USER)
                .whereEqualTo("notificationReportId", notifications.getNotificationReportId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++)
                        {
                            String notificationForUserId = queryDocumentSnapshots.getDocuments().get(i).getString("notificationForUserId");
                            String notificationType = queryDocumentSnapshots.getDocuments().get(i).getString("notificationType");

                            if(notificationForUserId.equals(userID) && notificationType.equals(notifications.getNotificationType()))
                            {
                                count++;
                            }
                        }
                        if(count==0)
                        {
                            saveAndMakeNotificationUser(
                                    context,
                                    notificationId,
                                    notifications,
                                    CHANNEL_ID,
                                    sharedPrefName,
                                    default_number);
                        }
                    }
                });
    }

    private void createAdminNotificationChannelAdded() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ADMIN NEW REPORT ADDED NOTIFICATION";
            String description = "This channel is for admin, that send notification when new report added in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_ADDED, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createAdminNotificationChannelModified() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ADMIN REPORT MODIFIED NOTIFICATION";
            String description = "This channel is for admin, that send notification when one report modified in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MODIFIED, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createAdminNotificationChannelRemoved() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ADMIN REPORT REMOVED NOTIFICATION";
            String description = "This channel is for admin, that send notification when one report removed from database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REMOVED, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createUserNotificationChannelModified() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "NEW_REPORT_MODIFIED";
            CharSequence name = "USER REPORT MODIFIED NOTIFICATION";
            String description = "This channel is for user, that send notification when one report state modified in database!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
