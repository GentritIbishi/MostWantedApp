package fiek.unipr.mostwantedapp.fragment.anonymous;

import static android.view.View.GONE;

import static fiek.unipr.mostwantedapp.utils.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.utils.Constants.LOGIN_HISTORY;
import static fiek.unipr.mostwantedapp.utils.Constants.PHONE_USER;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.maps.user.MapUserActivity;
import fiek.unipr.mostwantedapp.adapter.maps.MapsInformerPersonListAdapter;
import fiek.unipr.mostwantedapp.models.LoginHistory;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class SearchFragment extends Fragment implements RecyclerViewInterface {

    private View view;
    private TextView tv_anonymous_helper, tv_anonymous_hello;
    private TextInputEditText anonymous_search_filter;
    private RecyclerView anonymous_lvPersons;
    private LinearLayout search_anonymous_list_view1, search_anonymous_list_view2;
    private ViewSwitcher search_anonymous_list_switcher;
    private MapsInformerPersonListAdapter mapsInformerAnonymousPersonListAdapter;
    private ArrayList<Person> personArrayList;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String userID, fullName;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initializeFields();
        loadDatainListview();
        setInformation();

        anonymous_search_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0)
                {
                    search_anonymous_list_switcher.setVisibility(View.VISIBLE);
                    tv_anonymous_helper.setText(getContext().getText(R.string.now_tap_on_desired_person));
                    tv_anonymous_helper.setMinLines(1);
                    filter(charSequence.toString());
                }else if(charSequence.length() == 0)
                {
                    search_anonymous_list_switcher.setVisibility(View.GONE);
                    tv_anonymous_helper.setMinLines(2);
                    tv_anonymous_helper.setText(getContext().getText(R.string.tap_on_search_and_enter_full_name_of_person_that_you_want_to_report));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        onBackPressed();

        return view;
    }

    private void setInformation() {
        if(firebaseUser != null)
        {
            if(firebaseUser.isAnonymous()){
                LoginHistory loginHistory = new LoginHistory(firebaseAuth.getUid(), ANONYMOUS, ANONYMOUS, ANONYMOUS, DateHelper.getDateTime());
                setLoginHistoryAnonymous(loginHistory);
                tv_anonymous_hello.setText(getContext().getText(R.string.hi)+" "+"Anonymous");
            }else if(!StringHelper.empty(firebaseAuth.getCurrentUser().getPhoneNumber())){
                LoginHistory loginHistory = new LoginHistory(firebaseAuth.getCurrentUser().getUid(), PHONE_USER, PHONE_USER, PHONE_USER, DateHelper.getDateTime());
                setLoginHistoryPhone(loginHistory);
                tv_anonymous_hello.setText(getContext().getText(R.string.hi)+" "+firebaseAuth.getCurrentUser().getPhoneNumber());
            }
        }
    }

    public void setLoginHistoryPhone(LoginHistory loginHistory) {

        documentReference = firebaseFirestore.collection(LOGIN_HISTORY)
                .document(loginHistory.getUserID()).collection(loginHistory.getRole())
                .document(firebaseAuth.getUid()+" "+loginHistory.getDate_time());
        documentReference.set(loginHistory);
    }

    private void setLoginHistoryAnonymous(LoginHistory loginHistory) {
        firebaseFirestore.collection(LOGIN_HISTORY)
                .document(loginHistory.getUserID()).collection(loginHistory.getRole())
                .document(loginHistory.getUserID()+" "+loginHistory.getDate_time()).set(loginHistory);
    }

    private void initializeFields() {
        anonymous_search_filter = view.findViewById(R.id.anonymous_search_filter);
        tv_anonymous_hello = view.findViewById(R.id.tv_anonymous_hello);
        tv_anonymous_helper = view.findViewById(R.id.tv_anonymous_helper);
        search_anonymous_list_view1 = view.findViewById(R.id.search_anonymous_list_view1);
        search_anonymous_list_view2 = view.findViewById(R.id.search_anonymous_list_view2);
        search_anonymous_list_switcher = view.findViewById(R.id.search_anonymous_list_switcher);
        anonymous_lvPersons = view.findViewById(R.id.anonymous_lvPersons);
        personArrayList = new ArrayList<>();
        mapsInformerAnonymousPersonListAdapter = new MapsInformerPersonListAdapter(getContext(), personArrayList, this);
        anonymous_lvPersons.setAdapter(mapsInformerAnonymousPersonListAdapter);
        anonymous_lvPersons.setLayoutManager(new LinearLayoutManager(getContext()));
        search_anonymous_list_switcher.setVisibility(GONE);
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection(WANTED_PERSONS)
                .limit(5)
                .orderBy("registration_date", Query.Direction.ASCENDING)
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
                            if(search_anonymous_list_switcher.getCurrentView() == search_anonymous_list_view2){
                                search_anonymous_list_switcher.showNext();
                            }
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

                            mapsInformerAnonymousPersonListAdapter.notifyDataSetChanged();
                        } else {
                            if(search_anonymous_list_switcher.getCurrentView() == search_anonymous_list_view1){
                                search_anonymous_list_switcher.showNext();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getContext(), getContext().getText(R.string.failed_to_load_data), Toast.LENGTH_SHORT).show();
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
                            if(search_anonymous_list_switcher.getCurrentView() == search_anonymous_list_view2){
                                search_anonymous_list_switcher.showNext();
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
                            mapsInformerAnonymousPersonListAdapter.notifyDataSetChanged();
                        } else {
                            if(search_anonymous_list_switcher.getCurrentView() == search_anonymous_list_view1){
                                search_anonymous_list_switcher.showNext();
                            }
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
        Intent intent=new Intent(getContext(), MapUserActivity.class);
        Bundle viewBundle = new Bundle();
        viewBundle.putString("personId", personArrayList.get(position).getPersonId());
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

    private void onBackPressed() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    firebaseAuth.signOut();
                    return true;
                }
                return false;
            }
        });
    }

}