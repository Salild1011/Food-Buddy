package in.codestar.foodbuddy.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.R;

/**
 * ViewHolder for each nutrition entry
 */

public class NutritionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.nutrition_text_view) TextView nutritionTextView;

    public NutritionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getNutritionTextView() {
        return nutritionTextView;
    }
}
