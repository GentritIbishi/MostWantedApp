package fiek.unipr.mostwantedapp.maps.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.R;

public class ReportInfoActivity extends AppCompatActivity {

    private Bundle reportInfoBundle;
    private String notificationReportDateTime, notificationReportBody, notificationReportTitle, notificationReportType,
            notificationReportStatusChangedTo, notificationReportUID, notificationDateTimeChanged;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView report_info_title, report_info_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        report_info_title = findViewById(R.id.report_info_title);
        report_info_description = findViewById(R.id.report_info_description);

        reportInfoBundle = new Bundle();
        getFromBundle(reportInfoBundle);

        if(firebaseAuth.getCurrentUser().getPhoneNumber() == null || empty(firebaseAuth.getCurrentUser().getPhoneNumber())){
            setReportTitleFromFirebase(notificationReportUID);
        }else {
            report_info_title.setText(getApplicationContext().getText(R.string.hello_dear)+" "+firebaseAuth.getCurrentUser().getPhoneNumber());
        }

        report_info_description.setText(getApplicationContext().getText(R.string.your_report_in_datetime)+" "+
                notificationReportDateTime+ " "+ getApplicationContext().getText(R.string.and_with_title)+" "+notificationReportTitle+" "+getApplicationContext().getText(R.string.has_new_status_right_now)+" "
        + getApplicationContext().getText(R.string.update_status_of_report_with)+" "+notificationReportDateTime+" "+getApplicationContext().getText(R.string.has_been_changed_to)+" "+notificationReportStatusChangedTo);
    }

    private void setReportTitleFromFirebase(String notificationReportUID) {
        firebaseFirestore.collection("users")
                .document(notificationReportUID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String fullName = documentSnapshot.getString("fullName");
                        report_info_title.setText(getApplicationContext().getText(R.string.hello_dear)+" "+fullName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReportInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

}