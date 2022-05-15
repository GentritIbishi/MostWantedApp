package fiek.unipr.mostwantedapp.lists;

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

import java.util.ArrayList;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.models.User;
import fiek.unipr.mostwantedapp.update.UpdateMultipleUsers;

public class UserListAdapter extends ArrayAdapter<User> {
    // constructor for our list view adapter.
    public UserListAdapter(@NonNull Context context, ArrayList<User> locationArrayList) {
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
        TextView tv_l_u_fullName = listitemView.findViewById(R.id.tv_l_u_fullName);
        TextView tv_l_u_role = listitemView.findViewById(R.id.tv_l_u_role);
        ImageView profile_user = listitemView.findViewById(R.id.profile_user);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        tv_l_u_fullName.setText(user.getFullName());
        tv_l_u_role.setText(user.getRole());

        // in below line we are using Picasso to
        // load image from URL in our Image VIew.
        Picasso.get().load(user.getUrlOfProfile()).transform(new CircleTransform()).into(profile_user);

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent = new Intent(v.getContext(), UpdateMultipleUsers.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle viewBundle = new Bundle();
                viewBundle.putString("fullName", user.getFullName());
                viewBundle.putString("email", user.getEmail());
                viewBundle.putString("Role", user.getRole());
                if(user.getUrlOfProfile() != null) {
                    viewBundle.putString("urlOfProfile", user.getUrlOfProfile());
                }
                else {
                    viewBundle.putString("urlOfProfile", "noSetURL");
                }
                viewBundle.putString("userID", user.getUserID());
                intent.putExtras(viewBundle);
                v.getContext().startActivity(intent);
            }
        });
        return listitemView;
    }
}
