package fiek.unipr.mostwantedapp.adapter.maps;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class MapsLocationListViewHolder extends RecyclerView.ViewHolder{

    public TextView tv_location_time_elapsed, tv_location_person_fullName;
    public CircleImageView profile_location_person;

    public MapsLocationListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        tv_location_person_fullName = itemView.findViewById(R.id.tv_location_person_fullName);
        tv_location_time_elapsed = itemView.findViewById(R.id.tv_location_time_elapsed);
        profile_location_person = itemView.findViewById(R.id.profile_location_person);

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
