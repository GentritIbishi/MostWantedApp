package fiek.unipr.mostwantedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LocationListAdapter extends ArrayAdapter<Person> {
    // constructor for our list view adapter.
    public LocationListAdapter(@NonNull Context context, ArrayList<Person> personArrayList) {
        super(context, 0, personArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.location_item_single, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Person person = getItem(position);

        // initializing our UI components of list view item.
        TextView tv_location_person_fullName = listitemView.findViewById(R.id.tv_location_person_fullName);
        TextView tv_latitude = listitemView.findViewById(R.id.tv_latitude);
        TextView tv_longitude = listitemView.findViewById(R.id.tv_longitude);
        ImageView profile_location_person = listitemView.findViewById(R.id.profile_location_person);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        tv_location_person_fullName.setText(person.getFullName());
        tv_latitude.setText(Double.toString(person.getLatitude()));
        tv_longitude.setText(Double.toString(person.getLongitude()));



        // in below line we are using Picasso to
        // load image from URL in our Image VIew.
        Picasso.get().load(person.getUrlOfProfile()).transform(new CircleTransform()).into(profile_location_person);

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Toast.makeText(getContext(), "Item clicked is : " + person.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }
}