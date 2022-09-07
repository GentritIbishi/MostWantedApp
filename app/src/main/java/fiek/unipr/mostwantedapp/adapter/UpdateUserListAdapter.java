package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.admin.update.UpdateUserFragment;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.User;
import fiek.unipr.mostwantedapp.update.UpdateMultipleUsers;

public class UpdateUserListAdapter extends ArrayAdapter<User> {

    private TextView update_user_time_joined, update_user_role, update_user_name;
    private ImageView update_user_image;
    private String user_time_elapsed;

    // constructor for our list view adapter.
    public UpdateUserListAdapter(@NonNull Context context, ArrayList<User> locationArrayList) {
        super(context, 0, locationArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item_single, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        User user = getItem(position);

        // initializing our UI components of list view item.
        update_user_name = listitemView.findViewById(R.id.update_user_name);
        update_user_role = listitemView.findViewById(R.id.update_user_role);
        update_user_time_joined = listitemView.findViewById(R.id.update_user_time_joined);
        update_user_image = listitemView.findViewById(R.id.update_user_image);


        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            update_user_name.setText(user.getFullName());
            update_user_role.setText(user.getRole());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (user.getUrlOfProfile() != null && !user.getUrlOfProfile().isEmpty()) {
                Picasso.get().load(user.getUrlOfProfile()).transform(new CircleTransform()).into(update_user_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(user.getRegister_date_time());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (user_time_elapsed != null) {
                update_user_time_joined.setText(user_time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle viewBundle = new Bundle();
                viewBundle.putString("address", user.getAddress());
                viewBundle.putInt("balance", user.getBalance());
                viewBundle.putString("email", user.getEmail());
                viewBundle.putString("fullName", user.getFullName());
                viewBundle.putString("gender", user.getGender());
                viewBundle.putString("lastname", user.getLastname());
                viewBundle.putString("name", user.getName());
                viewBundle.putString("parentName", user.getParentName());
                viewBundle.putString("password", user.getPassword());
                viewBundle.putString("personal_number", user.getPersonal_number());
                viewBundle.putString("phone", user.getPhone());
                viewBundle.putString("register_date_time", user.getRegister_date_time());
                viewBundle.putString("role", user.getRole());
                viewBundle.putString("userID", user.getUserID());
                viewBundle.putString("grade", user.getGrade());
                viewBundle.putInt("coins", user.getCoins());
                viewBundle.putInt("balance", user.getBalance());
                if(user.getUrlOfProfile() != null) {
                    viewBundle.putString("urlOfProfile", user.getUrlOfProfile());
                }
                else {
                    viewBundle.putString("urlOfProfile", "noSetURL");
                }
                UpdateUserFragment updateUserFragment = new UpdateUserFragment();
                updateUserFragment.setArguments(viewBundle);
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
            user_time_elapsed = weeks+"w ";
        }else if(elapsedDays != 0) {
            user_time_elapsed = elapsedDays+"d ";
        }else if(elapsedHours != 0){
            user_time_elapsed = elapsedHours+"h ";
        }else if(elapsedMinutes != 0){
            user_time_elapsed = elapsedMinutes+"m ";
        }else if(elapsedSeconds != 0){
            user_time_elapsed = elapsedSeconds+"s ";
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
