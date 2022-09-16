package fiek.unipr.mostwantedapp.fragment.admin.update.investigator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.helpers.RecyclerViewInterface;

public class UpdateInvestigatorListViewHolder extends RecyclerView.ViewHolder {

    public TextView update_investigator_time_joined, update_investigator_name;
    public ImageView update_investigator_image;

    public UpdateInvestigatorListViewHolder(@NonNull View investigatorItemView, RecyclerViewInterface recyclerViewInterface) {
        super(investigatorItemView);

        update_investigator_name = investigatorItemView.findViewById(R.id.update_investigator_name);
        update_investigator_time_joined = investigatorItemView.findViewById(R.id.update_investigator_time_joined);
        update_investigator_image = investigatorItemView.findViewById(R.id.update_investigator_image);

        investigatorItemView.setOnClickListener(new View.OnClickListener() {
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
