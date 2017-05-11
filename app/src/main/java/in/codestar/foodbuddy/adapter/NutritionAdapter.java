package in.codestar.foodbuddy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.viewholder.NutritionViewHolder;

/**
 * Adapter for managing nutrition table structure
 */

public class NutritionAdapter extends RecyclerView.Adapter<NutritionViewHolder> {

    private Context mContext;
    private ArrayList<String> mNutritionList;

    public NutritionAdapter(Context context, ArrayList<String> nutritionList) {
        this.mContext = context;
        this.mNutritionList = nutritionList;
    }

    @Override
    public NutritionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.nutrition_item, parent, false);
        return (new NutritionViewHolder(itemView));
    }

    @Override
    public void onBindViewHolder(NutritionViewHolder holder, int position) {
        holder.getNutritionTextView().setText(mNutritionList.get(position));
    }

    @Override
    public int getItemCount() {
        return (mNutritionList != null ? mNutritionList.size() : 0);
    }
}
