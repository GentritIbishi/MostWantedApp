package fiek.unipr.mostwantedapp.utils;

import static fiek.unipr.mostwantedapp.utils.Constants.ADMIN_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_ADDED;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_MODIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.CHANNEL_ID_REMOVED;
import static fiek.unipr.mostwantedapp.utils.Constants.INFORMER_ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.ROLE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.USER_ROLE;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import fiek.unipr.mostwantedapp.activity.SplashActivity;
import fiek.unipr.mostwantedapp.activity.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.activity.dashboard.AnonymousDashboardActivity;
import fiek.unipr.mostwantedapp.activity.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.services.PayoutPaypalService;

public class ApplicationClass extends Application {

    private static final String ONESIGNAL_APP_ID = "b395b71d-5034-46cb-a691-4f56139461ac";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        createAdminNotificationChannelAdded();
        createAdminNotificationChannelModified();
        createAdminNotificationChannelRemoved();
        createUserNotificationChannelModified();

        Intent intent = new Intent(this, PayoutPaypalService.class);
        startService(intent);


    }

    private void check()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // user is signed in
            DocumentReference documentReference = firebaseFirestore.collection(USERS).document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String role = task.getResult().getString(ROLE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (role != null && role.matches(ADMIN_ROLE)) {
                            //aktivizo servicet qe jone
                        }
                    } else if (role != null && role.matches(USER_ROLE)) {

                    } else if (role != null && role.matches(INFORMER_ROLE)) {

                    } else {

                    }
                }

            });
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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

}
