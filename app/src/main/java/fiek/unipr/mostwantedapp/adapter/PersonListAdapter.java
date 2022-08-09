package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.maps.MapsActivity;
import fiek.unipr.mostwantedapp.maps.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.update.UpdatePerson;

public class PersonListAdapter extends ArrayAdapter<Person> {
    // constructor for our list view adapter.

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private List<Person> personList = null;
    private ArrayList<Person> arraylist;

    public PersonListAdapter(@NonNull Context context, ArrayList<Person> personArrayList) {
        super(context, 0, personArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.person_item_single, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Person person = getItem(position);

        // initializing our UI components of list view item.
        TextView tv_l_fullName = listitemView.findViewById(R.id.tv_l_fullName);
        TextView tv_l_status = listitemView.findViewById(R.id.tv_l_status);
        ImageView profile_person = listitemView.findViewById(R.id.profile_person);
        ProgressBar lvPerson_progressBar = listitemView.findViewById(R.id.lvPerson_progressBar);

        lvPerson_progressBar.setVisibility(View.VISIBLE);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        tv_l_fullName.setText(person.getFullName());
        tv_l_status.setText(person.getStatus());

        // in below line we are using Picasso to
        // load image from URL in our Image VIew.
        Picasso.get()
                .load(person.getUrlOfProfile())
                .transform(new CircleTransform())
                .into(profile_person);

        lvPerson_progressBar.setVisibility(View.INVISIBLE);

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setClickable(true);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent=new Intent(v.getContext(), MapsInformerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle viewBundle = new Bundle();
                viewBundle.putString("fullName", person.getFullName());
                viewBundle.putString("acts", person.getActs());
                viewBundle.putString("address", person.getAddress());
                viewBundle.putInt("age", person.getAge());
                viewBundle.putString("eyeColor", person.getEyeColor());
                viewBundle.putString("hairColor", person.getHairColor());
                viewBundle.putInt("height", person.getHeight());
                viewBundle.putString("phy_appearance", person.getPhy_appearance());
                viewBundle.putString("status", person.getStatus());
                viewBundle.putString("prize", person.getPrize());
                viewBundle.putString("urlOfProfile", person.getUrlOfProfile());
                viewBundle.putInt("weight", person.getWeight());
                intent.putExtras(viewBundle);
                v.getContext().startActivity(intent);
            }
        });
        return listitemView;
    }


}
