package fiek.unipr.mostwantedapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fiek.unipr.mostwantedapp.utils.ServiceManager;

public class AdminNotificationServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceManager.startServiceAdmin(context);
    }
}
