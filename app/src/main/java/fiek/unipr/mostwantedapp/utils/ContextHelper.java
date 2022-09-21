package fiek.unipr.mostwantedapp.utils;

import android.content.Context;

public class ContextHelper {

    public static boolean checkContext(Context context) {
        if(context != null)
            return true;
        else
            return false;
    }

}
