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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.maps.MapsActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.maps.MapsInformerActivity;
import fiek.unipr.mostwantedapp.models.Person;

public class MapsLocationListAdapter extends ArrayAdapter<Person> {

    private TextView tv_location_time_elapsed, tv_location_person_fullName;
    private CircleImageView profile_location_person;
    private String time_elapsed;
    // constructor for our list view adapter.
    public MapsLocationListAdapter(@NonNull Context context, ArrayList<Person> locationArrayList) {
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
        tv_location_person_fullName = listitemView.findViewById(R.id.tv_location_person_fullName);
        tv_location_time_elapsed = listitemView.findViewById(R.id.tv_location_time_elapsed);
        profile_location_person = listitemView.findViewById(R.id.profile_location_person);

        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            tv_location_person_fullName.setText(person.getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (person.getUrlOfProfile() != null && !person.getUrlOfProfile().isEmpty()) {
                Picasso.get().load(person.getUrlOfProfile()).transform(new CircleTransform()).into(profile_location_person);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(person.getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (time_elapsed != null) {
                tv_location_time_elapsed.setText(time_elapsed);
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
            time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            time_elapsed = elapsedSeconds+"s ";
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

}
