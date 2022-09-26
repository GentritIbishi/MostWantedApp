package fiek.unipr.mostwantedapp.adapter.maps;

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

import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.models.Person;

public class MapsLocationListAdapter extends RecyclerView.Adapter<MapsLocationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Person> personLocationList;
    private String time_elapsed;

    public MapsLocationListAdapter(Context context, List<Person> personLocationList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.personLocationList = personLocationList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MapsLocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.location_item_single, parent, false);
        return new MapsLocationListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsLocationListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.tv_location_person_fullName.setText(personLocationList.get(position).getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (personLocationList.get(position).getUrlOfProfile() != null && !personLocationList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(personLocationList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.profile_location_person);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(personLocationList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            time_elapsed = DateHelper.printDifference(start_date, end_date);

            if (time_elapsed != null) {
                holder.tv_location_time_elapsed.setText(time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return personLocationList.size();
    }

}
