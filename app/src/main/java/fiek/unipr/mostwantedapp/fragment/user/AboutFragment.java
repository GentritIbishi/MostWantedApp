package fiek.unipr.mostwantedapp.fragment.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiek.unipr.mostwantedapp.R;

public class AboutFragment extends Fragment {

    View about_user_view;

    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        about_user_view = inflater.inflate(R.layout.fragment_about_user, container, false);

        return about_user_view;
    }
}