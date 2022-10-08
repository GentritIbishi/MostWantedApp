package fiek.unipr.mostwantedapp.fragment.admin.search;

import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.invoice.InvoiceListAdapter;
import fiek.unipr.mostwantedapp.fragment.admin.InvoiceFragment;
import fiek.unipr.mostwantedapp.models.Invoice;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class SearchInvoiceFragment extends Fragment implements RecyclerViewInterface {

    private Context mContext;
    private View view;
    private RecyclerView lvInvoices;
    private LinearLayout search_admin_invoice_list_view1, search_admin_invoice_list_view2;
    private TextView tv_search_admin_invoice_userListEmpty;
    private ViewSwitcher search_admin_invoice_list_switcher;
    private InvoiceListAdapter invoiceListAdapter;
    private ArrayList<Invoice> invoiceArrayList;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText admin_invoice_search_filter;

    public SearchInvoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_invoice, container, false);
        mContext = getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout admin_invoice_pullToRefreshInSearch = view.findViewById(R.id.admin_invoice_pullToRefreshInSearch);
        admin_invoice_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                invoiceArrayList.clear();
                loadDatainListview();
                admin_invoice_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        admin_invoice_search_filter.requestFocus();

        admin_invoice_search_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    private void InitializeFields() {
        admin_invoice_search_filter = view.findViewById(R.id.admin_invoice_search_filter);
        search_admin_invoice_list_view1 = view.findViewById(R.id.search_admin_invoice_list_view1);
        search_admin_invoice_list_view2 = view.findViewById(R.id.search_admin_invoice_list_view2);
        tv_search_admin_invoice_userListEmpty = view.findViewById(R.id.tv_search_admin_invoice_userListEmpty);
        search_admin_invoice_list_switcher = view.findViewById(R.id.search_admin_invoice_list_switcher);
        lvInvoices = view.findViewById(R.id.lvInvoices);
        invoiceArrayList = new ArrayList<>();
        invoiceListAdapter = new InvoiceListAdapter(mContext, invoiceArrayList, this);
        lvInvoices.setAdapter(invoiceListAdapter);
        lvInvoices.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection(INVOICE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            if (search_admin_invoice_list_switcher.getCurrentView() == search_admin_invoice_list_view2) {
                                search_admin_invoice_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Invoice invoice = d.toObject(Invoice.class);
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                invoiceArrayList.add(invoice);
                            }

                            invoiceListAdapter.notifyDataSetChanged();
                        } else {
                            if (search_admin_invoice_list_switcher.getCurrentView() == search_admin_invoice_list_view1) {
                                search_admin_invoice_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(mContext, mContext.getText(R.string.failed_to_load_data), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void filter(String s) {
        invoiceArrayList.clear();
        firebaseFirestore.collection(INVOICE)
                .orderBy("transactionID")
                .startAt(s)
                .endAt(s + "\uf8ff")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            if (search_admin_invoice_list_switcher.getCurrentView() == search_admin_invoice_list_view2) {
                                search_admin_invoice_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Invoice invoice = d.toObject(Invoice.class);
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                invoiceArrayList.add(invoice);
                            }
                            invoiceListAdapter.notifyDataSetChanged();
                        } else {
                            if (search_admin_invoice_list_switcher.getCurrentView() == search_admin_invoice_list_view1) {
                                search_admin_invoice_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(mContext, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        InvoiceFragment invoiceFragment = new InvoiceFragment();
        Bundle viewBundle = new Bundle();
        viewBundle.putString("created_date_time", invoiceArrayList.get(position).getCreated_date_time());
        viewBundle.putString("transactionID", invoiceArrayList.get(position).getTransactionID());
        viewBundle.putString("userId", invoiceArrayList.get(position).getUserId());
        viewBundle.putString("account", invoiceArrayList.get(position).getAccount());
        viewBundle.putString("status", invoiceArrayList.get(position).getStatus());
        viewBundle.putString("updated_date_time", invoiceArrayList.get(position).getUpdated_date_time());
        viewBundle.putDouble("amount", invoiceArrayList.get(position).getAmount());
        invoiceFragment.setArguments(viewBundle);
        loadFragment(invoiceFragment);
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}