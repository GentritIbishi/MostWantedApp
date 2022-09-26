package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class ReportInfoFragment extends Fragment {

    private Context context;
    String  notificationId,
            notificationDateTime,
            notificationType,
            notificationReportId,
            notificationReportUid,
            notificationReportDateTime,
            notificationReportTitle,
            notificationReportDescription,
            notificationReportInformerPerson,
            notificationReportWantedPerson,
            notificationReportPrizeToWin,
            notificationReportNewStatus,
            notificationForUserId;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView report_info_title, report_info_description;

    public ReportInfoFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_info, container, false);
        context = getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        report_info_title = view.findViewById(R.id.report_info_title);
        report_info_description = view.findViewById(R.id.report_info_description);

        Bundle reportInfoBundle = new Bundle();
        getFromBundle(reportInfoBundle);

        if(StringHelper.empty(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber())){
            setReportTitleFromFirebase(notificationForUserId);
        }else {
            report_info_title.setText(context.getText(R.string.hello_dear)+" "+firebaseAuth.getCurrentUser().getPhoneNumber());
        }

        report_info_description.setText(context.getText(R.string.your_report_in_datetime)+" "+
                notificationReportDateTime+ " "+ context.getText(R.string.and_with_title)+" "+notificationReportTitle+" "+getContext().getText(R.string.has_new_status_right_now)+" "
                + getContext().getText(R.string.update_status_of_report_with)+" "+notificationReportDateTime+" "+getContext().getText(R.string.has_been_changed_to)+" "+notificationReportNewStatus);

        return view;
    }

    private void setReportTitleFromFirebase(String notificationReportUID) {
        firebaseFirestore.collection(USERS)
                .document(notificationReportUID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String fullName = documentSnapshot.getString("fullName");
                        report_info_title.setText(context.getText(R.string.hello_dear)+" "+fullName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFromBundle(Bundle bundle) {
        try {
            bundle = getArguments();
            if(bundle != null)
            {
                notificationId = bundle.getString("notificationId");
                notificationDateTime = bundle.getString("notificationDateTime");
                notificationType = bundle.getString("notificationType");
                notificationReportId = bundle.getString("notificationReportId");
                notificationReportUid = bundle.getString("notificationReportUid");
                notificationReportDateTime = bundle.getString("notificationReportDateTime");
                notificationReportTitle = bundle.getString("notificationReportTitle");
                notificationReportDescription = bundle.getString("notificationReportDescription");
                notificationReportInformerPerson = bundle.getString("notificationReportInformerPerson");
                notificationReportWantedPerson = bundle.getString("notificationReportWantedPerson");
                notificationReportPrizeToWin = bundle.getString("notificationReportPrizeToWin");
                notificationReportNewStatus = bundle.getString("notificationReportNewStatus");
                notificationForUserId = bundle.getString("notificationForUserId");
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

}