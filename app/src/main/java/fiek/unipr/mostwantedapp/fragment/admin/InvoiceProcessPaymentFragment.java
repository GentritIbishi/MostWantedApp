package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;

public class InvoiceProcessPaymentFragment extends Fragment {

    private Context mContext;
    private View view;
    private String created_date_time, transactionID, userId, account, status, updated_date_time;
    private Double amount;

    public InvoiceProcessPaymentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invoice_process_payment, container, false);
        mContext = getContext();

        getFromBundle();

        return view;
    }

    private void getFromBundle() {

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

        }catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
    }

}