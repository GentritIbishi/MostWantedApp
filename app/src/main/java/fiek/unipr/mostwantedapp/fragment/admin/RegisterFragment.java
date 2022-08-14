package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.register.RegisterInvestigatorActivity;
import fiek.unipr.mostwantedapp.register.RegisterPersonActivity;
import fiek.unipr.mostwantedapp.register.RegisterUsersActivity;

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
                Intent intent = new Intent(getContext(), RegisterUsersActivity.class);
                startActivity(intent);
            }
        });

        constraintWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterPersonActivity.class);
                startActivity(intent);
            }
        });

        constraintInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterInvestigatorActivity.class);
                startActivity(intent);
            }
        });

        return view_register_fragment;
    }

    private void initialize() {
        constraintPU = view_register_fragment.findViewById(R.id.constraintPU);
        constraintWP = view_register_fragment.findViewById(R.id.constraintWP);
        constraintInv = view_register_fragment.findViewById(R.id.constraintInv);
    }
}