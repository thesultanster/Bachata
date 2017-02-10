package com.meetup.uhoo.service_layer.business_services;


import com.meetup.uhoo.core.Business;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface BusinessNearbyListener {
    void onBusinessFetched(Business object);
    void onFetchComplete();
    void onBusinessDoesntExist();
}
