package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import fiek.unipr.mostwantedapp.adapter.LocationListAdapter;
import fiek.unipr.mostwantedapp.models.Person;

public class LocationReportsFragment extends Fragment {

    // creating a variable for our list view,
    // arraylist and firebase Firestore.
    View locations_persons_view;
    ListView lvLocationOfPersons;
    ArrayList<Person> locationArrayList;
    FirebaseFirestore firebaseFirestore;

    public LocationReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        locations_persons_view = inflater.inflate(R.layout.fragment_location_reports, container, false);

        // below line is use to initialize our variables
        lvLocationOfPersons = locations_persons_view.findViewById(R.id.lvLocationOfPersons);
        locationArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

        return locations_persons_view;
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

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                locationArrayList.add(person);
                            }
                            // after that we are passing our array list to our adapter class.
                            LocationListAdapter adapter = new LocationListAdapter(getContext(), locationArrayList);

                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                            lvLocationOfPersons.setAdapter(adapter);
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
}