package fiek.unipr.mostwantedapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import fiek.unipr.mostwantedapp.R;

public class SpinnerAdapter extends ArrayAdapter<Object> {

    private Context context;
    int resId;
    private String[] mAnimListItems;

    public SpinnerAdapter(Context context, int textViewResourceId,
                          String[] strText) {
        super(context, textViewResourceId, strText);
        this.resId = textViewResourceId;
        this.context = context;
        this.mAnimListItems = strText;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(resId, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.tvItem);
        if (position == 0) {
            tv.setTextSize(25f);
        } else if (position == 1) {
            tv.setTextSize(35f);
        } else if (position == 1) {
            tv.setTextSize(40f);
        }

        tv.setText(mAnimListItems[position].toString());

        return convertView;
    }
}
