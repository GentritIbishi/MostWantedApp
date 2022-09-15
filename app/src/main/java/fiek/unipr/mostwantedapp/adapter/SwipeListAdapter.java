package fiek.unipr.mostwantedapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fiek.unipr.mostwantedapp.R;

public class SwipeListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<String> listData;

    public SwipeListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void remove(int pos) {
        listData.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.testing, null);
            }
            TextView textView = (TextView) convertView
                    .findViewById(R.id.name);
            textView.setText(listData.get(position));

            LinearLayout layout = (LinearLayout) convertView
                    .findViewById(R.id.delete_lay);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 50);
            layout.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
