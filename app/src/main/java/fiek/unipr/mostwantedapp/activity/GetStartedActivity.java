package fiek.unipr.mostwantedapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.OnSwipeTouchListener;

public class GetStartedActivity extends AppCompatActivity {

    private Context mContext;
    private TextView tvgetStarted1, dot1, dot2;
    private Button bt_getStarted;
    private RelativeLayout view_getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        mContext = getApplicationContext();
        tvgetStarted1 = findViewById(R.id.tvgetStarted1);
        view_getStarted = findViewById(R.id.view_getStarted);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        bt_getStarted = findViewById(R.id.bt_getStarted);

        //Check if application is opened for the first time
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String firstTime = preferences.getString("FirstTimeInstall","");

        if(firstTime.equals("Yes")){
            //If application was opened for the first time
            Intent intent = new Intent(GetStartedActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }else {
            //Else...
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("FirstTimeInstall", "Yes");
            editor.apply();
        }


        bt_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        view_getStarted.setOnTouchListener(new OnSwipeTouchListener(GetStartedActivity.this){
            public void onSwipeRight() {
                tvgetStarted1.setText(mContext.getText(R.string.get_started_description));
                dot1.setBackgroundResource(R.drawable.ic_dot_clicked);
                dot2.setBackgroundResource(R.drawable.ic_dot_unclicked);
            }
            public void onSwipeLeft() {
                tvgetStarted1.setText(mContext.getText(R.string.get_started_description2));
                dot2.setBackgroundResource(R.drawable.ic_dot_clicked);
                dot1.setBackgroundResource(R.drawable.ic_dot_unclicked);
            }
        });

    }

}