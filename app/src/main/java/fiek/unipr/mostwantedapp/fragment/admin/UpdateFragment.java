package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.investigator.UpdateInvestigatorListFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonListFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserListFragment;

public class UpdateFragment extends Fragment {

    private View update_view;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ConstraintLayout update_constraintPU, update_constraintWP, update_constraintInv;

    public UpdateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_view = inflater.inflate(R.layout.fragment_update, container, false);

        initializeFields();
        checkAdminLevel(firebaseAuth.getUid());

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
                Fragment fragment = new UpdatePersonListFragment();
                loadFragment(fragment);
            }
        });

        update_constraintInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UpdateInvestigatorListFragment();
                loadFragment(fragment);
            }
        });


        return update_view;
    }

    private void initializeFields() {
        update_constraintPU = update_view.findViewById(R.id.update_constraintPU);
        update_constraintWP = update_view.findViewById(R.id.update_constraintWP);
        update_constraintInv = update_view.findViewById(R.id.update_constraintInv);
    }

    private void checkAdminLevel(String userId) {
        firebaseFirestore.collection(USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = documentSnapshot.getString("role");
                        if(role.equals("Semi-Admin"))
                        {
                            update_constraintPU.setVisibility(View.GONE);
                        }else if(role.equals("Admin"))
                        {
                            update_constraintPU.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}