package fiek.unipr.mostwantedapp.fragment.admin.notification;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class NotificationListViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView user_reported_image;
    public TextView user_report_name, user_report_description, user_report_time;

    public NotificationListViewHolder(@NonNull View notificationItemView, RecyclerViewInterface recyclerViewInterface) {
        super(notificationItemView);

        user_report_name = notificationItemView.findViewById(R.id.user_report_name);
        user_report_description = notificationItemView.findViewById(R.id.user_report_description);
        user_report_time = notificationItemView.findViewById(R.id.user_report_time);
        user_reported_image = notificationItemView.findViewById(R.id.user_reported_image);

        notificationItemView.setOnClickListener(new View.OnClickListener() {
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
