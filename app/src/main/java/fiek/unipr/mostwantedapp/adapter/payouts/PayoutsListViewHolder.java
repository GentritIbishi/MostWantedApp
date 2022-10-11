package fiek.unipr.mostwantedapp.adapter.payouts;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class PayoutsListViewHolder extends RecyclerView.ViewHolder {

    public PayoutsListViewHolder(@NonNull View itemView,  RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

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
