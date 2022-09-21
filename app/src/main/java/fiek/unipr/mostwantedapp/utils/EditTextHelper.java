package fiek.unipr.mostwantedapp.utils;

import com.google.android.material.textfield.TextInputEditText;

public class EditTextHelper {

    public static void disableEditable(TextInputEditText textInputEditText) {
        textInputEditText.setFocusable(false);
        textInputEditText.setFocusableInTouchMode(false);
        textInputEditText.setClickable(false);
        textInputEditText.setKeyListener(null);
        textInputEditText.setCursorVisible(false);
        textInputEditText.setPressed(false);
    }

}
