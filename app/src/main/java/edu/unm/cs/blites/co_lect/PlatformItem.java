package edu.unm.cs.blites.co_lect;

/**
 * Created by Brandon on 5/5/2015.
 */
public class PlatformItem {
    String platformTitle;
    String imageUrl;
    String platformId;
    int numOfItems;

    public int getNumOfItems() {
        return numOfItems;
    }

    public void setNumOfItems(int numOfItems) {
        this.numOfItems = numOfItems;
    }

    public String getPlatformTitle() {
        return platformTitle;
    }

    public void setPlatformTitle(String platformTitle) {
        this.platformTitle = platformTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }
}
