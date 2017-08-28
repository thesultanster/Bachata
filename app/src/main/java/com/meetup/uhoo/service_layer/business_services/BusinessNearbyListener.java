package com.meetup.uhoo.service_layer.business_services;


import com.meetup.uhoo.core.Business;

import java.util.ArrayList;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface BusinessNearbyListener {
    void onBusinessFetched(Business object);
    void onFetchComplete(ArrayList<Business> loadedBusinesses);
    void onFetchComplete();
    void onBusinessDoesntExist();
}
