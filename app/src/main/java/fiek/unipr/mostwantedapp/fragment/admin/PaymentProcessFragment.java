package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import fiek.unipr.mostwantedapp.R;

public class PaymentProcessFragment extends Fragment {

    private Context mContext;
    private View view;
    private String created_date_time, transactionID, userId, account, status, updated_date_time;
    private Double amount;

    private ConstraintLayout constraintCreditCard, constraintPaypalProcess;

    private static final String CONFIG_CLIENT_ID = "ARk7W1iWMwu0OpCGImzymAqnWmsqlucEYjyVXYEydkNZCAGFEzFMbj8XAsIWysu3lOLwx5WT5YNLmNox";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    public PaymentProcessFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_process, container, false);
        mContext = getContext();

        initializeFields();
        paypalProcessCheckout();
        creditCardCheckout();

        return view;
    }

    private void initializeFields() {
        constraintCreditCard = view.findViewById(R.id.constraintCreditCard);
        constraintPaypalProcess = view.findViewById(R.id.constraintPaypalProcess);
    }

    private void paypalProcessCheckout() {
        constraintPaypalProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPaypal();
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

    private void processPaypal() {
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

}