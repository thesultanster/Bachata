package com.meetup.uhoo.service_layer.current_user_data_services;

import com.meetup.uhoo.core.User;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface FirebaseDataFetchListener {
    void onDataFetch(User user);
}
