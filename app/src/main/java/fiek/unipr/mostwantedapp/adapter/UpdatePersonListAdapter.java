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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.person.UpdatePersonFragment;
import fiek.unipr.mostwantedapp.fragment.admin.update.user.UpdateUserFragment;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.maps.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.Person;
import fiek.unipr.mostwantedapp.models.User;

public class UpdatePersonListAdapter extends ArrayAdapter<Person> {

    private TextView update_person_time_joined, update_person_name;
    private ImageView update_person_image;
    private String person_time_elapsed;

    // constructor for our list view adapter.
    public UpdatePersonListAdapter(@NonNull Context context, ArrayList<Person> personArrayList) {
        super(context, 0, personArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.update_person_item_single, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Person person = getItem(position);

        // initializing our UI components of list view item.
        update_person_name = listitemView.findViewById(R.id.update_person_name);
        update_person_time_joined = listitemView.findViewById(R.id.update_person_time_joined);
        update_person_image = listitemView.findViewById(R.id.update_person_image);

        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            update_person_name.setText(person.getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (person.getUrlOfProfile() != null && !person.getUrlOfProfile().isEmpty()) {
                Picasso.get().load(person.getUrlOfProfile()).transform(new CircleTransform()).into(update_person_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(person.getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (person_time_elapsed != null) {
                update_person_time_joined.setText(person_time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setClickable(true);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Bundle viewBundle = new Bundle();
                viewBundle.putString("personId", person.getPersonId());
                viewBundle.putString("firstName", person.getFirstName());
                viewBundle.putString("lastName", person.getLastName());
                viewBundle.putString("parentName", person.getParentName());
                viewBundle.putString("fullName", person.getFullName());
                viewBundle.putString("birthday", person.getBirthday());
                viewBundle.putString("gender", person.getGender());
                viewBundle.putString("address", person.getAddress());
                viewBundle.putString("age", person.getAge());
                viewBundle.putString("eyeColor", person.getEyeColor());
                viewBundle.putString("hairColor", person.getHairColor());
                viewBundle.putString("height", person.getHeight());
                viewBundle.putString("weight", person.getWeight());
                viewBundle.putString("phy_appearance", person.getPhy_appearance());
                viewBundle.putStringArrayList("acts", (ArrayList<String>) person.getActs());
                viewBundle.putDouble("latitude", person.getLatitude());
                viewBundle.putDouble("longitude", person.getLongitude());
                viewBundle.putString("prize", person.getPrize());
                viewBundle.putString("status", person.getStatus());
                viewBundle.putString("registration_date", person.getRegistration_date());
                viewBundle.putString("urlOfProfile", person.getUrlOfProfile());
                UpdatePersonFragment updatePersonFragment = new UpdatePersonFragment();
                updatePersonFragment.setArguments(viewBundle);
                loadFragment(updatePersonFragment);
            }
        });
        return listitemView;
    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        long weeks = elapsedDays/7;

        if(weeks != 0){
            person_time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            person_time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            person_time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            person_time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            person_time_elapsed = elapsedSeconds+"s ";
        }

    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}
