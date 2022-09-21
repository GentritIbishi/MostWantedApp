package fiek.unipr.mostwantedapp.utils;

import android.view.Window;
import android.view.WindowManager;

public class WindowHelper {
    public static void setFullScreenActivity(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
