package fiek.unipr.mostwantedapp.adapter.report.location;

import static fiek.unipr.mostwantedapp.utils.Constants.ANONYMOUS;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.Report;

public class ManageLocationReportListAdapter extends RecyclerView.Adapter<ManageLocationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Report> reportList;
    private String urlOfProfile, time_elapsed, informer_person;

    public ManageLocationReportListAdapter(Context context, List<Report> reportList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.reportList = reportList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ManageLocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.report_notification_item, parent, false);
        return new ManageLocationListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageLocationListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.user_report_name.setText(reportList.get(position).getInformer_person());

            informer_person = holder.user_report_name.getText().toString();

            if (informer_person != null && informer_person.equals(ANONYMOUS)){

                holder.user_reported_image.setImageResource(R.drawable.ic_anonymous);

            }else if(informer_person != null && informer_person.startsWith("+")){

                holder.user_reported_image.setImageResource(R.drawable.ic_phone_login);

            }else if(!informer_person.equals(ANONYMOUS) && !informer_person.startsWith("+")){
                // in below line we are using Picasso to
                // load image from URL in our Image VIew.
                Glide.with(context)
                        .load(reportList.get(position).getInformer_person_urlOfProfile())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.user_reported_image.setImageDrawable(resource);
                                return true;
                            }
                        })
                        .circleCrop()
                        .preload();
            }


            holder.user_report_description.setText(reportList.get(position).getDescription());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(reportList.get(position).getDate_time());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            DateHelper.printDifference(start_date, end_date, time_elapsed);

            if(time_elapsed != null){
                holder.user_report_time.setText(time_elapsed);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

}
