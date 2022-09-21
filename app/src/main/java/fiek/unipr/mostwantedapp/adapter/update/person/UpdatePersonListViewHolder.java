package fiek.unipr.mostwantedapp.adapter.update.person;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class UpdatePersonListViewHolder extends RecyclerView.ViewHolder{

    public TextView update_person_time_joined, update_person_name;
    public ImageView update_person_image;

    public UpdatePersonListViewHolder(@NonNull View personView, RecyclerViewInterface recyclerViewInterface) {
        super(personView);

        update_person_name = personView.findViewById(R.id.update_person_name);
        update_person_time_joined = personView.findViewById(R.id.update_person_time_joined);
        update_person_image = personView.findViewById(R.id.update_person_image);

        personView.setOnClickListener(new View.OnClickListener() {
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
