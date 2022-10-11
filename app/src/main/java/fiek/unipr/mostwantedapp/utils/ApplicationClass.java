package fiek.unipr.mostwantedapp.utils;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.onesignal.OneSignal;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class ApplicationClass extends Application {

    private static final String ONESIGNAL_APP_ID = "b395b71d-5034-46cb-a691-4f56139461ac";

    @Override
    public void onCreate() {
        super.onCreate();
        setOnesignal();
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