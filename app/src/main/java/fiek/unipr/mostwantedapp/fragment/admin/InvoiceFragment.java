package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.gallery.CustomizedGalleryAdapter;

public class InvoiceFragment extends Fragment {

    private Context mContext;
    private View view;
    private String created_date_time, transactionID, userId, account, status, updated_date_time;
    private Double amount;

    public InvoiceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invoice, container, false);
        mContext = getContext();

        getFromBundle();

        return view;
    }

    private void getFromBundle() {

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                created_date_time = bundle.getString("created_date_time");
                transactionID = bundle.getString("transactionID");
                userId = bundle.getString("userId");
                account = bundle.getString("account");
                status = bundle.getString("status");
                updated_date_time = bundle.getString("updated_date_time");
                amount = bundle.getDouble("amount");
            }

        }catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
    }

}