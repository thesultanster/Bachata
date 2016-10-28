package com.meetup.uhoo.profile;

import android.graphics.Bitmap;

/**
 * Created by sultankhan on 10/23/16.
 */
public class SimpleProfileActivityItem {
    private Bitmap image;
    private String title;
    private boolean selected;

    public SimpleProfileActivityItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
        this.selected = false;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean getSelected(){
        return this.selected;
    }

}
