package fiek.unipr.mostwantedapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import fiek.unipr.mostwantedapp.lists.PersonListAdapter;
import fiek.unipr.mostwantedapp.models.Person;

public class PersonFragment extends Fragment {

    View person_view;
    ListView lvtestPerson;
    ArrayAdapter<Person> arrayAdapter;
    ArrayList<Person> personArrayList;

    FirebaseFirestore firebaseFirestore;

    public PersonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        person_view = inflater.inflate(R.layout.fragment_person, container, false);
        InitializeFields();

        return person_view;
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
                                personArrayList.add(person);
                            }

                            arrayAdapter.notifyDataSetChanged();
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

    private void InitializeFields() {
        lvtestPerson = (ListView) person_view.findViewById(R.id.lvtestPerson);
        personArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<Person>(getContext(), R.layout.person_item_single, personArrayList);
        lvtestPerson.setAdapter(arrayAdapter);
    }
}