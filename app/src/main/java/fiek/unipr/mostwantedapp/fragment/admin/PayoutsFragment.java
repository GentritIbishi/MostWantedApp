package fiek.unipr.mostwantedapp.fragment.admin;

import static android.view.View.GONE;
import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE_PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUT_CONFIG;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL_SANDBOX_KEY_BEARER;
import static fiek.unipr.mostwantedapp.utils.Constants.PENDING;
import static fiek.unipr.mostwantedapp.utils.Constants.REFUSED;
import static fiek.unipr.mostwantedapp.utils.Constants.TIME_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.USD;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.InvoicesPaid;
import fiek.unipr.mostwantedapp.models.PayoutConfig;
import fiek.unipr.mostwantedapp.services.PayoutReceiver;
import fiek.unipr.mostwantedapp.utils.DateHelper;
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
    @SuppressLint("UseSwitchCompatOrMaterialCode")
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
    private String urlOfPdfUploaded;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public PayoutsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, PayoutReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        checkForAutomaticPayment();
    }

    private void checkForAutomaticPayment() {
        //here we need to check if payment_state is true
        // if is true is automatic payment get all time from user
        // if is not true is manual payment

        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean payment_STATE = documentSnapshot.getBoolean("payment_state");
                        if(payment_STATE != null)
                        {
                            if(payment_STATE.equals(true))
                            {
                                // Is automatic payment get all time from user
                                Integer hour = Integer.parseInt(String.valueOf(documentSnapshot.get("hour")));
                                Integer minute = Integer.parseInt(String.valueOf(documentSnapshot.get("minute")));
                                Integer second = Integer.parseInt(String.valueOf(documentSnapshot.get("second")));
                                Integer millisecond = Integer.parseInt(String.valueOf(documentSnapshot.get("millisecond")));
                                setPaymentAtSpecificTime(hour, minute, second, millisecond);

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

    private void setPaymentAtSpecificTime(int hour, int minute, int second, int millisecond) {
        //Set the alarm to start at 20:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        //in this case PayoutReceiver is the Broadcast Receiver and it already has a context, so you can directly set the ringer mode to silent from the Broadcast
        //Receiver without starting up
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

        final SwipeRefreshLayout payoutsSwipe = view.findViewById(R.id.payoutsSwipe);
        payoutsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                analyticsInvoice();
                payoutsSwipe.setRefreshing(false);
            }
        });

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

        getPaymentState();

        switchPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //nese o true
                    constraintAutomaticPayment.setVisibility(View.VISIBLE);
                    constrainProcessPayment.setVisibility(GONE);
                } else {
                    constraintAutomaticPayment.setVisibility(GONE);
                    constrainProcessPayment.setVisibility(View.VISIBLE);
                    saveEmpty(false);
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

    private void getPaymentState() {
        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean payment_STATE = documentSnapshot.getBoolean("payment_state");
                        if(payment_STATE != null)
                        {
                            switchPaymentCheck(payment_STATE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    private void checkProcess() {
        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int num_of_reports_today = queryDocumentSnapshots.size();
                        if (num_of_reports_today > 0) {
                            try {
                                process(USD);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            UIMessage.showMessage(mContext, mContext.getText(R.string.no_invoice),
                                    mContext.getText(R.string.you_must_have_at_least_one_invoice));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CHECKPROCESS_ERROR", e.getMessage());
                        dismissProgressBar();
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

    private void saveEmpty(Boolean payment_STATE) {
        growProgressBar(50);
        PayoutConfig payoutConfig = new PayoutConfig(
                PAYOUT_CONFIG,
                payment_STATE,
                null,
                null,
                null,
                null,
                DateHelper.getDateTime()
        );

        firebaseFirestore.collection(PAYOUT_CONFIG)
                .document(PAYOUT_CONFIG)
                .set(payoutConfig)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        growProgressBar(50);
                        dismissProgressBar();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressBar();
                        Log.d("TAG", e.getMessage());
                        dismissProgressBar();
                    }
                });
        dismissProgressBar();
    }

    private void switchPaymentCheck(Boolean isChecked) {
        if (isChecked) {
            //nese o true
            constraintAutomaticPayment.setVisibility(View.VISIBLE);
            constrainProcessPayment.setVisibility(GONE);
            switchPayment.setChecked(true);
            firebaseFirestore.collection(PAYOUT_CONFIG)
                    .document(PAYOUT_CONFIG)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!StringHelper.empty(String.valueOf(documentSnapshot.get("hour")))) {
                                auto_complete_hour_payment.setText(String.valueOf(documentSnapshot.get("hour")));
                            } else {
                                auto_complete_hour_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if (!StringHelper.empty(String.valueOf(documentSnapshot.get("minute")))) {
                                auto_complete_minute_payment.setText(String.valueOf(documentSnapshot.get("minute")));
                            } else {
                                auto_complete_minute_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if (!StringHelper.empty(String.valueOf(documentSnapshot.get("second")))) {
                                auto_complete_second_payment.setText(String.valueOf(documentSnapshot.get("second")));
                            } else {
                                auto_complete_second_payment.setText(String.valueOf(TIME_DEFAULT));
                            }

                            if (!StringHelper.empty(String.valueOf(documentSnapshot.get("millisecond")))) {
                                auto_complete_millisecond_payment.setText(String.valueOf(documentSnapshot.get("millisecond")));
                            } else {
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
            switchPayment.setChecked(false);
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
                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("payment_state"))) {
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

    private void analyticsInvoice() {
        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int num_of_reports_today = queryDocumentSnapshots.size();
                        tvInvoiceReport.setText(mContext.getText(R.string.today) + " " +
                                mContext.getText(R.string.are_created) + " " + num_of_reports_today + " " +
                                mContext.getText(R.string.invoice));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setHour() {
        HOUR_ARRAY = new Integer[24];
        for (int i = 0; i < 24; i++) {
            HOUR_ARRAY[i] = i;
        }
    }

    private void setMinuteAndSecond() {
        MINUTE_ARRAY = new Integer[60];
        for (int i = 0; i < 60; i++) {
            MINUTE_ARRAY[i] = i;
        }
    }

    private void setMilliseconds() {
        MILLISECOND_ARRAY = new Integer[60];
        for (int i = 0; i < 60; i++) {
            MILLISECOND_ARRAY[i] = i * 1000;
        }
    }

    private void process(String currency) throws JSONException {
        growProgressBar(25);
        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            try {
                                //collection
                                int rows = queryDocumentSnapshots.size();
                                List<String> saveInvoices = new ArrayList<>();
                                String[] arrayTransactionId = new String[rows];
                                String[] arrayUserId = new String[rows];
                                String[] arrayAmount = new String[rows];

                                float[] pointColumnWidths = {200F, 200F, 200F, 200F, 200F};
                                Table table = new Table(pointColumnWidths);

                                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_transaction_id)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_account)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_created_date_time)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_amount)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_status)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);

                                CollectionReference collRef = firebaseFirestore.collection(PAYOUTS);
                                String sender_batch_id = collRef.document().getId();

                                JSONObject main = new JSONObject();

                                JSONObject sender_batch_header = new JSONObject();
                                sender_batch_header.put("sender_batch_id", sender_batch_id);
                                sender_batch_header.put("recipient_type", "EMAIL");
                                sender_batch_header.put("email_subject", "You have money!");
                                sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

                                JSONArray arrayItems = new JSONArray();

                                growProgressBar(25);

                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                    String updated_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("updated_date_time");
                                    String transactionID = queryDocumentSnapshots.getDocuments().get(i).getString("transactionID");
                                    String userId = queryDocumentSnapshots.getDocuments().get(i).getString("userId");
                                    String account = queryDocumentSnapshots.getDocuments().get(i).getString("account");
                                    String new_status = PAID;
                                    Double amount = queryDocumentSnapshots.getDocuments().get(i).getDouble("amount");
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

                                            saveInvoices.add(i+transactionID+", "+paypalEmail+", "+created_date_time+", "+amount+", "+new_status);

                                            table.addCell(new Cell().add(new Paragraph(transactionID)));
                                            table.addCell(new Cell().add(new Paragraph(paypalEmail)));
                                            table.addCell(new Cell().add(new Paragraph(created_date_time)));
                                            table.addCell(new Cell().add(new Paragraph(String.valueOf(amount))));
                                            table.addCell(new Cell().add(new Paragraph(new_status)));
                                        }
                                    }
                                }

                                main.put("sender_batch_header", sender_batch_header);
                                main.put("items", arrayItems);

                                Log.d("TAG", "onSuccess: JSON DONE, TABLE DONE");

                                Log.d("JSON", main.toString(4));

                                growProgressBar(25);

                                processPayoutsWithPaypal(main,
                                        arrayTransactionId,
                                        arrayUserId,
                                        arrayAmount,
                                        saveInvoices,
                                        table);

                            } catch (ParseException | JSONException | IOException e) {
                                e.printStackTrace();
                                dismissProgressBar();
                            }
                        }else {
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

    private void processPayoutsWithPaypal(JSONObject main, String[] arrayTransactionId,
                                          String[] arrayUserId, String[] arrayAmount,
                                          List<String> saveInvoices, Table table) throws IOException, JSONException
    {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, main.toString(4));

        Request request = new Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", PAYPAL_SANDBOX_KEY_BEARER)
                .build();

        Response response = client.newCall(request).execute();

        Log.d("RESPONSE", response.toString());

        // Reset the response code
        responseCode = 0;

        if ((responseCode = response.code()) == 201)
        {
            Log.d("TAG", "onSuccess: RESPONSE 201");

            growProgressBar(5);

            for (int i = 0; i < arrayTransactionId.length; i++)
            {
                updateStatusInvoice(PAID, arrayTransactionId[i]);
            }

            for (int i = 0; i < arrayUserId.length; i++) {
                updateBalanceUser(Double.parseDouble(arrayAmount[i]), arrayUserId[i]);
            }

            savePayoutMade(saveInvoices, table);

        } else if ((responseCode = response.code()) == 204) {
            growProgressBar(25);
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(REFUSED, arrayTransactionId[i]);
            }
            dismissProgressBar();
        } else {
            Log.d("TAG", "onSuccess: PENDING");
            growProgressBar(25);
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PENDING, arrayTransactionId[i]);
            }
            dismissProgressBar();
        }

    }

    private void growProgressBar(Integer number) {
        progress += number;
        updateProgressBar();
    }

    private void savePayoutMade(List<String> paidInvoices, Table table) {
        CollectionReference collRef = firebaseFirestore.collection(INVOICE_PAID);
        String id = collRef.document().getId();

        InvoicesPaid invoicesPaid = new InvoicesPaid(
                id,
                paidInvoices,
                DateHelper.getDateTime());

        firebaseFirestore.collection(INVOICE_PAID)
                .document(id)
                .set(invoicesPaid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("CREATED DOCUMENT", "TRUE");
                        Uri uri = null;
                        try {
                            uri = generatePDF(table);
                        } catch (IOException e) {
                            Log.d("GENERATE_ERROR", e.getMessage());
                        }
                        uploadPDFtoFirebase(uri, id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("BULK INVOICE FAILED", e.getMessage());
                    }
                });

    }

    private void uploadPDFtoFirebase(Uri pdfUri, String id) {
        //upload image to storage in firebase
        StorageReference profRef = storageReference.child(INVOICE_PAID + "/" + id + "/" + PAYOUTS_PDF);
        profRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save uri of pdf uploaded file
                        urlOfPdfUploaded = uri.toString();
                        saveReportUrlToPayoutsCollection(id, urlOfPdfUploaded);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPLOAD ERROR", e.getMessage());
                        dismissProgressBar();
                    }
                });
            }
        });
    }

    private void saveReportUrlToPayoutsCollection(String id, String urlOfPdfUploaded) {
        firebaseFirestore.collection(INVOICE_PAID)
                .document(id)
                .update("urlOfPdfUploaded", urlOfPdfUploaded)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        growProgressBar(25);
        dismissProgressBar();
    }

    private Uri generatePDF(Table table) throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        // File directory = getFilesDir();
        File file = new File(pdfPath, "Payout.pdf");

        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDocument = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.saveState();

        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);

        canvas.setExtGState(state);

        Document document = new Document(pdfDocument, PageSize.A4);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        document.setMargins(15, 15, 15, 15);

        Drawable ic_republic_of_kosovo = ContextCompat.getDrawable(mContext, R.drawable.ic_republic_of_kosovo);
        assert ic_republic_of_kosovo != null;
        Bitmap bmp_ic_republic_of_kosovo = ((BitmapDrawable) ic_republic_of_kosovo).getBitmap();
        ByteArrayOutputStream stream_ic_republic_of_kosovo = new ByteArrayOutputStream();
        bmp_ic_republic_of_kosovo.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_republic_of_kosovo);
        byte[] bitmap_data_ic_republic_of_kosovo = stream_ic_republic_of_kosovo.toByteArray();

        ImageData image_data_ic_republic_of_kosovo = ImageDataFactory.create(bitmap_data_ic_republic_of_kosovo);
        Image image_ic_republic_of_kosovo = new Image(image_data_ic_republic_of_kosovo);

        Drawable ic_kp = ContextCompat.getDrawable(mContext, R.drawable.ic_kp_10_opacity);
        assert ic_kp != null;
        Bitmap bmp_ic_kp = ((BitmapDrawable) ic_kp).getBitmap();
        ByteArrayOutputStream stream_ic_kp = new ByteArrayOutputStream();
        bmp_ic_kp.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_kp);
        byte[] bitmap_data_ic_kp = stream_ic_kp.toByteArray();

        ImageData image_data_ic_kp = ImageDataFactory.create(bitmap_data_ic_kp);
        Image image_ic_kp = new Image(image_data_ic_kp);

        //image ic_kp as background of pdf
        canvas.addImage(image_data_ic_kp, 0, 0, pdfDocument.getDefaultPageSize().getWidth(), false);
        canvas.restoreState();

        DeviceRgb setColor = new DeviceRgb(8, 106, 119);

        table.setMargin(5);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setTextAlignment(TextAlignment.CENTER);

        image_ic_republic_of_kosovo.setWidth(80);
        image_ic_republic_of_kosovo.setHeight(90);

        image_ic_kp.setWidth(100);
        image_ic_kp.setHeight(100);

        image_ic_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);

        image_ic_kp.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph republika_e_kosoves = new Paragraph(String.valueOf(mContext.getText(R.string.republika_e_kosoves)));
        Paragraph republika_kosovo_republic_of_kosovo = new Paragraph(String.valueOf(mContext.getText(R.string.republika_kosovo_republic_of_kosovo)));
        Paragraph mpb_three_language = new Paragraph(String.valueOf(mContext.getText(R.string.mpb_three_language)));
        Paragraph empty = new Paragraph("");
        Paragraph payouts = new Paragraph(String.valueOf(mContext.getText(R.string.payouts)));

        republika_e_kosoves.setMultipliedLeading(0.5f);
        republika_kosovo_republic_of_kosovo.setMultipliedLeading(0.5f);
        mpb_three_language.setMultipliedLeading(0.5f);

        republika_e_kosoves.setFontSize(10f);
        republika_kosovo_republic_of_kosovo.setFontSize(10f);
        mpb_three_language.setFontSize(10f);
        payouts.setFontSize(22f);

        republika_e_kosoves.setHorizontalAlignment(HorizontalAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        mpb_three_language.setHorizontalAlignment(HorizontalAlignment.CENTER);
        payouts.setHorizontalAlignment(HorizontalAlignment.CENTER);

        republika_e_kosoves.setTextAlignment(TextAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setTextAlignment(TextAlignment.CENTER);
        mpb_three_language.setTextAlignment(TextAlignment.CENTER);
        payouts.setTextAlignment(TextAlignment.CENTER);
        payouts.setMarginTop(pdfDocument.getDefaultPageSize().getHeight() / 18);

        document.add(image_ic_republic_of_kosovo);
        document.add(republika_e_kosoves);
        document.add(republika_kosovo_republic_of_kosovo);
        document.add(mpb_three_language);
        document.add(empty);
        document.add(payouts);
        document.add(table);
        document.close();

        Uri uri = Uri.fromFile(file);
        Log.d("TAG", "generatePDF: " + uri);
        return uri;
    }

    private void updateStatusInvoice(String status, String transactionId) {
        Log.d("TAG", "updateStatusInvoice DONE");
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
        Log.d("TAG", "updateBalanceUser");
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
        Log.d("TAG", "updateBalanceTotalPaidUser");
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