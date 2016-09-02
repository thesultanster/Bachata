package com.meetup.uhoo.util.location;

import android.location.Location;

public interface LocationUpdatedCommunicator {

    public void onChange(Location location);
}
