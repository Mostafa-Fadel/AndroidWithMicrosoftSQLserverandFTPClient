package com.mfadel.angel.firebaseauth;

import android.graphics.Bitmap;

/**
 * Created by Angel on 2017-09-25.
 */

public class ListItem {


    String image;
    String title, itemkey;

    public ListItem(String image, String title, String itemkey) {
        this.image = image;
        this.title = title;
        this.itemkey = itemkey;
    }

    public String getImage() {
        return image;
    }

    public String gettitle() {
        return title;
    }

    public String getitemkey() {
        return itemkey;
    }
}
