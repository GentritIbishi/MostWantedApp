package fiek.unipr.mostwantedapp.fragment.admin.update.person;

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
import android.widget.ListView;
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
import fiek.unipr.mostwantedapp.adapter.UpdatePersonListAdapter;
import fiek.unipr.mostwantedapp.adapter.UpdateUserListAdapter;
import fiek.unipr.mostwantedapp.helpers.MyButtonClickListener;
import fiek.unipr.mostwantedapp.helpers.MySwipeHelper;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Investigator;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.User;

public class UpdatePersonListFragment extends Fragment implements RecyclerViewInterface {

    private View update_person_view;
    private RecyclerView lvUpdatePersons;
    private ArrayList<Person> personArrayList;
    private UpdatePersonListAdapter updatePersonListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText update_et_person_search_filter;

    public UpdatePersonListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_person_view = inflater.inflate(R.layout.fragment_update_person_list, container, false);

        // initializing our variable for firebase
        // firestore and getting its instance.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // below line is use to initialize our variables
        lvUpdatePersons = update_person_view.findViewById(R.id.lvUpdatePersons);
        update_et_person_search_filter = update_person_view.findViewById(R.id.update_et_person_search_filter);
        personArrayList = new ArrayList<>();
        updatePersonListAdapter = new UpdatePersonListAdapter(getContext(), personArrayList, this);
        lvUpdatePersons.setAdapter(updatePersonListAdapter);
        lvUpdatePersons.setLayoutManager(new LinearLayoutManager(getContext()));

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

        final SwipeRefreshLayout update_person_pullToRefreshInSearch = update_person_view.findViewById(R.id.update_person_pullToRefreshInSearch);
        update_person_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                personArrayList.clear();
                loadDatainListview();
                update_person_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        update_et_person_search_filter.addTextChangedListener(new TextWatcher() {
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

        swipeDeleteOnRecyclerList(lvUpdatePersons, personArrayList);

        return update_person_view;
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons")
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
                                Person person = d.toObject(Person.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }
                            updatePersonListAdapter.notifyDataSetChanged();
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

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }
                            updatePersonListAdapter.notifyDataSetChanged();
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
        viewBundle.putString("personId", personArrayList.get(position).getPersonId());
        viewBundle.putString("firstName", personArrayList.get(position).getFirstName());
        viewBundle.putString("lastName", personArrayList.get(position).getLastName());
        viewBundle.putString("parentName", personArrayList.get(position).getParentName());
        viewBundle.putString("fullName", personArrayList.get(position).getFullName());
        viewBundle.putString("birthday", personArrayList.get(position).getBirthday());
        viewBundle.putString("gender", personArrayList.get(position).getGender());
        viewBundle.putString("address", personArrayList.get(position).getAddress());
        viewBundle.putString("age", personArrayList.get(position).getAge());
        viewBundle.putString("eyeColor", personArrayList.get(position).getEyeColor());
        viewBundle.putString("hairColor", personArrayList.get(position).getHairColor());
        viewBundle.putString("height", personArrayList.get(position).getHeight());
        viewBundle.putString("weight", personArrayList.get(position).getWeight());
        viewBundle.putString("phy_appearance", personArrayList.get(position).getPhy_appearance());
        viewBundle.putStringArrayList("acts", (ArrayList<String>) personArrayList.get(position).getActs());
        viewBundle.putDouble("latitude", personArrayList.get(position).getLatitude());
        viewBundle.putDouble("longitude", personArrayList.get(position).getLongitude());
        viewBundle.putString("prize", personArrayList.get(position).getPrize());
        viewBundle.putString("status", personArrayList.get(position).getStatus());
        viewBundle.putString("registration_date", personArrayList.get(position).getRegistration_date());
        viewBundle.putString("urlOfProfile", personArrayList.get(position).getUrlOfProfile());
        UpdatePersonFragment updatePersonFragment = new UpdatePersonFragment();
        updatePersonFragment.setArguments(viewBundle);
        loadFragment(updatePersonFragment);
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void swipeDeleteOnRecyclerList(RecyclerView lvUpdatePersons, ArrayList<Person> personArrayList) {
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), lvUpdatePersons, 200)
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
                                String personIdToDelete = personArrayList.get(pos).getPersonId();
                                firebaseFirestore.collection("wanted_persons")
                                        .document(personIdToDelete)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.person_is_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                personArrayList.clear();
                                                loadDatainListview();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.error_person_failed_to_delete), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }));
            }
        };
    }

}