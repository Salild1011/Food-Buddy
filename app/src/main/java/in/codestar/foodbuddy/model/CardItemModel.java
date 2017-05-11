package in.codestar.foodbuddy.model;

import org.json.JSONObject;

/**
 * Data model for Cards in Sections
 */

public class CardItemModel {

    private String mCardTitle;
    private String mImageUrl;
    private JSONObject mCardJsonData;

    public CardItemModel(String cardTitle, String imageUrl, JSONObject cardJsonData) {
        this.mCardTitle = cardTitle;
        this.mImageUrl = imageUrl;
        this.mCardJsonData = cardJsonData;
    }

    public String getCardTitle() {
        return mCardTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public JSONObject getCardJsonData() {
        return mCardJsonData;
    }
}
