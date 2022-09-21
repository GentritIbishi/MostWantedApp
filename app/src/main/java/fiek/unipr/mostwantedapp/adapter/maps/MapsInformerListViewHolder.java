package fiek.unipr.mostwantedapp.adapter.maps;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class MapsInformerListViewHolder extends RecyclerView.ViewHolder{

    public TextView person_search_time_joined, tv_l_fullName, tv_l_status;
    public CircleImageView profile_person;
    public ProgressBar lvPerson_progressBar;

    public MapsInformerListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        tv_l_fullName = itemView.findViewById(R.id.tv_l_fullName);
        tv_l_status = itemView.findViewById(R.id.tv_l_status);
        profile_person = itemView.findViewById(R.id.profile_person);
        lvPerson_progressBar = itemView.findViewById(R.id.lvPerson_progressBar);
        person_search_time_joined = itemView.findViewById(R.id.person_search_time_joined);

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
