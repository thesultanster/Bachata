package com.meetup.uhoo.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.AppConstant;

/**
 * Created by sultankhan on 10/30/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get User Data if it Exists
        SharedPreferences prefs = context.getSharedPreferences("currentUser", context.MODE_PRIVATE);
        String checkedInto = prefs.getString("checkedInto", "");

        String action = intent.getAction();
        if (AppConstant.DEALS_ACTION.equals(action)) {
            Toast.makeText(context, "deal", Toast.LENGTH_SHORT).show();
        } else if (AppConstant.CHECKOUT_ACTION.equals(action)) {
            // If user checked into a business
            if(!checkedInto.equals("")) {
                DatabaseReference mDatabase;

                // Remove user to checkin table
                mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(checkedInto);
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                // Update user isCheckedIn state
                mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mDatabase.child("isCheckedIn").setValue(false);

                cancelNotification(context, AppConstant.CHECKIN_NOTIF);
            }

        }
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}

