package in.codestar.foodbuddy.model;

import java.util.ArrayList;

/**
 * Data Model for holding Sections Data
 */

public class SectionItemModel {

    private String mSectionTitle;
    private ArrayList<CardItemModel> mCardItemModels;

    public SectionItemModel(String sectionTitle) {
        this.mSectionTitle = sectionTitle;
        mCardItemModels = new ArrayList<>();
    }

    public SectionItemModel(String sectionTitle, ArrayList<CardItemModel> cardItemModels) {
        this.mSectionTitle = sectionTitle;
        this.mCardItemModels = cardItemModels;
    }

    public String getSectionTitle() {
        return mSectionTitle;
    }

    public ArrayList<CardItemModel> getCardItemModels() {
        return mCardItemModels;
    }

    public void addCardItemModel(CardItemModel model) {
        mCardItemModels.add(model);
    }
}
