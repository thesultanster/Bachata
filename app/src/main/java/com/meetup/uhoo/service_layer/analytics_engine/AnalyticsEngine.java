package com.meetup.uhoo.service_layer.analytics_engine;

import com.google.firebase.auth.FirebaseAuth;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;

/**
 * Created by sultankhan on 5/6/17.
 */
public class AnalyticsEngine {

    private static AnalyticsEngine ourInstance = new AnalyticsEngine();

    public static AnalyticsEngine getInstance() {
        return ourInstance;
    }

    private AnalyticsEngine() {

    }


    public void logHappeningClick(String happeningId){
        AnalyticsService.getInstance().logHappeningClick(happeningId, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}

