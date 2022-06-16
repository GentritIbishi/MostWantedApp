package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

import fiek.unipr.mostwantedapp.R;

public class HomeFragment extends Fragment {

    View admin_dashboard_view;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        admin_dashboard_view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final LinearLayout l_admin_myAccount = admin_dashboard_view.findViewById(R.id.l_admin_myAccount);

        l_admin_myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AccountFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_register = admin_dashboard_view.findViewById(R.id.l_admin_register);
        l_admin_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final LinearLayout l_admin_analytics = admin_dashboard_view.findViewById(R.id.l_admin_analytics);
        l_admin_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ProfileFragment();
                loadFragment(fragment);
            }
        });

        final LinearLayout l_admin_locationReports = admin_dashboard_view.findViewById(R.id.l_admin_locationReports);
        l_admin_locationReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LocationReportsFragment();
                loadFragment(fragment);
            }
        });

        return admin_dashboard_view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}