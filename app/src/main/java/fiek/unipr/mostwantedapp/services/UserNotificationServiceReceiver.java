package fiek.unipr.mostwantedapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fiek.unipr.mostwantedapp.utils.ServiceManager;

public class UserNotificationServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceManager.startServiceUser(context);
    }
}
