package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.InvoiceState;

public class InvoicePaymentProcessFragment extends Fragment {

    private Context mContext;
    private View view;
    private String created_date_time, transactionID, userId, account, status, updated_date_time;
    private Double amount;

    private MaterialAutoCompleteTextView auto_complete_invoice_status;
    private TextInputEditText et_invoice_transaction_id, et_invoice_created_date_time, et_invoice_account,
            et_invoice_amount, et_invoice_userId, et_invoice_updated_date_time;
    private Button btnSaveInvoice;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    public InvoicePaymentProcessFragment() {
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
        view = inflater.inflate(R.layout.fragment_invoice_process_payment, container, false);
        mContext = getContext();

        initializeFields();
        getAndSetFromBundle();
        process();

        return view;
    }

    private void process() {
        btnSaveInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_status = auto_complete_invoice_status.getText().toString();
                PaymentProcessFragment paymentProcessFragment = new PaymentProcessFragment();
                Bundle viewBundle = new Bundle();
                viewBundle.putString("created_date_time", created_date_time);
                viewBundle.putString("transactionID", transactionID);
                viewBundle.putString("userId", userId);
                viewBundle.putString("account", account);
                viewBundle.putString("status", new_status);
                viewBundle.putString("updated_date_time", updated_date_time);
                viewBundle.putDouble("amount", amount);
                paymentProcessFragment.setArguments(viewBundle);
                loadFragment(paymentProcessFragment);
            }
        });
    }

    private void initializeFields() {
        auto_complete_invoice_status = view.findViewById(R.id.auto_complete_invoice_status);
        et_invoice_transaction_id = view.findViewById(R.id.et_invoice_transaction_id);
        et_invoice_created_date_time = view.findViewById(R.id.et_invoice_created_date_time);
        et_invoice_account = view.findViewById(R.id.et_invoice_account);
        et_invoice_amount = view.findViewById(R.id.et_invoice_amount);
        et_invoice_userId = view.findViewById(R.id.et_invoice_userId);
        et_invoice_updated_date_time = view.findViewById(R.id.et_invoice_updated_date_time);
        btnSaveInvoice = view.findViewById(R.id.btnSaveInvoice);
        ArrayAdapter<String> invoice_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.invoice_state));
        auto_complete_invoice_status.setAdapter(invoice_adapter);
    }

    @Override
    public void onResume() {
        ArrayAdapter<String> invoice_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.invoice_state));
        auto_complete_invoice_status.setAdapter(invoice_adapter);
        super.onResume();
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

                et_invoice_created_date_time.setText(created_date_time);
                et_invoice_updated_date_time.setText(updated_date_time);
                et_invoice_transaction_id.setText(transactionID);
                et_invoice_userId.setText(userId);
                et_invoice_account.setText(account);
                auto_complete_invoice_status.setText(status);
                et_invoice_amount.setText(String.valueOf(amount));
            }

        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}