package in.codestar.foodbuddy;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.adapter.NutritionAdapter;
import in.codestar.foodbuddy.util.Utility;
import in.codestar.foodbuddy.database.RecipeContract.RecipeEntry;
import in.codestar.foodbuddy.widget.RecipeWidgetProvider;

public class RecipeActivity extends AppCompatActivity {

    // views
    @BindView(R.id.recipe_linear_layout) LinearLayout mRecipeLinearLayout;
    @BindView(R.id.recipe_image_view) ImageView mRecipeImageView;
    @BindView(R.id.recipe_title_text_view) TextView mTitleTextView;
    @BindView(R.id.recipe_tags_text_view) TextView mTagsTextView;
    @BindView(R.id.recipe_caution_text_view) TextView mCautionTextView;
    @BindView(R.id.recipe_content_text_view) TextView mRecipeContentTextView;
    @BindView(R.id.nutrition_recycler_view) RecyclerView mNutritionRecyclerView;
    @BindView(R.id.view_recipe_button) Button mViewRecipeButton;
    @BindView(R.id.pin_recipe_button) ToggleButton mPinRecipeButton;

    private InterstitialAd mInterstitialAd;


    // data
    private final String LOG_TAG = getClass().getSimpleName();
    private Bundle mRecipeBundle;
    private boolean mPinned = false;
    private String mRecipeUrl;

    // Analytics Tracker
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                viewRecipe(mRecipeUrl);
            }
        });

        requestNewInterstitial();

        if (savedInstanceState != null) {
            mRecipeBundle = savedInstanceState.getBundle("recipe_bundle");
        }
        else if (getIntent().hasExtra("recipe_bundle")) {
            mRecipeBundle = getIntent().getBundleExtra("recipe_bundle");
        }

        if (mRecipeBundle != null) {
            try {
                JSONObject recipeObj = new JSONObject(mRecipeBundle.getString("recipe"));

                // Add image
                Picasso.with(this).load(recipeObj.getString("image")).into(mRecipeImageView);

                // Add recipe title
                mTitleTextView.setText(recipeObj.getString("label"));

                // Add tags
                StringBuilder builder = new StringBuilder();
                builder.append(getString(R.string.tag_desc));
                builder.append(" ");

                boolean added = false;

                JSONArray jsonArray = recipeObj.getJSONArray("dietLabels");
                for (int i = 0; i < jsonArray.length() && i < 3; i++) {
                    if (i > 0) builder.append(", ");
                    builder.append(jsonArray.getString(i));
                    added = true;
                }

                jsonArray = recipeObj.getJSONArray("healthLabels");
                for (int i = 0; i < jsonArray.length() && i < 3; i++) {
                    if (i > 0 || added) builder.append(", ");
                    builder.append(jsonArray.getString(i));
                }
                mTagsTextView.setText(builder.toString());

                // Add caution
                added = false;
                builder = new StringBuilder();
                builder.append(getString(R.string.caution_desc));
                builder.append(" ");

                jsonArray = recipeObj.getJSONArray("cautions");
                for (int i = 0; i < jsonArray.length() && i < 3; i++) {
                    if (i > 0) builder.append(", ");
                    builder.append(jsonArray.getString(i));
                    added = true;
                }
                if (!added) {
                    builder.append(" -");
                }
                mCautionTextView.setText(builder.toString());

                // Add recipe lines
                builder = new StringBuilder();
                builder.append(getString(R.string.recipe_lines_desc));
                builder.append("\n");

                jsonArray = recipeObj.getJSONArray("ingredientLines");
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (i > 0) builder.append("\n\n");
                    builder.append(jsonArray.getString(i));
                }
                mRecipeContentTextView.setText(builder.toString());

                // Add nutritional info
                ArrayList<String> nutritionList = new ArrayList<>();
                JSONObject nutritionObj = recipeObj.getJSONObject("totalNutrients");

                nutritionList.add(getString(R.string.energy));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("ENERC_KCAL").getString("quantity")));
                nutritionList.add(getString(R.string.fats));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("FAT").getString("quantity")));
                nutritionList.add(getString(R.string.carbs));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("CHOCDF").getString("quantity")));
                nutritionList.add(getString(R.string.fibers));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("FIBTG").getString("quantity")));
                nutritionList.add(getString(R.string.sugar));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("SUGAR").getString("quantity")));
                nutritionList.add(getString(R.string.protein));
                nutritionList.add(Utility.formatNutritionValues(nutritionObj.getJSONObject("PROCNT").getString("quantity")));

                mNutritionRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                NutritionAdapter nutritionAdapter = new NutritionAdapter(this, nutritionList);
                mNutritionRecyclerView.setAdapter(nutritionAdapter);

                // Add view recipe button link
                mRecipeUrl = recipeObj.getString("url");
                final String url = mRecipeUrl;
                mViewRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            viewRecipe(url);
                        }
                    }
                });

                // Check database for pin status
                managePinDatabase();

                // Write entry in recents file
                Utility.updateRecentsFile(this, recipeObj.getString("uri"));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error_loading_recipe), Toast.LENGTH_SHORT).show();
                mRecipeLinearLayout.setVisibility(View.GONE);
            }
        }
        else {
            Toast.makeText(this, getString(R.string.error_loading_recipe), Toast.LENGTH_SHORT).show();
            mRecipeLinearLayout.setVisibility(View.GONE);
        }

        // Initialize analytics
        mTracker = getDefaultTracker();
    }

    private void viewRecipe(String url) {
        Intent intent = new Intent(RecipeActivity.this, WebViewActivity.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    synchronized Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTracker != null) {
            String name = "Recipe Page";
            Log.i(LOG_TAG, "Setting screen name: " + name);
            mTracker.setScreenName("Image~" + name);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("recipe_bundle", mRecipeBundle);
    }

    private void managePinDatabase() {
        String recipeStr = mRecipeBundle.getString("recipe");

        String[] columns = {RecipeEntry.RECIPE_URI};
        String selection = RecipeEntry.RECIPE_URI + " =?";
        String[] selectionArgs = {Utility.getRecipeUri(recipeStr)};

        Cursor cursor = getContentResolver().query(
                RecipeEntry.getContentUri(),
                columns,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            mPinned = cursor.getCount() > 0;
            cursor.close();
        }

        mPinRecipeButton.setChecked(mPinned);
        mPinRecipeButton.setOnCheckedChangeListener(new MyPinnedCheckedChangeListener());
    }

    private class MyPinnedCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String recipeStr = mRecipeBundle.getString("recipe");

            if (mPinned != isChecked && isChecked && mRecipeBundle != null) {
                ContentValues values = new ContentValues();
                values.put(RecipeEntry.RECIPE_URI, Utility.getRecipeUri(recipeStr));
                values.put(RecipeEntry.RECIPE_NAME, mTitleTextView.getText().toString());
                values.put(RecipeEntry.RECIPE_STR, recipeStr);

                getContentResolver().insert(RecipeEntry.getContentUri(), values);

                Toast.makeText(RecipeActivity.this, getString(R.string.pin_add_success), Toast.LENGTH_SHORT).show();
                mPinned = true;

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Pinned")
                        .build());
            }
            else {
                String[] whereArgs = { Utility.getRecipeUri(recipeStr) };
                getContentResolver().delete(
                        RecipeEntry.getContentUri(), RecipeEntry.RECIPE_URI + "=?", whereArgs);

                Toast.makeText(RecipeActivity.this, getString(R.string.pin_remove_success), Toast.LENGTH_SHORT).show();
                mPinned = false;

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Unpinned")
                        .build());
            }

            updateWidget();
        }
    }

    // Update widgets with changes
    private void updateWidget() {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, RecipeWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }
}
