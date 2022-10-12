package fiek.unipr.mostwantedapp.fragment.admin;

import static android.view.View.GONE;
import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUT_CONFIG;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL_SANDBOX_KEY_BEARER;
import static fiek.unipr.mostwantedapp.utils.Constants.PENDING;
import static fiek.unipr.mostwantedapp.utils.Constants.REFUSED;
import static fiek.unipr.mostwantedapp.utils.Constants.TIME_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.USD;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.PayoutConfig;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.PayoutsPaypalTask;
import fiek.unipr.mostwantedapp.utils.StringHelper;
import fiek.unipr.mostwantedapp.utils.UIMessage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayoutsFragment extends Fragment {

    private Integer[] MINUTE_ARRAY, MILLISECOND_ARRAY, HOUR_ARRAY;
    public static int responseCode = 0;
    private int progress;
    private Context mContext;
    private View view;
    private Switch switchPayment;
    private TextView tvInvoiceReport, tv_invoiceProgressBar;
    private ConstraintLayout constrainProcessPayment, constraintAutomaticPayment, constrainInvoiceProgress;
    private MaterialAutoCompleteTextView auto_complete_hour_payment, auto_complete_minute_payment,
            auto_complete_second_payment, auto_complete_millisecond_payment;
    private Button btnSaveReport, btnProcessPayout;
    private ProgressBar invoiceProgressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;


    public PayoutsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payouts, container, false);
        mContext = getContext();

        setHour();
        setMinuteAndSecond();
        setMilliseconds();

        switchPayment = view.findViewById(R.id.switchPayment);
        tvInvoiceReport = view.findViewById(R.id.tvInvoiceReport);
        constrainProcessPayment = view.findViewById(R.id.constrainProcessPayment);
        constraintAutomaticPayment = view.findViewById(R.id.constraintAutomaticPayment);
        auto_complete_hour_payment = view.findViewById(R.id.auto_complete_hour_payment);
        auto_complete_minute_payment = view.findViewById(R.id.auto_complete_minute_payment);
        auto_complete_second_payment = view.findViewById(R.id.auto_complete_second_payment);
        auto_complete_millisecond_payment = view.findViewById(R.id.auto_complete_millisecond_payment);
        btnSaveReport = view.findViewById(R.id.btnSaveReport);
        btnProcessPayout = view.findViewById(R.id.btnProcessPayout);
        invoiceProgressBar = view.findViewById(R.id.invoiceProgressBar);
        tv_invoiceProgressBar = view.findViewById(R.id.tv_invoiceProgressBar);
        constrainInvoiceProgress = view.findViewById(R.id.constrainInvoiceProgress);

        auto_complete_hour_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<Integer> hour_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, HOUR_ARRAY);
                auto_complete_hour_payment.setAdapter(hour_adapter);
            }
        });

        auto_complete_minute_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<Integer> minute_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MINUTE_ARRAY);
                auto_complete_minute_payment.setAdapter(minute_adapter);
            }
        });

        auto_complete_second_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<Integer> second_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MINUTE_ARRAY);
                auto_complete_second_payment.setAdapter(second_adapter);
            }
        });

        auto_complete_millisecond_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<Integer> millisecond_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MILLISECOND_ARRAY);
                auto_complete_millisecond_payment.setAdapter(millisecond_adapter);
            }
        });

        getFromDatabaseAndCheck();

        analyticsInvoice();

        switchPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    switchPaymentCheck(true);
                }else {
                    switchPaymentCheck(false);
                }
            }
        });

        btnSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        btnProcessPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProcess();
            }
        });

        return view;
    }

    private void checkProcess() {
        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num_of_reports_today = task.getResult().size();
                            if(num_of_reports_today>0)
                            {
                                try {
                                    process(USD);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                UIMessage.showMessage(mContext, mContext.getText(R.string.no_invoice),
                                        mContext.getText(R.string.you_must_have_at_least_one_invoice));
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        ArrayAdapter<Integer> hour_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, HOUR_ARRAY);
        auto_complete_hour_payment.setAdapter(hour_adapter);

        ArrayAdapter<Integer> minute_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MINUTE_ARRAY);
        auto_complete_minute_payment.setAdapter(minute_adapter);

        ArrayAdapter<Integer> second_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MINUTE_ARRAY);
        auto_complete_second_payment.setAdapter(second_adapter);

        ArrayAdapter<Integer> millisecond_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, MILLISECOND_ARRAY);
        auto_complete_millisecond_payment.setAdapter(millisecond_adapter);
        super.onResume();
    }

    private void save() {
        progress += 25;
        updateProgressBar();
        //ktu kina me marr krejt field edhe nese e bon save atehere me ru nshared prefrence automatic dhe hour minute etc.
        //true i bjen qe o checked false i bjen qe so checked
        boolean PAYMENT_STATE = switchPayment.isChecked();
        int HOUR = Integer.parseInt(auto_complete_hour_payment.getText().toString());
        int MINUTE = Integer.parseInt(auto_complete_minute_payment.getText().toString());
        int SECOND = Integer.parseInt(auto_complete_second_payment.getText().toString());
        int MILLISECOND = Integer.parseInt(auto_complete_millisecond_payment.getText().toString());

        PayoutConfig payoutConfig = new PayoutConfig(
                PAYOUT_CONFIG,
                PAYMENT_STATE,
                HOUR,
                MINUTE,
                SECOND,
                MILLISECOND,
                DateHelper.getDateTime());

        progress += 25;
        updateProgressBar();

        //qitu kina me i bo save to firebase
        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .set(payoutConfig, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progress += 25;
                        updateProgressBar();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress += 25;
                                updateProgressBar();
                                dismissProgressBar();
                            }
                        }, 1500);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: " + e.getMessage());
                    }
                });
    }

    private void switchPaymentCheck(Boolean isChecked) {
        if (isChecked) {
            //nese o true
            constraintAutomaticPayment.setVisibility(View.VISIBLE);
            constrainProcessPayment.setVisibility(GONE);

            firebaseFirestore.collection(PAYOUT_CONFIG)
                    .document(PAYOUT_CONFIG)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!StringHelper.empty(documentSnapshot.getString("HOUR")))
                            {
                                auto_complete_hour_payment.setText(documentSnapshot.getString("HOUR"));
                            }else {
                                auto_complete_hour_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if(!StringHelper.empty(documentSnapshot.getString("MINUTE")))
                            {
                                auto_complete_minute_payment.setText(documentSnapshot.getString("MINUTE"));
                            }else {
                                auto_complete_minute_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if(!StringHelper.empty(documentSnapshot.getString("SECOND")))
                            {
                                auto_complete_second_payment.setText(documentSnapshot.getString("SECOND"));
                            }else {
                                auto_complete_second_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if(!StringHelper.empty(documentSnapshot.getString("MILLISECOND")))
                            {
                                auto_complete_millisecond_payment.setText(documentSnapshot.getString("MILLISECOND"));
                            }else {
                                auto_complete_millisecond_payment.setText(String.valueOf(TIME_DEFAULT));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", e.getMessage());
                        }
                    });

        } else {
            //nese o false
            constraintAutomaticPayment.setVisibility(GONE);
            constrainProcessPayment.setVisibility(View.VISIBLE);
        }
    }

    private void getFromDatabaseAndCheck() {
        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("PAYMENT_STATE"))) {
                                //duhet me kon toogle lshut
                                switchPayment.setChecked(true);
                                constraintAutomaticPayment.setVisibility(View.VISIBLE);
                                constrainProcessPayment.setVisibility(GONE);
                            } else {
                                switchPayment.setChecked(false);
                                constraintAutomaticPayment.setVisibility(GONE);
                                constrainProcessPayment.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateProgressBar() {
        invoiceProgressBar.setProgress(progress);
        tv_invoiceProgressBar.setText(this.progress + "%");
        constrainInvoiceProgress.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void dismissProgressBar() {
        constrainInvoiceProgress.setVisibility(GONE);
        progress = 0;
        invoiceProgressBar.setProgress(progress);
        tv_invoiceProgressBar.setText(this.progress + "%");
    }

    private void setPayOutTask(int hour, int minute, int second, int millisecond) {
        Timer timer = new Timer();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        timer.schedule(new PayoutsPaypalTask(), cal.getTime());
    }

    private void analyticsInvoice() {
        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num_of_reports_today = task.getResult().size();
                            tvInvoiceReport.setText(mContext.getText(R.string.today) + " " +
                                    mContext.getText(R.string.are_created) + " " + num_of_reports_today + " " +
                                    mContext.getText(R.string.invoice));
                        }
                    }
                });
    }

    private void setHour() {
        HOUR_ARRAY = new Integer[24];
        for(int i=0; i<24; i++) {
            HOUR_ARRAY[i] = i;
        }
    }

    private void setMinuteAndSecond() {
        MINUTE_ARRAY = new Integer[60];
        for(int i=0; i<60; i++) {
            MINUTE_ARRAY[i] = i;
        }
    }

    private void setMilliseconds() {
        MILLISECOND_ARRAY = new Integer[60];
        for(int i=0; i<60; i++) {
            MILLISECOND_ARRAY[i] = i*1000;
        }
    }

    private void process(String currency) throws JSONException {
        progress += 20;
        updateProgressBar();
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("status", PENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.size() != 0) {
                                CollectionReference collRef = firebaseFirestore.collection(PAYOUTS);
                                String sender_batch_id = collRef.document().getId();
                                int rows = queryDocumentSnapshots.size();
                                progress += 20;
                                updateProgressBar();

                                JSONObject main = new JSONObject();

                                //static side
                                JSONObject sender_batch_header = new JSONObject();
                                sender_batch_header.put("sender_batch_id", sender_batch_id);
                                sender_batch_header.put("recipient_type", "EMAIL");
                                sender_batch_header.put("email_subject", "You have money!");
                                sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

                                String[] arrayTransactionId = new String[rows];
                                String[] arrayUserId = new String[rows];
                                String[] arrayAmount = new String[rows];
                                JSONArray arrayItems = new JSONArray();

                                progress += 20;
                                updateProgressBar();

                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                    String updated_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("updated_date_time");
                                    String transactionID = queryDocumentSnapshots.getDocuments().get(i).getString("transactionID");
                                    String userId = queryDocumentSnapshots.getDocuments().get(i).getString("userId");
                                    String account = queryDocumentSnapshots.getDocuments().get(i).getString("account");
                                    String status = queryDocumentSnapshots.getDocuments().get(i).getString("status");
                                    Double amount = queryDocumentSnapshots.getDocuments().get(i).getDouble("amount");
                                    String fullName = queryDocumentSnapshots.getDocuments().get(i).getString("fullName");
                                    String address = queryDocumentSnapshots.getDocuments().get(i).getString("address");
                                    String bankName = queryDocumentSnapshots.getDocuments().get(i).getString("bankName");
                                    String accountNumber = queryDocumentSnapshots.getDocuments().get(i).getString("accountNumber");
                                    String paypalEmail = queryDocumentSnapshots.getDocuments().get(i).getString("paypalEmail");

                                    if (!StringHelper.empty(created_date_time)) {
                                        Date givenDate = new SimpleDateFormat(DATE).parse(created_date_time);
                                        if (!DateHelper.checkDayBefore24(givenDate)) {
                                            JSONObject item = new JSONObject();
                                            item.put("sender_item_id", "item" + i);
                                            item.put("recipient_wallet", "PAYPAL");
                                            item.put("receiver", paypalEmail);

                                            JSONObject amountObj = new JSONObject();
                                            amountObj.put("value", amount);
                                            amountObj.put("currency", currency);
                                            item.put("amount", amountObj);
                                            arrayItems.put(item);
                                            arrayTransactionId[i] = transactionID;
                                            arrayUserId[i] = userId;
                                            arrayAmount[i] = String.valueOf(amount);
                                        }
                                    }
                                }
                                main.put("sender_batch_header", sender_batch_header);
                                main.put("items", arrayItems);

                                progress += 20;
                                updateProgressBar();

                                //process payment when array done
                                processPayoutsWithPaypal(main, arrayTransactionId, arrayUserId, arrayAmount);

                            }
                        } catch (ParseException | JSONException | IOException e) {
                            e.printStackTrace();
                            dismissProgressBar();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void processPayoutsWithPaypal(JSONObject main,
                                          String[] arrayTransactionId,
                                          String[] arrayUserId,
                                          String[] arrayAmount) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, main.toString(4));
        Request request = new Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", PAYPAL_SANDBOX_KEY_BEARER)
                .build();
        Response response = client.newCall(request).execute();

        // Reset the response code
        responseCode = 0;

        if ((responseCode = response.code()) == 201) {
            progress += 20;
            updateProgressBar();
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PAID, arrayTransactionId[i]);
            }

            for (int i = 0; i < arrayUserId.length; i++) {
                updateBalanceUser(Double.parseDouble(arrayAmount[i]), arrayUserId[i]);
            }

        } else if ((responseCode = response.code()) == 204) {
            progress += 20;
            updateProgressBar();
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(REFUSED, arrayTransactionId[i]);
            }
        } else {
            progress += 20;
            updateProgressBar();
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PENDING, arrayTransactionId[i]);
            }
        }

    }

    private void updateStatusInvoice(String status, String transactionId) {
        firebaseFirestore.collection(INVOICE)
                .document(transactionId)
                .update("status", status).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void updateBalanceUser(Double amount, String userId) {
        firebaseFirestore.collection(USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double totalPaid = documentSnapshot.getDouble("totalPaid");
                        updateBalanceTotalPaidUser(userId, amount, totalPaid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void updateBalanceTotalPaidUser(String userId, Double amount, Double totalPaid) {
        Double totalPaidNew = totalPaid + amount;
        firebaseFirestore.collection(USERS)
                .document(userId)
                .update("balance", BALANCE_DEFAULT,
                        "totalPaid", totalPaidNew).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

}