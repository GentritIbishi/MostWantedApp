package fiek.unipr.mostwantedapp.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.fragment.user.HomeFragment;
import fiek.unipr.mostwantedapp.fragment.user.NotificationFragment;
import fiek.unipr.mostwantedapp.fragment.user.ProfileFragment;
import fiek.unipr.mostwantedapp.fragment.user.SearchFragment;

public class InformerDashboardActivity extends AppCompatActivity {

    //num of selected tab. We have 4 tabs so value must lie between 1-4. Default value is 1, cause first tab is selected by deafult
    private int selectedTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informer_dashboard);

        final LinearLayout homeLayout = findViewById(R.id.homeLayout);
        final LinearLayout searchLayout = findViewById(R.id.searchLayout);
        final LinearLayout notificationLayout = findViewById(R.id.notificationLayout);
        final LinearLayout profileLayout = findViewById(R.id.profileLayout);

        final ImageView homeImage = findViewById(R.id.homeImage);
        final ImageView searchImage = findViewById(R.id.searchImage);
        final ImageView notificationImage = findViewById(R.id.notificationImage);
        final ImageView profileImage = findViewById(R.id.profileImage);

        final TextView homeTxt = findViewById(R.id.homeTxt);
        final TextView searchTxt = findViewById(R.id.searchTxt);
        final TextView notificationTxt = findViewById(R.id.notificationTxt);
        final TextView profileTxt = findViewById(R.id.profileTxt);

        //set home fragment by default
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, HomeFragment.class, null)
                .commit();

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if home is already selected or not.
                if(selectedTab != 1) {
                    //set home fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, HomeFragment.class, null)
                            .commit();

                    //unselect other tabs except home tab
                    searchTxt.setVisibility(View.GONE);
                    notificationTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    notificationImage.setImageResource(R.drawable.ic_notification);
                    profileImage.setImageResource(R.drawable.ic_user);

                    searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    homeTxt.setVisibility(View.VISIBLE);
                    homeImage.setImageResource(R.drawable.ic_selected_home);
                    homeLayout.setBackgroundResource(R.drawable.round_back_home_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    homeLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 1;
                }
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if search is already selected or not.
                if(selectedTab != 2) {
                    //set search fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, SearchFragment.class, null)
                            .commit();

                    //unselect other tabs except search tab
                    homeTxt.setVisibility(View.GONE);
                    notificationTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.ic_home_);
                    notificationImage.setImageResource(R.drawable.ic_notification);
                    profileImage.setImageResource(R.drawable.ic_user);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    searchTxt.setVisibility(View.VISIBLE);
                    searchImage.setImageResource(R.drawable.ic_selected_search);
                    searchLayout.setBackgroundResource(R.drawable.round_back_search_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    searchLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 2;
                }
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if notification is already selected or not.
                if(selectedTab != 3) {
                    //set notification fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, NotificationFragment.class, null)
                            .commit();

                    //unselect other tabs except notification tab
                    homeTxt.setVisibility(View.GONE);
                    searchTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.ic_home_);
                    searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    profileImage.setImageResource(R.drawable.ic_user);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    notificationTxt.setVisibility(View.VISIBLE);
                    notificationImage.setImageResource(R.drawable.ic_selected_notification);
                    notificationLayout.setBackgroundResource(R.drawable.round_back_notification_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    notificationLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 3;
                }
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if profile is already selected or not.
                if(selectedTab != 4) {
                    //set profile fragment by default
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, ProfileFragment.class, null)
                            .commit();

                    //unselect other tabs except profile tab
                    homeTxt.setVisibility(View.GONE);
                    searchTxt.setVisibility(View.GONE);
                    notificationTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.ic_home_);
                    searchImage.setImageResource(R.drawable.ic_search_navbottom);
                    notificationImage.setImageResource(R.drawable.ic_notification);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    searchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select notification tab
                    profileTxt.setVisibility(View.VISIBLE);
                    profileImage.setImageResource(R.drawable.ic_selected_user);
                    profileLayout.setBackgroundResource(R.drawable.round_back_profile_100);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f );
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    profileLayout.startAnimation(scaleAnimation);

                    // set selected tab 1 as selected tab
                    selectedTab = 4;
                }
            }
        });

    }

}