package fiek.unipr.mostwantedapp.fragment.user;

import static fiek.unipr.mostwantedapp.utils.Constants.FAKE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.NA;
import static fiek.unipr.mostwantedapp.utils.Constants.UNVERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.maps.MapsInformerPersonListAdapter;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.ContextHelper;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.activity.maps.user.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private Context mContext;
    public String YOUR_REPORT_IN_DATETIME_TRANSLATABLE;
    public String HAS_NEW_STATUS_RIGHT_NOW;
    public String STATUS_OF_REPORT_HAS_CHANGED_TO;
    private View home_fragment_view;
    private RecyclerView home_lvPersons;
    private LinearLayout home_user_list_view1, home_user_list_view2;
    private TextView tv_home_user_userListEmpty;
    private ViewSwitcher home_user_list_switcher;
    private MapsInformerPersonListAdapter mapsInformerPersonListAdapter;
    private ArrayList<Person> personArrayList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String userID, fullName, urlOfProfile, name, grade;
    private Double balance;

    private TextView user_home_tv_num_report_verified, user_home_tv_num_report_unverified, user_home_tv_num_report_fake, user_home_tv_gradeOfUser;
    private TextView user_rightNowDateTime, user_hiDashboard, user_tv_balance;
    private CircleImageView user_imageOfDashboard;
    private PieChart user_home_pieChart;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();
        YOUR_REPORT_IN_DATETIME_TRANSLATABLE = String.valueOf(mContext.getText(R.string.your_report_in_datetime));
        HAS_NEW_STATUS_RIGHT_NOW = String.valueOf(mContext.getText(R.string.has_new_status_right_now));
        STATUS_OF_REPORT_HAS_CHANGED_TO = String.valueOf(mContext.getText(R.string.status_of_report_has_changed_to));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            if(ContextHelper.checkContext(getContext())){
                loadInfoFromFirebase();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        home_fragment_view = inflater.inflate(R.layout.fragment_home_user, container, false);
        mContext = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        InitializeFields();
        loadDataInListView();

        final SwipeRefreshLayout user_home_pullToRefreshProfileDashboard = home_fragment_view.findViewById(R.id.user_home_pullToRefreshProfileDashboard);
        user_home_pullToRefreshProfileDashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload current fragment
                if(getContext() != null)
                {
                    personArrayList.clear();
                    loadDataInListView();

                    loadInfoFromFirebase();

                    setupPieChart();
                    setPieChart();
                }

                user_home_pullToRefreshProfileDashboard.setRefreshing(false);
            }
        });

        user_rightNowDateTime.setText(DateHelper.getDateTimeStyle());
        getGrade();
        setupPieChart();
        setPieChart();

        return home_fragment_view;
    }


    @Override
    public void onAttach(@NonNull Context mContext) {
        this.mContext = mContext;
        super.onAttach(mContext);
    }

    private void InitializeFields() {
        home_lvPersons = home_fragment_view.findViewById(R.id.home_lvPersons);
        personArrayList = new ArrayList<>();
        mapsInformerPersonListAdapter = new MapsInformerPersonListAdapter(getContext(), personArrayList, this);
        home_lvPersons.setAdapter(mapsInformerPersonListAdapter);
        home_lvPersons.setLayoutManager(new LinearLayoutManager(getContext()));
        user_rightNowDateTime = home_fragment_view.findViewById(R.id.user_rightNowDateTime);
        user_hiDashboard = home_fragment_view.findViewById(R.id.user_hiDashboard);
        user_imageOfDashboard = home_fragment_view.findViewById(R.id.user_imageOfDashboard);
        user_home_tv_num_report_verified = home_fragment_view.findViewById(R.id.user_home_tv_num_report_verified);
        user_home_tv_num_report_unverified = home_fragment_view.findViewById(R.id.user_home_tv_num_report_unverified);
        user_home_tv_num_report_fake = home_fragment_view.findViewById(R.id.user_home_tv_num_report_fake);
        user_home_tv_gradeOfUser = home_fragment_view.findViewById(R.id.user_home_tv_gradeOfUser);
        user_home_pieChart = home_fragment_view.findViewById(R.id.user_home_pieChart);
        user_tv_balance = home_fragment_view.findViewById(R.id.user_tv_balance);
        home_user_list_switcher = home_fragment_view.findViewById(R.id.home_user_list_switcher);
        home_user_list_view1 = home_fragment_view.findViewById(R.id.home_user_list_view1);
        home_user_list_view2 = home_fragment_view.findViewById(R.id.home_user_list_view2);
        tv_home_user_userListEmpty = home_fragment_view.findViewById(R.id.tv_home_user_userListEmpty);
    }

    private void getGrade() {
        if(CheckInternet.isConnected(mContext)){
            firebaseFirestore.collection(USERS)
                    .document(firebaseUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                            {
                                grade = task.getResult().getString("grade");
                                user_home_tv_gradeOfUser.setText(grade);
                            }
                        }
                    });
        }
    }

    private void loadDataInListView() {
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
                            if(home_user_list_switcher.getCurrentView() == home_user_list_view2){
                                home_user_list_switcher.showNext();
                            }
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Person person = d.toObject(Person.class);

                                if(person != null)
                                {
                                    fullName = person.getFullName();
                                }

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                personArrayList.add(person);
                            }

                            mapsInformerPersonListAdapter.notifyDataSetChanged();
                        } else {
                            if(home_user_list_switcher.getCurrentView() == home_user_list_view1){
                                home_user_list_switcher.showNext();
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

    //function that count all locations_reports: VERIFIED, UNVERIFIED, FAKE
    private void setPieChart() {
        if(CheckInternet.isConnected(mContext)) {
            firebaseFirestore.collection(LOCATION_REPORTS)
                    .whereEqualTo("uID", firebaseAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int newCountVERIFIED = 0;
                            int newCountUNVERIFIED = 0;
                            int newCountFAKE = 0;

                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                                String status = doc.getString("status");

                                if(status != null && !StringHelper.empty(status))
                                {
                                    if (status.equals(VERIFIED)) {
                                        newCountVERIFIED++;
                                    }

                                    if (status.equals(UNVERIFIED)) {
                                        newCountUNVERIFIED++;
                                    }

                                    if (status.equals(FAKE)) {
                                        newCountFAKE++;
                                    }
                                }
                            }

                            //set counters in card view
                            user_home_tv_num_report_verified.setText(String.valueOf(newCountVERIFIED));
                            user_home_tv_num_report_unverified.setText(String.valueOf(newCountUNVERIFIED));
                            user_home_tv_num_report_fake.setText(String.valueOf(newCountFAKE));

                            loadPieChartData(newCountVERIFIED, newCountUNVERIFIED, newCountFAKE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(), R.string.error_no_internet_connection_check_wifi_or_mobile_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPieChartData(int newCountVERIFIED, int newCountUNVERIFIED, int newCountFAKE) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        try {
            String reports_verified = String.valueOf(mContext.getText(R.string.reports_verified));
            String reports_unverified = String.valueOf(mContext.getText(R.string.reports_pending));
            String reports_fake = String.valueOf(mContext.getText(R.string.reports_fake));

            entries.add(new PieEntry((float) newCountVERIFIED, reports_verified));
            entries.add(new PieEntry((float) newCountUNVERIFIED, reports_unverified));
            entries.add(new PieEntry((float) newCountFAKE, reports_fake));
        }catch (Exception e){
            e.getMessage();
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for(int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for(int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(user_home_pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        user_home_pieChart.setData(data);
        user_home_pieChart.invalidate();
        user_home_pieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void setupPieChart() {
        user_home_pieChart.setDrawHoleEnabled(true);
        user_home_pieChart.setUsePercentValues(true);
        user_home_pieChart.setEntryLabelTextSize(12);
        user_home_pieChart.setEntryLabelColor(Color.BLACK);
        user_home_pieChart.setCenterText(getText(R.string.report));
        user_home_pieChart.setCenterTextSize(16f);
        user_home_pieChart.getDescription().setEnabled(false);

        Legend l = user_home_pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadInfoFromFirebase() {
        if(CheckInternet.isConnected(mContext)){
            documentReference = firebaseFirestore.collection(USERS).document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        fullName = task.getResult().getString("fullName");
                        name = task.getResult().getString("name");
                        urlOfProfile = task.getResult().getString("urlOfProfile");
                        balance = task.getResult().getDouble("balance");
                        if(balance != null){
                                user_tv_balance.setText(String.valueOf(balance));
                        }else {
                            user_tv_balance.setText(NA);
                        }

                        //set Image, verified if is email verified, name
                        //setVerifiedBadge(firebaseAuth.getCurrentUser());
                        if(urlOfProfile != null){
                            Picasso.get().load(urlOfProfile).into(user_imageOfDashboard);
                        }

                        if(fullName != null){
                            user_hiDashboard.setText(mContext.getText(R.string.hi)+" "+name);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(getContext(), MapsInformerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

}