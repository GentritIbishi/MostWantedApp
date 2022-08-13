package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import fiek.unipr.mostwantedapp.maps.MapsActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.maps.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.Person;

public class LocationListAdapter extends ArrayAdapter<Person> {
    // constructor for our list view adapter.
    public LocationListAdapter(@NonNull Context context, ArrayList<Person> locationArrayList) {
        super(context, 0, locationArrayList);
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
        ImageView profile_location_person = listitemView.findViewById(R.id.profile_location_person);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        tv_location_person_fullName.setText(person.getFullName());



        // in below line we are using Picasso to
        // load image from URL in our Image VIew.
        Picasso.get().load(person.getUrlOfProfile()).transform(new CircleTransform()).into(profile_location_person);

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setClickable(true);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(v.getContext(), MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle viewBundle = new Bundle();
                viewBundle.putString("fullName", person.getFullName());
                viewBundle.putString("status", person.getStatus());
                viewBundle.putString("urlOfProfile", person.getUrlOfProfile());
                viewBundle.putString("latitude", person.getLatitude().toString());
                viewBundle.putString("longitude", person.getLongitude().toString());
                intent.putExtras(viewBundle);
                v.getContext().startActivity(intent);
            }
        });
        return listitemView;
    }

}
