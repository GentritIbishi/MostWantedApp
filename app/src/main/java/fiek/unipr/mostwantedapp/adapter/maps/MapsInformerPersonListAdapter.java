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

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Person;

public class MapsInformerPersonListAdapter extends RecyclerView.Adapter<MapsInformerListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Person> personList;
    private String time_elapsed;

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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(personList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            DateHelper.printDifference(start_date, end_date, time_elapsed);

            if (time_elapsed != null) {
                holder.person_search_time_joined.setText(time_elapsed);
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

}
