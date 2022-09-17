package fiek.unipr.mostwantedapp.adapter.report.location;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class ManageLocationListViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView user_reported_image;
    public TextView user_report_name, user_report_description, user_report_time;

    public ManageLocationListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        user_report_name = itemView.findViewById(R.id.user_report_name);
        user_report_description = itemView.findViewById(R.id.user_report_description);
        user_report_time = itemView.findViewById(R.id.user_report_time);
        user_reported_image = itemView.findViewById(R.id.user_reported_image);

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
