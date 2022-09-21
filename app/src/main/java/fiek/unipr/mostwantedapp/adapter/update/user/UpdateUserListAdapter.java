package fiek.unipr.mostwantedapp.adapter.update.user;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.User;

public class UpdateUserListAdapter extends RecyclerView.Adapter<UpdateUserListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<User> userList;
    private String user_time_elapsed;

    public UpdateUserListAdapter(Context context, List<User> userList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.userList = userList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UpdateUserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context)
                .inflate(R.layout.user_item_single, parent, false);
        return new UpdateUserListViewHolder(userView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateUserListViewHolder holder, int position) {
        try {
            holder.update_user_name.setText(userList.get(position).getFullName());
            holder.update_user_role.setText(userList.get(position).getRole());

            if (userList.get(position).getUrlOfProfile() != null && !userList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(userList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.update_user_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(userList.get(position).getRegister_date_time());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (user_time_elapsed != null) {
                holder.update_user_time_joined.setText(user_time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
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
            SimpleDateFormat sfd = new SimpleDateFormat(DATE_TIME, Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}
