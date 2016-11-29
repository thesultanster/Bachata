package com.meetup.uhoo;

/**
 * Created by sultankhan on 11/26/16.
 */
public class Enum {
    public enum HappeningType{
        DEAL, EVENT, COMEDY,VARIETY;
    }

    public enum CheckinVisibilityState {
        AVAILABLE(0), CHECK(1), BUSY(2);

        private final int value;

        CheckinVisibilityState(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
}