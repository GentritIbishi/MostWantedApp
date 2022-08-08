package fiek.unipr.mostwantedapp.maps.report;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.CustomizedGalleryAdapter;

public class ReportInfoActivity extends AppCompatActivity {

    private Bundle reportInfoBundle;
    private String notificationReportDateTime, notificationReportBody, notificationReportTitle, notificationReportType,
            notificationReportStatusChangedTo, notificationReportUID, notificationDateTimeChanged;

    private TextView report_info_title, report_info_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        report_info_title = findViewById(R.id.report_info_title);
        report_info_description = findViewById(R.id.report_info_description);

        reportInfoBundle = new Bundle();
        getFromBundle(reportInfoBundle);

        report_info_title.setText(getApplicationContext().getText(R.string.hello_dear)+" "+notificationReportUID);

        report_info_description.setText(getApplicationContext().getText(R.string.your_report_with)+" "+
                notificationReportDateTime+ " "+ getApplicationContext().getText(R.string.and_with_title)+" "+notificationReportTitle+" "+getApplicationContext().getText(R.string.has_new_status_right_now)+" "
        + getApplicationContext().getText(R.string.update_status_of_report_with)+" "+notificationReportDateTime+" "+getApplicationContext().getText(R.string.has_been_changed_to)+" "+notificationReportStatusChangedTo);
    }

    private void getFromBundle(Bundle bundle) {
        try {
            bundle = getIntent().getExtras();
            notificationReportDateTime = bundle.getString("notificationReportDateTime");
            notificationReportBody = bundle.getString("notificationReportBody");
            notificationReportTitle = bundle.getString("notificationReportTitle");
            notificationReportType = bundle.getString("notificationReportType");
            notificationReportStatusChangedTo = bundle.getString("notificationReportStatusChangedTo");
            notificationReportUID = bundle.getString("notificationReportUID");
            notificationDateTimeChanged = bundle.getString("notificationDateTimeChanged");

        } catch (Exception e) {
            e.getMessage();
        }
    }

}