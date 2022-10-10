package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonListFragment;
import fiek.unipr.mostwantedapp.models.Invoice;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import okhttp3.OkHttpClient;


public class PaymentProcessFragment extends Fragment {

    private Context mContext;
    private View view;
    private String created_date_time, transactionID, userId, account, status, updated_date_time;
    private Double amount;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private ConstraintLayout constraintCreditCard, constraintPaypalProcess;

    public static final String clientKey = "ARk7W1iWMwu0OpCGImzymAqnWmsqlucEYjyVXYEydkNZCAGFEzFMbj8XAsIWysu3lOLwx5WT5YNLmNox";


    public PaymentProcessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_process, container, false);
        mContext = getContext();

        initializeFields();
        getAndSetFromBundle();
        paypalProcessCheckout();
        creditCardCheckout();

        return view;
    }

    private void processPaypalPayment(String email) {

    }

    private void setStatusPaid(String new_status) {

//        Invoice invoice = new Invoice(created_date_time,
//                transactionID,
//                userId,
//                account,
//                new_status,
//                DateHelper.getDateTime(),
//                amount);

//        firebaseFirestore.collection(INVOICE)
//                .document(transactionID)
//                .set(invoice, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(getContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }


    private void initializeFields() {
        constraintCreditCard = view.findViewById(R.id.constraintCreditCard);
        constraintPaypalProcess = view.findViewById(R.id.constraintPaypalProcess);
    }

    private void paypalProcessCheckout() {
        constraintPaypalProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void creditCardCheckout() {
        constraintCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processCreditCard();
            }
        });
    }

    private void processCreditCard() {
    }

    private void getAndSetFromBundle() {

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                created_date_time = bundle.getString("created_date_time");
                transactionID = bundle.getString("transactionID");
                userId = bundle.getString("userId");
                account = bundle.getString("account");
                status = bundle.getString("status");
                updated_date_time = bundle.getString("updated_date_time");
                amount = bundle.getDouble("amount");
            }

        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }

    public void showMessageAlert(String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // on pressing cancel button
        alertDialog.setNegativeButton(mContext.getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


}