package fiek.unipr.mostwantedapp.lists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import fiek.unipr.mostwantedapp.adapter.PersonListAdapter;
import fiek.unipr.mostwantedapp.models.Person;

public class LimitPersonActivity extends AppCompatActivity {

    // creating a variable for our list view,
    // arraylist and firebase Firestore.
    ListView limit_lvPersons;
    ArrayList<Person> limit_personArrayList;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_person);

        // below line is use to initialize our variables
        limit_lvPersons = findViewById(R.id.limit_lvPersons);
        limit_personArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        firebaseFirestore.collection("wanted_persons")
                .limitToLast(3)
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
                                limit_personArrayList.add(person);
                            }
                            // after that we are passing our array list to our adapter class.
                            PersonListAdapter adapter = new PersonListAdapter(LimitPersonActivity.this, limit_personArrayList);

                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                            limit_lvPersons.setAdapter(adapter);
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(LimitPersonActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(LimitPersonActivity.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}