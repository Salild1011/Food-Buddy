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
import in.codestar.foodbuddy.model.CardItemModel;

/**
 * ViewHolder for each card
 */

public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.image_view) ImageView imageView;
    @BindView(R.id.card_title_text_view) TextView cardTitleTextView;

    private Context mContext;
    private CardItemModel mCardItemModel;

    public CardViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mContext = context;

        itemView.setOnClickListener(this);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getCardTitleTextView() {
        return cardTitleTextView;
    }

    public void setCardItemModel(CardItemModel cardItemModel) {
        this.mCardItemModel = cardItemModel;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, RecipeActivity.class);

        String recipe = mCardItemModel.getCardJsonData().toString();
        Bundle bundle = new Bundle();
        bundle.putString("recipe", recipe);

        intent.putExtra("recipe_bundle", bundle);
        mContext.startActivity(intent);
    }
}
