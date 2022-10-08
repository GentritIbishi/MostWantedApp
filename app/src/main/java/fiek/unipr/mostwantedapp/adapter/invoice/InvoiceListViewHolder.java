package fiek.unipr.mostwantedapp.adapter.invoice;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class InvoiceListViewHolder extends RecyclerView.ViewHolder{

    public TextView invoice_search_time_joined, tv_transaction_id, tv_l_status;

    public InvoiceListViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        tv_transaction_id = itemView.findViewById(R.id.tv_transaction_id);
        tv_l_status = itemView.findViewById(R.id.tv_l_status);
        invoice_search_time_joined = itemView.findViewById(R.id.invoice_search_time_joined);

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
