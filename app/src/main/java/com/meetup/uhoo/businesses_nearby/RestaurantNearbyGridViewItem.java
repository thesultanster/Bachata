package com.meetup.uhoo.businesses_nearby;

import android.graphics.Bitmap;

/**
 * Created by sultankhan on 10/23/16.
 */
public class RestaurantNearbyGridViewItem {
    private Bitmap image;
    private String gender;

    public RestaurantNearbyGridViewItem(String gender) {
        super();

        this.gender = gender;
    }

    public RestaurantNearbyGridViewItem(){

    }



    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }


}
