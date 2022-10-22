package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.R;

public class InformationReportFragment extends Fragment {

    private Context mContext;
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
    private FirebaseUser firebaseUser;

    public InformationReportFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information_report, container, false);
        mContext = getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        report_info_title = view.findViewById(R.id.report_info_title);
        report_info_description = view.findViewById(R.id.report_info_description);

        Bundle reportInfoBundle = new Bundle();
        getFromBundle(reportInfoBundle);

        setReportTitleFromFirebase(firebaseAuth.getUid());

        report_info_description.setText(mContext.getText(R.string.report_with_id)+" \""+notificationReportId+"\" "+mContext.getText(R.string.with_date)+" "+notificationReportDateTime+" "
                + mContext.getText(R.string.has_new_status_right_now)+" "+mContext.getText(R.string.the_new_status_of_this_report_is)+" "+mContext.getText(R.string.changed_to)+" "+notificationReportNewStatus+".");


        //Raporti juaj me "id" ne daten ka status te ri, statusi i ri eshte ....

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
                        String name = documentSnapshot.getString("name");
                        report_info_title.setText(mContext.getText(R.string.hi)+" "+name);
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