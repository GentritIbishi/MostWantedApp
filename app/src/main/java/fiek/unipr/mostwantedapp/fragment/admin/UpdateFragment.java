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
import fiek.unipr.mostwantedapp.fragment.admin.update.UpdateUserListFragment;

public class UpdateFragment extends Fragment {

    private View update_view;
    private ConstraintLayout update_constraintPU, update_constraintWP, update_constraintInv;

    public UpdateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_view = inflater.inflate(R.layout.fragment_update, container, false);

        update_constraintPU = update_view.findViewById(R.id.update_constraintPU);
        update_constraintWP = update_view.findViewById(R.id.update_constraintWP);
        update_constraintInv = update_view.findViewById(R.id.update_constraintInv);

        update_constraintPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UpdateUserListFragment();
                loadFragment(fragment);
            }
        });

        update_constraintWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        update_constraintInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return update_view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}