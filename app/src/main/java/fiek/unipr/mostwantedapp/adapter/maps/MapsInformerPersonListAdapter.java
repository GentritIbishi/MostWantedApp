package fiek.unipr.mostwantedapp.adapter.maps;

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
import fiek.unipr.mostwantedapp.helpers.CircleTransform;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Person;

public class MapsInformerPersonListAdapter extends RecyclerView.Adapter<MapsInformerListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Person> personList;
    private String person_time_elapsed;

    public MapsInformerPersonListAdapter(Context context, List<Person> personList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.personList = personList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MapsInformerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context)
                .inflate(R.layout.person_item_single, parent, false);
        return new MapsInformerListViewHolder(userView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsInformerListViewHolder holder, int position) {
        try {
            holder.lvPerson_progressBar.setVisibility(View.VISIBLE);
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.tv_l_fullName.setText(personList.get(position).getFullName());
            holder.tv_l_status.setText(personList.get(position).getStatus());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (personList.get(position).getUrlOfProfile() != null && !personList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get()
                        .load(personList.get(position).getUrlOfProfile())
                        .transform(new CircleTransform())
                        .into(holder.profile_person);
            }else {
                holder.profile_person.setImageResource(R.drawable.ic_profile_picture_default);
            }
            holder.lvPerson_progressBar.setVisibility(View.INVISIBLE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

            Date start_date = simpleDateFormat.parse(personList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(getTimeDate());
            printDifference(start_date, end_date);

            if (person_time_elapsed != null) {
                holder.person_search_time_joined.setText(person_time_elapsed);
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
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

}
