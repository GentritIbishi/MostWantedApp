package fiek.unipr.mostwantedapp.utils;

import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUT_CONFIG;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class ApplicationClass extends Application {

    private static final String ONESIGNAL_APP_ID = "b395b71d-5034-46cb-a691-4f56139461ac";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setOnesignal();
        checkIfUserSetAutomaticPayment();
    }

    private void checkIfUserSetAutomaticPayment() {
        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String id = documentSnapshot.getString("id");
                        String datetime_updated = documentSnapshot.getString("datetime_updated");
                        Boolean payment_state = documentSnapshot.getBoolean("payment_state");
                        String hour = String.valueOf(documentSnapshot.get("hour"));
                        String minute = String.valueOf(documentSnapshot.get("minute"));
                        String second = String.valueOf(documentSnapshot.get("second"));
                        String millisecond = String.valueOf(documentSnapshot.get("millisecond"));

                        //kina me kqyre nese jon set dhe nese jon set check nese jon zero nese jon zero i bjen false

                        if (Boolean.TRUE.equals(payment_state))
                        {
                            //automatic set is enable
                            if (!hour.equals("0")) {
                                //boje set timer task
                                ScheduleTaskTimer.setPayoutPaypalTask(
                                        Integer.parseInt(hour),
                                        Integer.parseInt(minute),
                                        Integer.parseInt(second),
                                        Integer.parseInt(millisecond)
                                );
                            }
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void setOnesignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }

}