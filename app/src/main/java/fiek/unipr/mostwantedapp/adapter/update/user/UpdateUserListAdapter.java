package fiek.unipr.mostwantedapp.adapter.update.user;

import static fiek.unipr.mostwantedapp.utils.Constants.DATE_TIME;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.RecyclerViewInterface;
import fiek.unipr.mostwantedapp.models.User;

public class UpdateUserListAdapter extends RecyclerView.Adapter<UpdateUserListViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<User> userList;
    private String time_elapsed;

    public UpdateUserListAdapter(Context context, List<User> userList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.userList = userList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UpdateUserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context)
                .inflate(R.layout.user_item_single, parent, false);
        return new UpdateUserListViewHolder(userView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateUserListViewHolder holder, int position) {
        try {
            holder.update_user_name.setText(userList.get(position).getFullName());
            holder.update_user_role.setText(userList.get(position).getRole());

            if (userList.get(position).getUrlOfProfile() != null && !userList.get(position).getUrlOfProfile().isEmpty()) {
                Picasso.get().load(userList.get(position).getUrlOfProfile()).transform(new CircleTransform()).into(holder.update_user_image);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);

            Date start_date = simpleDateFormat.parse(userList.get(position).getRegister_date_time());
            Date end_date = simpleDateFormat.parse(DateHelper.getDateTime());
            time_elapsed = DateHelper.printDifference(start_date, end_date);

            if (time_elapsed != null) {
                holder.update_user_time_joined.setText(time_elapsed);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
