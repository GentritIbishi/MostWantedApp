package fiek.unipr.mostwantedapp.adapter.report.modified;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;
import static fiek.unipr.mostwantedapp.utils.Constants.FAKE;
import static fiek.unipr.mostwantedapp.utils.Constants.UNVERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Notifications;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class ModifiedReportNotificationAdapter extends RecyclerView.Adapter<ModifiedReportNotificationListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private String time_elapsed;
    private List<Notifications> modifiedReportList;

    public ModifiedReportNotificationAdapter(Context context, List<Notifications> modifiedReportList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.modifiedReportList = modifiedReportList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ModifiedReportNotificationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.modified_notification_item, parent, false);
        return new ModifiedReportNotificationListViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ModifiedReportNotificationListViewHolder holder, int position) {
        try {
            holder.user_report_modified_name.setText(context.getText(R.string.report_with_id)+" "+modifiedReportList.get(position).getNotificationReportId()+" "+context.getText(R.string.has_new_status_right_now));

            String verified = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.you_earn)+20+" "+ "coins\n"+context.getText(R.string.thank_you_for_collaboration);

            String fake = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.please_be_real_in_giving_information_next_time)+", "+
                    context.getText(R.string.thank_you);

            String unverified = context.getText(R.string.status_of_report_has_changed_to)+" "+
                    modifiedReportList.get(position).getNotificationReportNewStatus()+ " \n"+ context.getText(R.string.still_under_review)+", "+
                    context.getText(R.string.thank_you);

            if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(FAKE)){
                holder.user_report_modified_description.setText(fake.substring(0,36)+"...");
            }else if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(VERIFIED)){
                holder.user_report_modified_description.setText(verified.substring(0,36)+"...");
            }else if(modifiedReportList.get(position).getNotificationReportNewStatus().equals(UNVERIFIED)){
                holder.user_report_modified_description.setText(unverified.substring(0,36)+"...");
            }

            //Your report with date: and title: and decription has been changed to

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(modifiedReportList.get(position).getNotificationDateTime());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            time_elapsed = DateHelper.printDifference(start_date, end_date);

            if(time_elapsed != null){
                holder.user_report_modified_time.setText(time_elapsed);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return modifiedReportList.size();
    }

}
