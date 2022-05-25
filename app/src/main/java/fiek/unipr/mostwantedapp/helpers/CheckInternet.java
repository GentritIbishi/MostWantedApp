package fiek.unipr.mostwantedapp.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternet {

    //usuage
    // Check Internet Connection
//    CheckInternet checkInternet = new CheckInternet();
//    if(!checkInternet.isConnected(this)){
//        Toast.makeText(this, "You need to connect the internet!", Toast.LENGTH_SHORT).show();
//        return;
//    }

    public boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        } else {
            return false;
        }

    }

}
