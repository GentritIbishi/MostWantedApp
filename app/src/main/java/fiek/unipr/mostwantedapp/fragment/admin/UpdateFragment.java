package fiek.unipr.mostwantedapp.fragment.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;

public class UpdateFragment extends Fragment {

    private View update_view;

    public UpdateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        update_view = inflater.inflate(R.layout.fragment_update, container, false);



        return update_view;
    }
}