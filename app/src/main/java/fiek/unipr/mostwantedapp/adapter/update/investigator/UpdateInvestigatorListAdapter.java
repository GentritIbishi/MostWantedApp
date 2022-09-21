package fiek.unipr.mostwantedapp.adapter.update.investigator;

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
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Investigator;

public class UpdateInvestigatorListAdapter extends RecyclerView.Adapter<UpdateInvestigatorListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Investigator> investigatorList;
    private String time_elapsed;

    public UpdateInvestigatorListAdapter(Context context, List<Investigator> investigatorList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.investigatorList = investigatorList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UpdateInvestigatorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context)
                .inflate(R.layout.update_investigator_item_single, parent, false);
        return new UpdateInvestigatorListViewHolder(userView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateInvestigatorListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.update_investigator_name.setText(investigatorList.get(position).getFullName());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.
            if (investigatorList.get(position).getUrlOfProfile() != null && !investigatorList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(investigatorList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.update_investigator_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(investigatorList.get(position).getRegistration_date());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            DateHelper.printDifference(start_date, end_date, time_elapsed);

            if (time_elapsed != null) {
                holder.update_investigator_time_joined.setText(time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return investigatorList.size();
    }

}
