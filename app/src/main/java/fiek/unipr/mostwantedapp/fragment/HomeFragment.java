package fiek.unipr.mostwantedapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import fiek.unipr.mostwantedapp.lists.PersonActivity;
import fiek.unipr.mostwantedapp.lists.PersonListAdapter;
import fiek.unipr.mostwantedapp.models.Person;

public class HomeFragment extends Fragment {

    private View group_fragment_view;
    private ListView lvPersons;
    private PersonListAdapter personListAdapter;
    private ArrayList<Person> personArrayList;
    private FirebaseFirestore firebaseFirestore;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        group_fragment_view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        InitializeFields();
        loadDatainListview();
        return group_fragment_view;
    }

    private void InitializeFields() {
        lvPersons = group_fragment_view.findViewById(R.id.lvPersons);
        personArrayList = new ArrayList<>();
        personListAdapter = new PersonListAdapter(getActivity().getApplicationContext(), personArrayList);
        lvPersons.setAdapter(personListAdapter);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        setRetainInstance( true );

        LayoutInflater.from( getActivity().getApplicationContext() );
        lvPersons.setDividerHeight( 1 );
        lvPersons.setOnCreateContextMenuListener( this );
        lvPersons.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });
        lvPersons.setFocusable( true );
        lvPersons.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
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
//                            // after that we are passing our array list to our adapter class.
//                            PersonListAdapter adapter = new PersonListAdapter(getContext(), personArrayList);
//
//                            // after passing this array list to our adapter
//                            // class we are setting our adapter to our list view.
//                            lvPersons.setAdapter(adapter);
                            personListAdapter.notifyDataSetChanged();
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

}