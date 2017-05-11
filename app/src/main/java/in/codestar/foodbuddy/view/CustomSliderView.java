package in.codestar.foodbuddy.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

/**
 * Custom Slider View for the slider on Home Page
 * Derived from Slider View provided by
 */
public class CustomSliderView extends BaseSliderView {

    private Context mContext;

    public CustomSliderView(Context context) {
        super(context);
        mContext = context;
    }

    public View getView() {

        View v = LayoutInflater.from(this.getContext()).inflate(com.daimajia.slider.library.R.layout.render_type_text, null);
        ImageView target = (ImageView) v.findViewById(com.daimajia.slider.library.R.id.daimajia_slider_image);
        LinearLayout frame = (LinearLayout) v.findViewById(com.daimajia.slider.library.R.id.description_layout);
        frame.setBackgroundColor(Color.TRANSPARENT);

        this.bindEventAndShow(v, target);

        return v;
    }
}
