package com.meetup.uhoo.service_layer.user_services;

import com.meetup.uhoo.core.User;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface FirebaseDataFetchListener {
    void onDataFetch(User user);
}
