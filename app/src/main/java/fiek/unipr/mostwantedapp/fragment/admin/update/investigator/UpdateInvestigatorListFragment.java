package fiek.unipr.mostwantedapp.fragment.admin.update.investigator;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.UpdateInvestigatorListAdapter;
import fiek.unipr.mostwantedapp.helpers.MyButtonClickListener;
import fiek.unipr.mostwantedapp.helpers.MySwipeHelper;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Investigator;

public class UpdateInvestigatorListFragment extends Fragment implements RecyclerViewInterface {

    private View update_investigator_list_view;
    private RecyclerView lvUpdateInvestigators;
    private ArrayList<Investigator> investigatorArrayList;
    private UpdateInvestigatorListAdapter updateInvestigatorListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText update_et_investigator_search_filter;

    public UpdateInvestigatorListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_investigator_list_view = inflater.inflate(R.layout.fragment_update_investigator_list, container, false);

        // initializing our variable for firebase
        // firestore and getting its instance.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // below line is use to initialize our variables
        lvUpdateInvestigators = update_investigator_list_view.findViewById(R.id.lvUpdateInvestigators);
        update_et_investigator_search_filter = update_investigator_list_view.findViewById(R.id.update_et_investigator_search_filter);
        investigatorArrayList = new ArrayList<>();
        updateInvestigatorListAdapter = new UpdateInvestigatorListAdapter(getContext(), investigatorArrayList, this);
        lvUpdateInvestigators.setAdapter(updateInvestigatorListAdapter);
        lvUpdateInvestigators.setLayoutManager(new LinearLayoutManager(getContext()));

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

        final SwipeRefreshLayout update_investigator_pullToRefreshInSearch = update_investigator_list_view.findViewById(R.id.update_investigator_pullToRefreshInSearch);
        update_investigator_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                investigatorArrayList.clear();
                loadDatainListview();
                update_investigator_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        update_et_investigator_search_filter.addTextChangedListener(new TextWatcher() {
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

        swipeDeleteOnRecyclerList(lvUpdateInvestigators, investigatorArrayList);

        return update_investigator_list_view;
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("investigators")
                .orderBy("registration_date", Query.Direction.DESCENDING)
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
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Investigator investigator = d.toObject(Investigator.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                investigatorArrayList.add(investigator);
                            }
                            updateInvestigatorListAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getContext(), "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void filter(String s) {
        investigatorArrayList.clear();
        firebaseFirestore.collection("investigators")
                .orderBy("fullName")
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
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Investigator investigator = d.toObject(Investigator.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                investigatorArrayList.add(investigator);
                            }
                            updateInvestigatorListAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getContext(), R.string.please_type_name_like_hint, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        Bundle viewBundle = new Bundle();
        viewBundle.putString("investigator_id", investigatorArrayList.get(position).getInvestigator_id());
        viewBundle.putString("firstName", investigatorArrayList.get(position).getFirstName());
        viewBundle.putString("lastName", investigatorArrayList.get(position).getLastName());
        viewBundle.putString("parentName", investigatorArrayList.get(position).getParentName());
        viewBundle.putString("fullName", investigatorArrayList.get(position).getFullName());
        viewBundle.putString("birthday", investigatorArrayList.get(position).getBirthday());
        viewBundle.putString("address", investigatorArrayList.get(position).getAddress());
        viewBundle.putString("eyeColor", investigatorArrayList.get(position).getEyeColor());
        viewBundle.putString("hairColor", investigatorArrayList.get(position).getHairColor());
        viewBundle.putString("phy_appearance", investigatorArrayList.get(position).getPhy_appearance());
        viewBundle.putString("urlOfProfile", investigatorArrayList.get(position).getUrlOfProfile());
        viewBundle.putString("registration_date", investigatorArrayList.get(position).getRegistration_date());
        viewBundle.putString("age", investigatorArrayList.get(position).getAge());
        viewBundle.putString("gender", investigatorArrayList.get(position).getGender());
        viewBundle.putString("height", investigatorArrayList.get(position).getHeight());
        viewBundle.putString("weight", investigatorArrayList.get(position).getWeight());
        UpdateInvestigatorFragment updateInvestigatorFragment = new UpdateInvestigatorFragment();
        updateInvestigatorFragment.setArguments(viewBundle);
        loadFragment(updateInvestigatorFragment);
    }

    private void swipeDeleteOnRecyclerList(RecyclerView lvUpdateInvestigators, ArrayList<Investigator> investigatorArrayList) {
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), lvUpdateInvestigators, 200)
        {

            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(),
                        getContext().getString(R.string.delete),
                        40,
                        0,
                        getResources().getColor(R.color.red_fixed),
                        new MyButtonClickListener(){

                            @Override
                            public void onClick(int pos) {
                                String investigatorIdToDelete = investigatorArrayList.get(pos).getInvestigator_id();
                                firebaseFirestore.collection("investigators")
                                        .document(investigatorIdToDelete)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.investigator_is_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                investigatorArrayList.clear();
                                                loadDatainListview();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.error_investigator_failed_to_delete), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }));
            }
        };
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}