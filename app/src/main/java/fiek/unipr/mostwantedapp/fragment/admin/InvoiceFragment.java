package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.register.investigator.RegisterInvestigatorFragment;
import fiek.unipr.mostwantedapp.fragment.admin.register.person.RegisterPersonFragment;
import fiek.unipr.mostwantedapp.fragment.admin.register.user.RegisterUserFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchInvoicePaidFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchInvoicePendingFragment;
import fiek.unipr.mostwantedapp.fragment.admin.search.SearchInvoiceRefusedFragment;

public class InvoiceFragment extends Fragment {

    private Context mContext;
    private View view;
    private ConstraintLayout constraintPaid, constraintPending, constraintRefused;

    public InvoiceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invoice, container, false);

        initialize();

        constraintPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchInvoicePaidFragment();
                loadFragment(fragment);
            }
        });

        constraintPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchInvoicePendingFragment();
                loadFragment(fragment);
            }
        });

        constraintRefused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchInvoiceRefusedFragment();
                loadFragment(fragment);
            }
        });

        return view;
    }

    private void initialize() {
        constraintPaid = view.findViewById(R.id.constraintPaid);
        constraintPending = view.findViewById(R.id.constraintPending);
        constraintRefused = view.findViewById(R.id.constraintRefused);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}