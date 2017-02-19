package com.meetup.uhoo.service_layer.happening_services;


import com.meetup.uhoo.core.Happening;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface HappeningDataFetchListener {
    void onHappeningFetched(Happening object);
}
