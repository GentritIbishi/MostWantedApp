package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;

public class WithdrawFragment extends Fragment {

    private View view;
    private PieChart availableEarningPieChart;
    private TextView tv_paidOut;
    private TextInputEditText etPaymentMethod, etPaypalEmail, etFullNamePaymentInformation, etAddressPaymentInformation, etBankNamePaymentInformation,
            etAccountNumberPaymentInformation, etDateNextPayment;
    private ConstraintLayout paypalConstraint, bankAccountConstraint;
    private String userID, full_name_payment_information, address_payment_information, bank_account_payment_information, account_number_payment_information,
            paypal_email_payment_information, payment_method;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    public WithdrawFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        initializeFields();
        getAndSetFromSharedPreference();
        setupPieChart();
        setPieChart();
        return view;
    }

    private void initializeFields() {
        availableEarningPieChart = view.findViewById(R.id.availableEarningPieChart);
        tv_paidOut = view.findViewById(R.id.tv_paidOut);
        etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        etPaypalEmail = view.findViewById(R.id.etPaypalEmail);
        etFullNamePaymentInformation = view.findViewById(R.id.etFullNamePaymentInformation);
        etAddressPaymentInformation = view.findViewById(R.id.etAddressPaymentInformation);
        etBankNamePaymentInformation = view.findViewById(R.id.etBankNamePaymentInformation);
        etBankNamePaymentInformation = view.findViewById(R.id.etBankNamePaymentInformation);
        etAccountNumberPaymentInformation = view.findViewById(R.id.etAccountNumberPaymentInformation);
        etDateNextPayment = view.findViewById(R.id.etDateNextPayment);
        paypalConstraint = view.findViewById(R.id.paypalConstraint);
        bankAccountConstraint = view.findViewById(R.id.bankAccountConstraint);
    }

    private void getAndSetFromSharedPreference() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        full_name_payment_information = sharedPreferences.getString("full_name_payment_information", "");
        address_payment_information = sharedPreferences.getString("address_payment_information", "");
        bank_account_payment_information = sharedPreferences.getString("bank_account_payment_information", "");
        account_number_payment_information = sharedPreferences.getString("account_number_payment_information", "");
        paypal_email_payment_information = sharedPreferences.getString("paypal_email_payment_information", "");
        payment_method = sharedPreferences.getString("payment_method", "");

        etFullNamePaymentInformation.setText(full_name_payment_information);
        etAddressPaymentInformation.setText(address_payment_information);
        etBankNamePaymentInformation.setText(bank_account_payment_information);
        etAccountNumberPaymentInformation.setText(account_number_payment_information);
        etPaypalEmail.setText(paypal_email_payment_information);
        etPaymentMethod.setText(payment_method);

        etDateNextPayment.setText(DateHelper.getNextMonthFirstDay());


        if(payment_method.equals("Paypal"))
        {
            paypalConstraint.setVisibility(View.VISIBLE);
            bankAccountConstraint.setVisibility(View.GONE);
        }else if(payment_method.equals("Bank Account"))
        {
            paypalConstraint.setVisibility(View.GONE);
            bankAccountConstraint.setVisibility(View.VISIBLE);
        }

    }

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if(CheckInternet.isConnected(getContext())) {
            firebaseFirestore.collection(USERS)
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String balance = "";

                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                                balance = doc.getString("balance");
                                loadPieChartData(balance);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPieChartData(String balance) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        String[] justBalance = balance.split(" ");
        balance = justBalance[0];
        tv_paidOut.setText(balance+"EURO");

        Float finalBalance = Float.valueOf(balance);

        try {
            entries.add(new PieEntry(finalBalance, EURO));
        }catch (Exception e){
            e.getMessage();
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for(int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for(int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(availableEarningPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        availableEarningPieChart.setData(data);
        availableEarningPieChart.invalidate();
        availableEarningPieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void setupPieChart() {
        availableEarningPieChart.setDrawHoleEnabled(true);
        availableEarningPieChart.setUsePercentValues(true);
        availableEarningPieChart.setEntryLabelTextSize(12);
        availableEarningPieChart.setEntryLabelColor(Color.BLACK);
        availableEarningPieChart.setCenterText(getText(R.string.all_available_earnings));
        availableEarningPieChart.setCenterTextSize(16f);
        availableEarningPieChart.getDescription().setEnabled(false);

        Legend l = availableEarningPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(14f);
        l.setFormSize(14f);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

}