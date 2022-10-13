package fiek.unipr.mostwantedapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import fiek.unipr.mostwantedapp.services.AdminNotificationService;
import fiek.unipr.mostwantedapp.services.UserNotificationService;

public class ServiceManager {

    public static void startServiceAdmin(Context context) {
        Intent serviceIntent = new Intent(context,AdminNotificationService.class);
        context.startService(serviceIntent);
    }

    public static void stopServiceAdmin(Context context) {
        Intent serviceIntent = new Intent(context,AdminNotificationService.class);
        context.stopService(serviceIntent);
    }

    public static void startServiceUser(Context context) {
        Intent serviceIntent = new Intent(context,UserNotificationService.class);
        context.startService(serviceIntent);
    }

    public static void stopServiceUser(Context context) {
        Intent serviceIntent = new Intent(context,UserNotificationService.class);
        context.stopService(serviceIntent);
    }
}
