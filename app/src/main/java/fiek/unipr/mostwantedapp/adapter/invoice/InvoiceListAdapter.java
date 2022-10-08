package fiek.unipr.mostwantedapp.adapter.invoice;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.Invoice;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Invoice> invoiceList;
    private String time_elapsed;

    public InvoiceListAdapter(Context context, List<Invoice> invoiceList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.invoiceList = invoiceList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public InvoiceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View invoiceView = LayoutInflater.from(context)
                .inflate(R.layout.invoice_single_item, parent, false);
        return new InvoiceListViewHolder(invoiceView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceListViewHolder holder, int position) {
        try {
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            holder.tv_l_status.setText(invoiceList.get(position).getStatus());
            holder.tv_transaction_id.setText(invoiceList.get(position).getTransactionID());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(invoiceList.get(position).getCreated_date_time());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            time_elapsed = DateHelper.printDifference(start_date, end_date);

            if (time_elapsed != null) {
                holder.invoice_search_time_joined.setText(time_elapsed);
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

}
