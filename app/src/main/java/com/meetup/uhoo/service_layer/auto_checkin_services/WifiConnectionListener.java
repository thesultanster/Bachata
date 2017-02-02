package com.meetup.uhoo.service_layer.auto_checkin_services;


import com.meetup.uhoo.core.Survey;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface WifiConnectionListener {
    void onWifiNetworkConnected(String BSSID);
}
