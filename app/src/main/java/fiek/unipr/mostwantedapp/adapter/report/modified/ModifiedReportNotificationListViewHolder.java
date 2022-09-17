package fiek.unipr.mostwantedapp.adapter.report.modified;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class ModifiedReportNotificationListViewHolder extends RecyclerView.ViewHolder {

    public TextView user_report_modified_name, user_report_modified_description, user_report_modified_time;

    public ModifiedReportNotificationListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        user_report_modified_name = itemView.findViewById(R.id.user_report_modified_name);
        user_report_modified_description = itemView.findViewById(R.id.user_report_modified_description);
        user_report_modified_time = itemView.findViewById(R.id.user_report_modified_time);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerViewInterface != null)
                {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            }
        });

    }

}
