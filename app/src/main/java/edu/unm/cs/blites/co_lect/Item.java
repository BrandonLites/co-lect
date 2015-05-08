package edu.unm.cs.blites.co_lect;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Brandon on 5/4/2015.
 */
public class Item {

    private String imageUrl;
    private String itemTitle;
    private String itemPlatform;

    private int itemPlatformId;

//    public Item(NetworkImageView itemImage, String itemTitle, String itemPlatform) {
//        this.itemImage = itemImage;
//        this.itemTitle = itemTitle;
//        this.itemPlatform = itemPlatform;
////    }
//
//    public NetworkImageView getItemImage() {
//        return itemImage;
//    }
//
//    public void setItemImage(NetworkImageView itemImage) {
//        this.itemImage = itemImage;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemPlatform() {
        return itemPlatform;
    }

    public void setItemPlatform(String itemPlatform) {
        this.itemPlatform = itemPlatform;
    }


    public int getItemPlatformId() {
        return itemPlatformId;
    }

    public void setItemPlatformId(int itemPlatformId) {
        this.itemPlatformId = itemPlatformId;
    }

}
