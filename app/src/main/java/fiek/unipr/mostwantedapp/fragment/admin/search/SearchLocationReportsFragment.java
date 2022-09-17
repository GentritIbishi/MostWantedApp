package fiek.unipr.mostwantedapp.fragment.admin.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.maps.MapsLocationListAdapter;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.maps.admin.MapsActivity;
import fiek.unipr.mostwantedapp.models.Person;

public class SearchLocationReportsFragment extends Fragment implements RecyclerViewInterface {

    private View locations_persons_view;
    private RecyclerView lvLocationPersonsFragment;
    private MapsLocationListAdapter mapsLocationListAdapter;
    private ArrayList<Person> locationArrayList;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText location_search_filter;
    private String fullName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locations_persons_view = inflater.inflate(R.layout.fragment_search_location_reports, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout location_pullToRefreshInSearch = locations_persons_view.findViewById(R.id.location_pullToRefreshInSearch);
        location_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                locationArrayList.clear();
                loadDatainListview();
                location_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        location_search_filter.requestFocus();

        location_search_filter.addTextChangedListener(new TextWatcher() {
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

        return locations_persons_view;
    }

    private void InitializeFields() {
        lvLocationPersonsFragment = locations_persons_view.findViewById(R.id.lvLocationPersonsFragment);
        location_search_filter = locations_persons_view.findViewById(R.id.location_search_filter);
        locationArrayList = new ArrayList<>();
        mapsLocationListAdapter = new MapsLocationListAdapter(getContext(), locationArrayList, this);
        lvLocationPersonsFragment.setAdapter(mapsLocationListAdapter);
        lvLocationPersonsFragment.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons").get()
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
                                Person person = d.toObject(Person.class);

                                fullName = person.getFullName();

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                locationArrayList.add(person);
                            }

                            mapsLocationListAdapter.notifyDataSetChanged();
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
        locationArrayList.clear();
        firebaseFirestore.collection("wanted_persons")
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
                                Person person = d.toObject(Person.class);

                                fullName = person.getFullName();

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                locationArrayList.add(person);
                            }
                            mapsLocationListAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity().getApplicationContext(), R.string.please_type_name_like_hint, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        Intent intent =new Intent(getContext(), MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle viewBundle = new Bundle();
        viewBundle.putString("fullName", locationArrayList.get(position).getFullName());
        viewBundle.putString("status", locationArrayList.get(position).getStatus());
        viewBundle.putString("urlOfProfile", locationArrayList.get(position).getUrlOfProfile());
        viewBundle.putString("latitude", locationArrayList.get(position).getLatitude().toString());
        viewBundle.putString("longitude", locationArrayList.get(position).getLongitude().toString());
        intent.putExtras(viewBundle);
        startActivity(intent);
    }
}