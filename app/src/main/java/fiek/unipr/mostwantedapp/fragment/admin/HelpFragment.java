package fiek.unipr.mostwantedapp.fragment.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fiek.unipr.mostwantedapp.R;

public class HelpFragment extends Fragment {

    private View help_admin_view;
    private Button btnCallUs, btnEmailUs;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        help_admin_view =  inflater.inflate(R.layout.fragment_help_admin, container, false);

        btnCallUs = help_admin_view.findViewById(R.id.btnCallUs);
        btnEmailUs = help_admin_view.findViewById(R.id.btnEmailUs);

        btnCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:192"));
                startActivity(callIntent);
            }
        });

        btnEmailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", String.valueOf(getContext().getText(R.string.support_email)), null));
                startActivity(Intent.createChooser(intent, String.valueOf(getContext().getText(R.string.choose_an_email_client))));
            }
        });

        return help_admin_view;
    }
}