package fiek.unipr.mostwantedapp.fragment.admin.update.person;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class UpdatePersonListViewHolder extends RecyclerView.ViewHolder{

    public TextView update_person_time_joined, update_person_name;
    public ImageView update_person_image;

    public UpdatePersonListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        update_person_name = itemView.findViewById(R.id.update_person_name);
        update_person_time_joined = itemView.findViewById(R.id.update_person_time_joined);
        update_person_image = itemView.findViewById(R.id.update_person_image);

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
