package fiek.unipr.mostwantedapp.fragment.user;

import static android.view.View.GONE;
import static fiek.unipr.mostwantedapp.utils.Constants.BANK_ACCOUNT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYMENT_INFORMATION;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Invoice;
import fiek.unipr.mostwantedapp.models.InvoiceState;
import fiek.unipr.mostwantedapp.models.PaymentInformation;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class WithdrawFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;
    private View view;
    private TableLayout invoiceTableLayout;
    private PieChart availableEarningPieChart;
    private TextView tv_balance_value;
    private ImageView
            row1Done, row1Error,
            row2Done, row2Error,
            row3Done, row3Error,
            row4Done, row4Error,
            row5Done, row5Error,
            row6Done, row6Error;
    private TextView labelCheckingWithdraw, tv_date_time, tv_transaction_id, tv_account, tv_amount, tv_status;
    private TableRow tableRowPaypal, tableRowBankAccount, tableRowTableLabel;
    private Button btnMakeRequestForPayment;
    private TextInputEditText etPaymentMethod, etPaypalEmail, etFullNamePaymentInformation, etAddressPaymentInformation, etBankNamePaymentInformation,
            etAccountNumberPaymentInformation;
    private ConstraintLayout paypalConstraint, bankAccountConstraint;
    private String userID, full_name_payment_information, address_payment_information, bank_account_payment_information, account_number_payment_information,
            paypal_email_payment_information, payment_method;

    private FirebaseAuth firebaseAuth;
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
        mContext = getContext();

        initializeFields();
        checking();

        btnMakeRequestForPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequestForPayment();
            }
        });

        final SwipeRefreshLayout swipeToRefreshWithdrawal = view.findViewById(R.id.swipeToRefreshWithdrawal);
        swipeToRefreshWithdrawal.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checking();
                swipeToRefreshWithdrawal.setRefreshing(false);
            }
        });

        return view;
    }

    private void makeRequestForPayment() {
        if (row1Done.getVisibility() == View.VISIBLE) {
            if (row2Done.getVisibility() == View.VISIBLE) {
                if (row3Done.getVisibility() == View.VISIBLE) {
                    if (row4Done.getVisibility() == View.VISIBLE) {
                        if (row5Done.getVisibility() == View.VISIBLE || row6Done.getVisibility() == View.VISIBLE) {
                            createInvoice();
                        } else {
                            Toast.makeText(mContext, mContext.getText(R.string.error_cash_out_limit_not_reached), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getText(R.string.error_this_account_is_changed_in_24_hours_wait_for_couple_hours_to_make_request_for_payment), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getText(R.string.error_is_no_longer_than_24_hours), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, mContext.getText(R.string.error_this_account_made_a_payment_this_month), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, mContext.getText(R.string.error_withdrawal_account_nots_set), Toast.LENGTH_SHORT).show();
        }
    }

    private void checking() {
        getAndSetFromSharedPreference();
        setupPieChart();
        setPieChart();
        checkWithdrawalAccountSet();
        checkIfUserMakePaymentThisMonth();
        checkIfAccountIsLongerThanOneMonth();
        checkIfUserIsChangedIn24Hours();
        checkCashOutLimit();
        addToTable();

        labelCheckingWithdraw.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndEnableDisableButton();
                labelCheckingWithdraw.setVisibility(GONE);
            }
        }, 3000);
    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    private void addToTable() {
        firebaseFirestore.collection(INVOICE)
                .whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0)
                        {
                            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.open_sans_bold);

                            tableRowTableLabel = new TableRow(mContext);
                            tv_date_time = new TextView(mContext);
                            tv_date_time.setText(mContext.getText(R.string.column_created_date_time)+" ");
                            tv_date_time.setTextColor(Color.WHITE);
                            tv_date_time.setTypeface(typeface);
                            tv_date_time.setBackground(mContext.getResources().getDrawable(R.drawable.bg_header_table));
                            tableRowTableLabel.addView(tv_date_time);

                            tv_transaction_id = new TextView(mContext);
                            tv_transaction_id.setText(mContext.getText(R.string.column_transaction_id)+" ");
                            tv_transaction_id.setTextColor(Color.WHITE);
                            tv_transaction_id.setTypeface(typeface);
                            tv_transaction_id.setBackground(mContext.getResources().getDrawable(R.drawable.bg_header_table));
                            tableRowTableLabel.addView(tv_transaction_id);

                            tv_account = new TextView(mContext);
                            tv_account.setText(mContext.getText(R.string.column_name_account)+" ");
                            tv_account.setTextColor(Color.WHITE);
                            tv_account.setTypeface(typeface);
                            tv_account.setBackground(mContext.getResources().getDrawable(R.drawable.bg_header_table));
                            tableRowTableLabel.addView(tv_account);

                            tv_amount = new TextView(mContext);
                            tv_amount.setText(mContext.getText(R.string.column_name_amount)+" ");
                            tv_amount.setTextColor(Color.WHITE);
                            tv_amount.setTypeface(typeface);
                            tv_amount.setBackground(mContext.getResources().getDrawable(R.drawable.bg_header_table));
                            tableRowTableLabel.addView(tv_amount);

                            tv_status = new TextView(mContext);
                            tv_status.setText(mContext.getText(R.string.column_name_status)+" ");
                            tv_status.setBackground(mContext.getResources().getDrawable(R.drawable.bg_header_table));
                            tv_status.setTextColor(Color.WHITE);
                            tv_status.setTypeface(typeface);
                            tableRowTableLabel.addView(tv_status);

                            invoiceTableLayout.addView(tableRowTableLabel);

                            for(int i = 0; i < queryDocumentSnapshots.size(); i++)
                            {
                                cleanTable(invoiceTableLayout);
                                String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                String transactionID = queryDocumentSnapshots.getDocuments().get(i).getString("transactionID");
                                String account = queryDocumentSnapshots.getDocuments().get(i).getString("account");
                                Double amount = queryDocumentSnapshots.getDocuments().get(i).getDouble("amount");
                                String status = queryDocumentSnapshots.getDocuments().get(i).getString("status");

                                TableRow tvRow = new TableRow(mContext);

                                TextView t0v = new TextView(mContext);
                                t0v.setText(created_date_time);
                                t0v.setBackground(mContext.getResources().getDrawable(R.drawable.bg_row_table));
                                t0v.setTextColor(Color.GRAY);
                                t0v.setGravity(Gravity.CENTER);
                                t0v.setTypeface(typeface);
                                tvRow.addView(t0v);

                                TextView t1v = new TextView(mContext);
                                t1v.setText(transactionID);
                                t1v.setTextColor(Color.GRAY);
                                t1v.setTypeface(typeface);
                                t1v.setBackground(mContext.getResources().getDrawable(R.drawable.bg_row_table));
                                t1v.setGravity(Gravity.CENTER);
                                tvRow.addView(t1v);

                                TextView t2v = new TextView(mContext);
                                t2v.setText(account);
                                t2v.setTypeface(typeface);
                                t2v.setBackground(mContext.getResources().getDrawable(R.drawable.bg_row_table));
                                t2v.setTextColor(Color.GRAY);
                                t2v.setGravity(Gravity.CENTER);
                                tvRow.addView(t2v);

                                TextView t3v = new TextView(mContext);
                                t3v.setText(String.valueOf(amount));
                                t3v.setTextColor(Color.GRAY);
                                t3v.setTypeface(typeface);
                                t3v.setBackground(mContext.getResources().getDrawable(R.drawable.bg_row_table));
                                t3v.setGravity(Gravity.CENTER);
                                tvRow.addView(t3v);

                                TextView t4v = new TextView(mContext);
                                t4v.setText(status);
                                t4v.setTypeface(typeface);
                                t4v.setBackground(mContext.getResources().getDrawable(R.drawable.bg_row_table));
                                t4v.setTextColor(Color.GRAY);
                                t4v.setGravity(Gravity.CENTER);
                                tvRow.addView(t4v);

                                invoiceTableLayout.addView(tvRow);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkAndEnableDisableButton() {
        if (row1Done.getVisibility() == View.VISIBLE)
        {
            if (row2Done.getVisibility() == View.VISIBLE)
            {
                if (row3Done.getVisibility() == View.VISIBLE)
                {
                    if (row4Done.getVisibility() == View.VISIBLE)
                    {
                        if (row5Done.getVisibility() == View.VISIBLE || row6Done.getVisibility() == View.VISIBLE)
                        {
                            btnMakeRequestForPayment.setEnabled(true);
                        }
                    }
                }
            }
        }
    }

    private void checkWithdrawalAccountSet() {
        if (!StringHelper.empty(payment_method) || !StringHelper.empty(full_name_payment_information) ||
                !StringHelper.empty(address_payment_information) || !StringHelper.empty(bank_account_payment_information) || !StringHelper.empty(account_number_payment_information)) {
            row1Done.setVisibility(View.VISIBLE);
        } else {
            row1Error.setVisibility(View.VISIBLE);
        }

        if (!StringHelper.empty(paypal_email_payment_information)) {
            row1Done.setVisibility(View.VISIBLE);
        } else {
            row1Error.setVisibility(View.VISIBLE);
        }
    }

    private void checkIfUserMakePaymentThisMonth() {
        firebaseFirestore.collection(INVOICE)
                .whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size() != 0)
                            {
                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                    //duhet me kqyre se kjo date a i perkete ketij muaji
                                    if(!StringHelper.empty(created_date_time))
                                    {
                                        String[] arrayDateTime = created_date_time.split(" ");
                                        String date = arrayDateTime[0];
                                        try {
                                            if (DateHelper.isDateInCurrentMonth(date)) {
                                                row2Error.setVisibility(View.VISIBLE);
                                            } else {
                                                row2Done.setVisibility(View.VISIBLE);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        row2Done.setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{
                                row2Done.setVisibility(View.VISIBLE);
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EXCEPTION", e.getMessage());
                    }
                });
    }

    private void checkIfAccountIsLongerThanOneMonth() {
        firebaseFirestore.collection(USERS)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String register_date_time = documentSnapshot.getString("register_date_time");
                        if(!StringHelper.empty(register_date_time))
                        {
                            String[] arrayRegisterDateTime = register_date_time.split(" ");

                            String register_date = arrayRegisterDateTime[0];

                            String[] yyyyMMdd = register_date.split("-");
                            int dd = Integer.parseInt(yyyyMMdd[0]);
                            int mm = Integer.parseInt(yyyyMMdd[1]);
                            int yyyy = Integer.parseInt(yyyyMMdd[2]);
                            if (DateHelper.isAccountOlder(30, yyyy, mm, dd)) {
                                row3Done.setVisibility(View.VISIBLE);
                            } else {
                                row3Error.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorCheckCreateInvoice", e.getMessage());
                    }
                });
    }

    private void checkIfUserIsChangedIn24Hours() {
        firebaseFirestore.collection(PAYMENT_INFORMATION)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                        String last_time_update = documentSnapshot.getString("last_time_update");
                            if(!StringHelper.empty(last_time_update))
                            {
                                Date givenDate = new SimpleDateFormat(DATE).parse(last_time_update);
                                if (DateHelper.checkDayBefore24(givenDate)) {
                                    row4Done.setVisibility(View.VISIBLE);
                                } else {
                                    row4Error.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkCashOutLimit() {
        firebaseFirestore.collection(USERS)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double balance = documentSnapshot.getDouble("balance");
                        if(balance != null)
                        {
                            if (balance > 5 && payment_method.equals(PAYPAL)) {
                                row5Done.setVisibility(View.VISIBLE);
                            }

                            if (balance > 100 && payment_method.equals(BANK_ACCOUNT)) {
                                row6Done.setVisibility(View.VISIBLE);
                            }

                            if (payment_method.equals("")) {
                                row5Error.setVisibility(View.VISIBLE);
                                row6Error.setVisibility(View.VISIBLE);
                            }

                            if(balance < 5)
                            {
                                row5Error.setVisibility(View.VISIBLE);
                                row6Error.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorCheckCashOutLimit", e.getMessage());
                    }
                });
    }

    private void initializeFields() {
        availableEarningPieChart = view.findViewById(R.id.availableEarningPieChart);
        tv_balance_value = view.findViewById(R.id.tv_balance_value);
        etPaymentMethod = view.findViewById(R.id.etPaymentMethod);
        etPaypalEmail = view.findViewById(R.id.etPaypalEmail);
        etFullNamePaymentInformation = view.findViewById(R.id.etFullNamePaymentInformation);
        etAddressPaymentInformation = view.findViewById(R.id.etAddressPaymentInformation);
        etBankNamePaymentInformation = view.findViewById(R.id.etBankNamePaymentInformation);
        etBankNamePaymentInformation = view.findViewById(R.id.etBankNamePaymentInformation);
        etAccountNumberPaymentInformation = view.findViewById(R.id.etAccountNumberPaymentInformation);
        paypalConstraint = view.findViewById(R.id.paypalConstraint);
        bankAccountConstraint = view.findViewById(R.id.bankAccountConstraint);
        btnMakeRequestForPayment = view.findViewById(R.id.btnMakeRequestForPayment);
        row1Done = view.findViewById(R.id.row1Done);
        row1Error = view.findViewById(R.id.row1Error);
        row2Done = view.findViewById(R.id.row2Done);
        row2Error = view.findViewById(R.id.row2Error);
        row3Done = view.findViewById(R.id.row3Done);
        row3Error = view.findViewById(R.id.row3Error);
        row4Done = view.findViewById(R.id.row4Done);
        row4Error = view.findViewById(R.id.row4Error);
        row5Done = view.findViewById(R.id.row5Done);
        row5Error = view.findViewById(R.id.row5Error);
        row6Done = view.findViewById(R.id.row6Done);
        row6Error = view.findViewById(R.id.row6Error);
        tableRowPaypal = view.findViewById(R.id.tableRowPaypal);
        tableRowBankAccount = view.findViewById(R.id.tableRowBankAccount);
        labelCheckingWithdraw = view.findViewById(R.id.labelCheckingWithdraw);
        invoiceTableLayout = view.findViewById(R.id.invoiceTableLayout);
    }

    private void getAndSetFromSharedPreference() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
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

        checkPaymentMethod();
    }

    private void checkPaymentMethod() {
        if (payment_method.equals(PAYPAL)) {
            paypalConstraint.setVisibility(View.VISIBLE);
            bankAccountConstraint.setVisibility(GONE);
            tableRowPaypal.setVisibility(View.VISIBLE);
        } else if (payment_method.equals(BANK_ACCOUNT)) {
            paypalConstraint.setVisibility(GONE);
            bankAccountConstraint.setVisibility(View.VISIBLE);
            tableRowBankAccount.setVisibility(View.VISIBLE);
        }
    }

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if (CheckInternet.isConnected(mContext)) {
            firebaseFirestore.collection(USERS)
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Double balance;

                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                                balance = doc.getDouble("balance");
                                loadPieChartData(balance);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(mContext, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadPieChartData(Double balance) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        tv_balance_value.setText(mContext.getText(R.string.balance) + ": " + balance+" "+EURO);

        try {
            entries.add(new PieEntry(Float.parseFloat(String.valueOf(balance)), String.valueOf(mContext.getText(R.string.your_money_in_balance))));
            entries.add(new PieEntry(Float.parseFloat(String.valueOf(50000)), String.valueOf(mContext.getText(R.string.maximum))));
        } catch (Exception e) {
            e.getMessage();
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(getResources().getColor(R.color.gray_analytics),
                getResources().getColor(R.color.graph_color_1));

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(availableEarningPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        availableEarningPieChart.setData(data);
        availableEarningPieChart.invalidate();
        availableEarningPieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void setupPieChart() {
        availableEarningPieChart.setDrawHoleEnabled(true);
        availableEarningPieChart.setUsePercentValues(true);
        availableEarningPieChart.setEntryLabelTextSize(12);
        availableEarningPieChart.setEntryLabelColor(Color.WHITE);
        availableEarningPieChart.setCenterText(getText(R.string.all_available_earnings));
        availableEarningPieChart.setCenterTextSize(16f);
        availableEarningPieChart.getDescription().setEnabled(false);

        Legend l = availableEarningPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextColor(Color.GRAY);
        l.setTextSize(14f);
        l.setFormSize(14f);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        full_name_payment_information = sharedPreferences.getString("full_name_payment_information", "");
        address_payment_information = sharedPreferences.getString("address_payment_information", "");
        bank_account_payment_information = sharedPreferences.getString("bank_account_payment_information", "");
        account_number_payment_information = sharedPreferences.getString("account_number_payment_information", "");
        paypal_email_payment_information = sharedPreferences.getString("paypal_email_payment_information", "");
        payment_method = sharedPreferences.getString("payment_method", "");

        save(full_name_payment_information, address_payment_information, bank_account_payment_information,
                account_number_payment_information, paypal_email_payment_information, payment_method);
    }

    private void save(String full_name_payment_information, String address_payment_information, String bank_account_payment_information,
                      String account_number_payment_information, String paypal_email_payment_information, String payment_method) {
        PaymentInformation paymentInformation = new PaymentInformation(
                userID,
                full_name_payment_information,
                address_payment_information,
                bank_account_payment_information,
                account_number_payment_information,
                paypal_email_payment_information,
                payment_method,
                DateHelper.getDateTime()
        );
        firebaseFirestore.collection(PAYMENT_INFORMATION)
                .document(userID)
                .set(paymentInformation, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createInvoice() {
        firebaseFirestore.collection(USERS)
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double balance = documentSnapshot.getDouble("balance");
                        processWithInvoice(balance);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void processWithInvoice(Double balance) {//go create invoice
        CollectionReference collRef = firebaseFirestore.collection(INVOICE);
        String transactionID = collRef.document().getId();

        Invoice invoice = new Invoice(
                DateHelper.getDateTime(),
                transactionID,
                firebaseAuth.getUid(),
                payment_method,
                full_name_payment_information,
                address_payment_information,
                bank_account_payment_information,
                account_number_payment_information,
                paypal_email_payment_information,
                InvoiceState.PENDING.toString(),
                DateHelper.getDateTime(),
                balance
        );

        firebaseFirestore.collection(INVOICE)
                .document(transactionID)
                .set(invoice)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext, mContext.getText(R.string.you_have_successfully_made_the_request_wait_for_response), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorNotSet", e.getMessage());
                    }
                });
    }

}