package com.meetup.uhoo.service_layer.user_services;

import com.meetup.uhoo.core.User;

import java.util.List;

/**
 * Created by sultankhan on 11/24/16.
 */
public interface UserCheckinListener {
    void onUserCheckinStateChange( boolean isCheckedIn );
}
