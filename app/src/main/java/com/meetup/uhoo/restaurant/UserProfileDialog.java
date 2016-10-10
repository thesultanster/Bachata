package com.meetup.uhoo.restaurant;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.plus.People;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;

/**
 * Created by sultankhan on 9/25/16.
 */
public class UserProfileDialog extends Dialog implements
        android.view.View.OnClickListener {

    Button connectButton;
    User user;
    TextView name, miniBio;

    public UserProfileDialog(Activity a, User user) {
        super(a);
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_user_profile);

        connectButton = (Button) findViewById(R.id.connectButton);
        name = (TextView) findViewById(R.id.nameText);
        miniBio = (TextView) findViewById(R.id.miniBio);

        name.setText(user.getName_first() + " " + user.getName_last());
        miniBio.setText(user.getOne_liner());

        connectButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}