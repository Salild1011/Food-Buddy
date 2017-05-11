package in.codestar.foodbuddy.model;

import org.json.JSONObject;

/**
 * Data Model for holding search results
 */

public class SearchItemModel {

    private String mTitle;
    private String mSubtitle;
    private String mImageUrl;
    private JSONObject mRecipeJsonObject;

    public SearchItemModel(String title, String subtitle, String imageUrl, JSONObject recipeJsonObject) {
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mImageUrl = imageUrl;
        this.mRecipeJsonObject = recipeJsonObject;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        this.mSubtitle = subtitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public JSONObject getRecipeJsonObject() {
        return mRecipeJsonObject;
    }

    public void setRecipeJsonObject(JSONObject recipeJsonObject) {
        this.mRecipeJsonObject = recipeJsonObject;
    }
}
