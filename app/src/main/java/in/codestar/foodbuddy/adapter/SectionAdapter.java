package in.codestar.foodbuddy.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.model.SectionItemModel;
import in.codestar.foodbuddy.viewholder.SectionViewHolder;

/**
 * Adapter for Section Recycler View
 */

public class SectionAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    private Context mContext;
    private ArrayList<SectionItemModel> mSectionItemModels;

    public SectionAdapter(Context context) {
        this.mContext = context;
        mSectionItemModels = new ArrayList<>();
    }

    public SectionAdapter(Context context, ArrayList<SectionItemModel> sectionItemModels) {
        this.mContext = context;
        this.mSectionItemModels = sectionItemModels;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.section_layout_item, parent, false);
        return (new SectionViewHolder(itemView));
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        SectionItemModel model = mSectionItemModels.get(position);
        holder.getSectionTitleTextView().setText(model.getSectionTitle());

        RecyclerView cardRecyclerView = holder.getCardRecyclerView();
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        CardAdapter adapter = new CardAdapter(mContext, model.getCardItemModels());
        cardRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return (mSectionItemModels != null ? mSectionItemModels.size() : 0);
    }

    public void addSection(SectionItemModel itemModel) {
        mSectionItemModels.add(itemModel);
        notifyDataSetChanged();
    }

    public void removeSection(String name) {
        for (SectionItemModel model : mSectionItemModels) {
            if (model.getSectionTitle().equals(name)) {
                mSectionItemModels.remove(model);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void updateSection(SectionItemModel itemModel) {
        int i = 0;
        boolean updated = false;
        for (SectionItemModel model : mSectionItemModels) {
            if (model.getSectionTitle().equals(itemModel.getSectionTitle())) {
                mSectionItemModels.set(i, itemModel);
                Log.d("Section Uodate", "updated " + i + " -- " + itemModel.getCardItemModels().size());
                updated = true;
                break;
            }
            ++i;
        }

        if (!updated) {
            mSectionItemModels.add(itemModel);
            Log.d("Section Uodate", "added");
        }

        notifyDataSetChanged();
    }

    public void clear() {
        mSectionItemModels = new ArrayList<>();
        notifyDataSetChanged();
    }
}
