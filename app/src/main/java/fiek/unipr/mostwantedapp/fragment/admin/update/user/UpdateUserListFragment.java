package fiek.unipr.mostwantedapp.fragment.admin.update.user;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import fiek.unipr.mostwantedapp.adapter.UpdateUserListAdapter;
import fiek.unipr.mostwantedapp.helpers.MyButtonClickListener;
import fiek.unipr.mostwantedapp.helpers.MySwipeHelper;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.User;

public class UpdateUserListFragment extends Fragment implements RecyclerViewInterface {

    private View update_user_fragment;
    private RecyclerView lvUsers;
    private ArrayList<User> userArrayList;
    private UpdateUserListAdapter userListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText update_et_user_search_filter;

    public UpdateUserListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_user_fragment = inflater.inflate(R.layout.fragment_update_user_list, container, false);
        // initializing our variable for firebase
        // firestore and getting its instance.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // below line is use to initialize our variables
        lvUsers = update_user_fragment.findViewById(R.id.lvUsers);
        update_et_user_search_filter = update_user_fragment.findViewById(R.id.update_et_user_search_filter);
        userArrayList = new ArrayList<>();
        userListAdapter = new UpdateUserListAdapter(getContext(), userArrayList, this);
        lvUsers.setAdapter(userListAdapter);
        lvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

        final SwipeRefreshLayout update_user_pullToRefreshInSearch = update_user_fragment.findViewById(R.id.update_user_pullToRefreshInSearch);
        update_user_pullToRefreshInSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                userArrayList.clear();
                loadDatainListview();
                update_user_pullToRefreshInSearch.setRefreshing(false);
            }
        });

        update_et_user_search_filter.addTextChangedListener(new TextWatcher() {
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

        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), lvUsers, 200)
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
                                String userIDToDelete = userArrayList.get(pos).getUserID();
                                firebaseFirestore.collection("users")
                                        .document(userIDToDelete)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.user_is_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                userArrayList.clear();
                                                loadDatainListview();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), getContext().getText(R.string.error_user_failed_to_delete), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }));
            }
        };


        return update_user_fragment;
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("users")
                .orderBy("register_date_time", Query.Direction.DESCENDING)
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
                                User user = d.toObject(User.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                userArrayList.add(user);
                            }
                            userListAdapter.notifyDataSetChanged();
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
        userArrayList.clear();
        firebaseFirestore.collection("users")
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
                                User user = d.toObject(User.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                userArrayList.add(user);
                            }
                            userListAdapter.notifyDataSetChanged();
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
        viewBundle.putString("userID", userArrayList.get(position).getUserID());
        viewBundle.putString("address", userArrayList.get(position).getAddress());
        viewBundle.putString("balance", userArrayList.get(position).getBalance());
        viewBundle.putString("email", userArrayList.get(position).getEmail());
        viewBundle.putString("fullName", userArrayList.get(position).getFullName());
        viewBundle.putString("gender", userArrayList.get(position).getGender());
        viewBundle.putString("lastname", userArrayList.get(position).getLastname());
        viewBundle.putString("name", userArrayList.get(position).getName());
        viewBundle.putString("parentName", userArrayList.get(position).getParentName());
        viewBundle.putString("password", userArrayList.get(position).getPassword());
        viewBundle.putString("personal_number", userArrayList.get(position).getPersonal_number());
        viewBundle.putString("phone", userArrayList.get(position).getPhone());
        viewBundle.putString("register_date_time", userArrayList.get(position).getRegister_date_time());
        viewBundle.putString("role", userArrayList.get(position).getRole());
        viewBundle.putString("userID", userArrayList.get(position).getUserID());
        viewBundle.putString("grade", userArrayList.get(position).getGrade());
        viewBundle.putString("coins", userArrayList.get(position).getCoins());
        viewBundle.putString("balance", userArrayList.get(position).getBalance());
        viewBundle.putBoolean("emailVerified", userArrayList.get(position).getEmailVerified());
        if(userArrayList.get(position).getUrlOfProfile() != null) {
            viewBundle.putString("urlOfProfile", userArrayList.get(position).getUrlOfProfile());
        }
        else {
            viewBundle.putString("urlOfProfile", "noSetURL");
        }
        UpdateUserFragment updateUserFragment = new UpdateUserFragment();
        updateUserFragment.setArguments(viewBundle);
        loadFragment(updateUserFragment);
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}