package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.MapsInformerPersonListAdapter;
import fiek.unipr.mostwantedapp.models.Person;

public class SearchFragment extends Fragment {

    private View search_admin_view;
    private ListView lvPersons;
    private MapsInformerPersonListAdapter mapsInformerPersonListAdapter;
    private ArrayList<Person> personArrayList;
    private FirebaseFirestore firebaseFirestore;
    private EditText admin_search_filter;
    private String fullName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        search_admin_view = inflater.inflate(R.layout.fragment_search_admin, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();

        final SwipeRefreshLayout admin_pullToRefreshInSearch = search_admin_view.findViewById(R.id.admin_pullToRefreshInSearch);
        admin_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                personArrayList.clear();
                loadDatainListview();
                admin_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        admin_search_filter.requestFocus();

        admin_search_filter.addTextChangedListener(new TextWatcher() {
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


        return search_admin_view;
    }

    private void InitializeFields() {
        admin_search_filter = search_admin_view.findViewById(R.id.admin_search_filter);
        lvPersons = search_admin_view.findViewById(R.id.lvPersons);
        personArrayList = new ArrayList<>();
        mapsInformerPersonListAdapter = new MapsInformerPersonListAdapter(getActivity().getApplicationContext(), personArrayList);
        lvPersons.setAdapter(mapsInformerPersonListAdapter);
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons")
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
                                personArrayList.add(person);
                            }

                            mapsInformerPersonListAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity().getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity().getApplicationContext(), "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void filter(String s) {
        personArrayList.clear();
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
                                personArrayList.add(person);
                            }
                            mapsInformerPersonListAdapter.notifyDataSetChanged();
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
}