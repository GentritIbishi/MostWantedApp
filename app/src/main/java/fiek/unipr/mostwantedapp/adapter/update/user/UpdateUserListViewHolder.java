package fiek.unipr.mostwantedapp.adapter.update.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class UpdateUserListViewHolder extends RecyclerView.ViewHolder {

    public TextView update_user_time_joined, update_user_role, update_user_name;
    public ImageView update_user_image;

    public UpdateUserListViewHolder(@NonNull View userView, RecyclerViewInterface recyclerViewInterface) {
        super(userView);

        update_user_name = userView.findViewById(R.id.update_user_name);
        update_user_role = userView.findViewById(R.id.update_user_role);
        update_user_time_joined = userView.findViewById(R.id.update_user_time_joined);
        update_user_image = userView.findViewById(R.id.update_user_image);

        userView.setOnClickListener(new View.OnClickListener() {
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
