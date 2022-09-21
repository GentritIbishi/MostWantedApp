package fiek.unipr.mostwantedapp.adapter.update.person;

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

public class UpdatePersonListAdapter extends RecyclerView.Adapter<UpdatePersonListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Person> personList;
    private String time_elapsed;

    public UpdatePersonListAdapter(Context context, List<Person> personList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.personList = personList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UpdatePersonListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.update_person_item_single, parent, false);
        return new UpdatePersonListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatePersonListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.update_person_name.setText(personList.get(position).getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (personList.get(position).getUrlOfProfile() != null && !personList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(personList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.update_person_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(personList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            DateHelper.printDifference(start_date, end_date, time_elapsed);

            if (time_elapsed != null) {
                holder.update_person_time_joined.setText(time_elapsed);
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
