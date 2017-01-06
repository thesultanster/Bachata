package com.meetup.uhoo.core;

import java.util.List;

/**
 * Created by sultankhan on 11/24/16.
 */
public interface  UserCheckinListener {
    void onFetchUsersCheckedIn(List<User> checkedInUsers);
}
