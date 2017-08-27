package com.meetup.uhoo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.businesses_nearby.RestaurantsNearby;
import com.meetup.uhoo.restaurant.RestaurantActivity;

public class IntroScreen extends AppCompatActivity {

    TextView tvNearYouTitle;
    TextView tvNumberOfUsers;
    TextView tvNumberOfUsersText;
    TextView tvNumberOfHappenings;
    TextView tvNumberOfHappeningsText;
    LinearLayout llUhooContinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        tvNearYouTitle = (TextView) findViewById(R.id.tvNearYouTitle);
        tvNumberOfUsers = (TextView) findViewById(R.id.tvNumberOfUsers);
        tvNumberOfUsersText = (TextView) findViewById(R.id.tvNumberOfUsersText);
        tvNumberOfHappenings = (TextView) findViewById(R.id.tvNumberOfHappenings);
        tvNumberOfHappeningsText = (TextView) findViewById(R.id.tvNumberOfHappeningsText);
        llUhooContinue = (LinearLayout) findViewById(R.id.llUhooContinue);


        llUhooContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantsNearby.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
