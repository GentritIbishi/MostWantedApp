package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.BANK_ACCOUNT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.NOTIFICATION_ADMIN;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYMENT_INFORMATION;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL_SANDBOX_KEY_BEARER;
import static fiek.unipr.mostwantedapp.utils.Constants.USD;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayoutsFragment extends Fragment {

    private Context mContext;
    private View view;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payouts, container, false);


        try {
            process(USD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void process(String currency) throws JSONException {
        firebaseFirestore.collection(INVOICE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.size() != 0) {
                                CollectionReference collRef = firebaseFirestore.collection(PAYOUTS);
                                String sender_batch_id = collRef.document().getId();

                                JSONObject main = new JSONObject();

                                //static side
                                JSONObject sender_batch_header = new JSONObject();
                                sender_batch_header.put("sender_batch_id", sender_batch_id);
                                sender_batch_header.put("recipient_type", "EMAIL");
                                sender_batch_header.put("email_subject", "You have money!");
                                sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

                                JSONArray arrayItems = new JSONArray();

                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
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
                                        }
                                    }
                                }
                                main.put("sender_batch_header", sender_batch_header);
                                main.put("items", arrayItems);

                                //process payment when array done
                                processPayoutsWithPaypal(main);

                            }
                        } catch (ParseException | JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    private void processPayoutsWithPaypal(JSONObject main) throws IOException, JSONException {
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
    }

    private void testJson(String transactionID, int itemNumber, String paypalEmail, Double amount, String currency) throws JSONException {
        JSONObject main = new JSONObject();

        //static side
        JSONObject sender_batch_header = new JSONObject();
        sender_batch_header.put("sender_batch_id", transactionID);
        sender_batch_header.put("recipient_type", "EMAIL");
        sender_batch_header.put("email_subject", "You have money!");
        sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

        JSONArray arrayItems = new JSONArray();
        //dynamic side
        JSONObject item = new JSONObject();
        item.put("sender_item_id", "item" + itemNumber);
        item.put("recipient_wallet", "PAYPAL");
        item.put("receiver", paypalEmail);

        JSONObject amountObj = new JSONObject();
        amountObj.put("value", amount);
        amountObj.put("currency", currency);
        item.put("amount", amountObj);
        arrayItems.put(item);
        //dynamic side

        main.put("sender_batch_header", sender_batch_header);
        main.put("items", arrayItems);
    }
}