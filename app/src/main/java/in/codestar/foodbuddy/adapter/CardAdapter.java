package in.codestar.foodbuddy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.model.CardItemModel;
import in.codestar.foodbuddy.viewholder.CardViewHolder;

/**
 * Adapter for Card Recycler View
 */

class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private Context mContext;
    private ArrayList<CardItemModel> mCardItemList;

    CardAdapter(Context context, ArrayList<CardItemModel> cardItemList) {
        this.mContext = context;
        this.mCardItemList = cardItemList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
        return (new CardViewHolder(itemView, mContext));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardItemModel model = mCardItemList.get(position);
        holder.getCardTitleTextView().setText(model.getCardTitle());
        Picasso.with(mContext).load(model.getImageUrl()).into(holder.getImageView());
        holder.setCardItemModel(mCardItemList.get(position));

        String contentDesc = mContext.getString(R.string.card_icon_desc) + " " + model.getCardTitle();
        holder.getImageView().setContentDescription(contentDesc);
    }

    @Override
    public int getItemCount() {
        return (mCardItemList != null ? mCardItemList.size() : 0);
    }
}
