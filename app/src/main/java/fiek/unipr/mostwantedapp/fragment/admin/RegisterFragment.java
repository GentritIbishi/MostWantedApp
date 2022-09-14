package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.register.investigator.RegisterInvestigatorFragment;
import fiek.unipr.mostwantedapp.fragment.admin.register.person.RegisterPersonFragment;
import fiek.unipr.mostwantedapp.fragment.admin.register.user.RegisterUserFragment;

public class RegisterFragment extends Fragment {

    private View view_register_fragment;
    private ConstraintLayout constraintPU, constraintWP, constraintInv;

    public RegisterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_register_fragment = inflater.inflate(R.layout.fragment_register, container, false);
        initialize();

        constraintPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegisterUserFragment();
                loadFragment(fragment);
            }
        });

        constraintWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegisterPersonFragment();
                loadFragment(fragment);
            }
        });

        constraintInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RegisterInvestigatorFragment();
                loadFragment(fragment);
            }
        });

        return view_register_fragment;
    }

    private void initialize() {
        constraintPU = view_register_fragment.findViewById(R.id.constraintPU);
        constraintWP = view_register_fragment.findViewById(R.id.constraintWP);
        constraintInv = view_register_fragment.findViewById(R.id.constraintInv);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}