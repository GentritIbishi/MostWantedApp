package fiek.unipr.mostwantedapp.fragment.admin.search;

import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.maps.MapsInformerPersonListAdapter;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.activity.maps.admin.MapAdminActivity;
import fiek.unipr.mostwantedapp.models.Person;

public class SearchFragment extends Fragment implements RecyclerViewInterface {

    private Context mContext;
    private View search_admin_view;
    private RecyclerView lvPersons;
    private LinearLayout search_admin_list_view1, search_admin_list_view2;
    private TextView tv_search_admin_userListEmpty;
    private ViewSwitcher search_admin_list_switcher;
    private MapsInformerPersonListAdapter mapsInformerPersonListAdapter;
    private ArrayList<Person> personArrayList;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText admin_search_filter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

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
        search_admin_list_view1 = search_admin_view.findViewById(R.id.search_admin_list_view1);
        search_admin_list_view2 = search_admin_view.findViewById(R.id.search_admin_list_view2);
        tv_search_admin_userListEmpty = search_admin_view.findViewById(R.id.tv_search_admin_userListEmpty);
        search_admin_list_switcher = search_admin_view.findViewById(R.id.search_admin_list_switcher);
        lvPersons = search_admin_view.findViewById(R.id.lvPersons);
        personArrayList = new ArrayList<>();
        mapsInformerPersonListAdapter = new MapsInformerPersonListAdapter(mContext, personArrayList, this);
        lvPersons.setAdapter(mapsInformerPersonListAdapter);
        lvPersons.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection(WANTED_PERSONS)
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
                            if(search_admin_list_switcher.getCurrentView() == search_admin_list_view2){
                                search_admin_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Person person = d.toObject(Person.class);
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }

                            mapsInformerPersonListAdapter.notifyDataSetChanged();
                        } else {
                            if(search_admin_list_switcher.getCurrentView() == search_admin_list_view1){
                                search_admin_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(mContext, mContext.getText(R.string.failed_to_load_data), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void filter(String s) {
        personArrayList.clear();
        firebaseFirestore.collection(WANTED_PERSONS)
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
                            if(search_admin_list_switcher.getCurrentView() == search_admin_list_view2){
                                search_admin_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Person person = d.toObject(Person.class);
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }
                            mapsInformerPersonListAdapter.notifyDataSetChanged();
                        } else {
                            if(search_admin_list_switcher.getCurrentView() == search_admin_list_view1){
                                search_admin_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(mContext, R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(mContext, MapAdminActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle viewBundle = new Bundle();
        viewBundle.putString("fullName", personArrayList.get(position).getFullName());
        viewBundle.putStringArrayList("acts", (ArrayList<String>) personArrayList.get(position).getActs());
        viewBundle.putString("address", personArrayList.get(position).getAddress());
        viewBundle.putString("age", personArrayList.get(position).getAge());
        viewBundle.putString("eyeColor", personArrayList.get(position).getEyeColor());
        viewBundle.putString("hairColor", personArrayList.get(position).getHairColor());
        viewBundle.putString("height", personArrayList.get(position).getHeight());
        viewBundle.putString("phy_appearance", personArrayList.get(position).getPhy_appearance());
        viewBundle.putString("status", personArrayList.get(position).getStatus());
        viewBundle.putString("prize", personArrayList.get(position).getPrize());
        viewBundle.putString("urlOfProfile", personArrayList.get(position).getUrlOfProfile());
        viewBundle.putString("weight", personArrayList.get(position).getWeight());
        intent.putExtras(viewBundle);
        startActivity(intent);
    }

}