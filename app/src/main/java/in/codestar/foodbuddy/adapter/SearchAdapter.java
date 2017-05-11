package in.codestar.foodbuddy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.model.SearchItemModel;
import in.codestar.foodbuddy.viewholder.SearchViewHolder;

/**
 * Adapter for RecyclerView holding search results
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private Context mContext;
    private ArrayList<SearchItemModel> mSearchList;

    public SearchAdapter(Context context) {
        this.mContext = context;
        mSearchList = new ArrayList<>();
    }

    public SearchAdapter(Context context, ArrayList<SearchItemModel> searchList) {
        this.mContext = context;
        this.mSearchList = searchList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, parent, false);
        return (new SearchViewHolder(itemView, mContext));
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        SearchItemModel itemModel = mSearchList.get(position);

        Picasso.with(mContext).load(itemModel.getImageUrl()).into(holder.getImageView());
        holder.getTitleTextView().setText(itemModel.getTitle());
        holder.getSubtitleTextView().setText(itemModel.getSubtitle());
        holder.setSearchItemModel(itemModel);

        String contentDesc = mContext.getString(R.string.card_icon_desc) + " " + itemModel.getTitle();
        holder.getImageView().setContentDescription(contentDesc);
    }

    @Override
    public int getItemCount() {
        return (mSearchList != null ? mSearchList.size() : 0);
    }

    public void updateData(ArrayList<SearchItemModel> searchList) {
        this.mSearchList = searchList;
        notifyDataSetChanged();
    }
}
