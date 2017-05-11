package in.codestar.foodbuddy.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.RecipeActivity;
import in.codestar.foodbuddy.model.SearchItemModel;

/**
 * ViewHolder class for search result items
 */

public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.image_view) ImageView imageView;
    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.subtitle_text_view) TextView subtitleTextView;

    private Context mContext;
    private SearchItemModel mSearchItemModel;

    public SearchViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mContext = context;

        itemView.setOnClickListener(this);
    }

    public void setSearchItemModel(SearchItemModel searchItemModel) {
        this.mSearchItemModel = searchItemModel;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getSubtitleTextView() {
        return subtitleTextView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, RecipeActivity.class);

        String recipe = mSearchItemModel.getRecipeJsonObject().toString();
        Bundle bundle = new Bundle();
        bundle.putString("recipe", recipe);

        intent.putExtra("recipe_bundle", bundle);
        mContext.startActivity(intent);
    }
}
