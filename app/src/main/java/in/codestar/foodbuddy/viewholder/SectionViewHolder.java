package in.codestar.foodbuddy.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.R;

/**
 * ViewHolder for sections
 */

public class SectionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.section_title_text_view) TextView sectionTitleTextView;
    @BindView(R.id.card_recycler_view) RecyclerView cardRecyclerView;

    public SectionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getSectionTitleTextView() {
        return sectionTitleTextView;
    }

    public RecyclerView getCardRecyclerView() {
        return cardRecyclerView;
    }
}
