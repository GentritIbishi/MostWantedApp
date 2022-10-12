package fiek.unipr.mostwantedapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import fiek.unipr.mostwantedapp.R;

public class UIMessage {

    public static void showMessage(Context context, CharSequence title, CharSequence message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // on pressing cancel button
        alertDialog.setNegativeButton(context.getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

}
