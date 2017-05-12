package in.codestar.foodbuddy;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.adapter.SectionAdapter;
import in.codestar.foodbuddy.analytics.FoodBuddyAnalytics;
import in.codestar.foodbuddy.database.RecipeContract;
import in.codestar.foodbuddy.loader.DataAsyncLoader;
import in.codestar.foodbuddy.model.CardItemModel;
import in.codestar.foodbuddy.model.SectionItemModel;
import in.codestar.foodbuddy.pref.AppPreferences;
import in.codestar.foodbuddy.util.Utility;
import in.codestar.foodbuddy.view.CustomSliderView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, BaseSliderView.OnSliderClickListener {

    // static final data
    private static final int BANNER_LOADER_ID = 1;
    private static final String BANNER_URL = Utility.buildBannerUrl("0", "3");
    private final String LOG_TAG = getClass().getSimpleName();

    // views
    @BindView(R.id.image_slider) SliderLayout mSliderLayout;
    @BindView(R.id.popular_tags_recycler_view) RecyclerView mPopularTagsRV;

    // adapters
    private SectionAdapter mSectionAdapter;

    // Analytics Tracker
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        ButterKnife.bind(this);

        if (AppPreferences.isFirstRun(this)) {
            createFirstRunDialog(this);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Search")
                        .build());
            }
        });

        initSlider();

        // Initialize adapter for sections
        mSectionAdapter = new SectionAdapter(this);
        mPopularTagsRV.setLayoutManager(new LinearLayoutManager(this));
        mPopularTagsRV.setAdapter(mSectionAdapter);

        // Initialize AdMob SDK
        MobileAds.initialize(this, getString(R.string.bottom_banner_unit_id));

        // Subscribe to recipe news topic notifications on FCM
        if (Utility.isGooglePlayServicesAvailable(this)) {
            FirebaseMessaging.getInstance().subscribeToTopic("recipe_news");
        }

        // Initialize analytics
        FoodBuddyAnalytics analytics = (FoodBuddyAnalytics) getApplication();
        mTracker = analytics.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSections();

        if (mTracker != null) {
            String name = "Home Page";
            Log.i(LOG_TAG, "Setting screen name: " + name);
            mTracker.setScreenName("Image~" + name);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_about) {
            createAboutDialog(MainActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param context
     * Creates Dialog Box to redirect user to SettingsActivity, to set preferences and settings
     */
    private void createFirstRunDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.first_run_pref));
        builder.setMessage(getString(R.string.first_run_welcome));

        builder.setPositiveButton(getString(R.string.dialog_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_negative_btn), null);

        builder.show();
    }

    private void createAboutDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.about));

        String message = getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME + " " +
                getString(R.string.about_message);
        builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.dialog_positive), null);
        builder.show();
    }

    /**
     * Initialize the home page slider with images and URLs
     */
    private void initSlider() {
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setDuration(4000);
        getLoaderManager().restartLoader(BANNER_LOADER_ID, null, this).forceLoad();
    }

    /**
     * Initialize the sections on home page
     */
    private void initSections() {
        // Section for Recently Viewed Recipes
        // Read file
        ArrayList<String> recents = Utility.readRecentsFile(this);

        // Execute AsyncTask for all
        if (recents != null) {
            new SectionRecentsAsyncTask(recents).execute();
        }

        // Section for Pinned recipes
        SectionItemModel pinnedSection = new SectionItemModel(getString(R.string.pinned));
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_NAME));
                String recipeStr = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_STR));
                String imageUri;
                CardItemModel cardItemModel = null;

                try {
                    JSONObject recipeObj = new JSONObject(recipeStr);
                    imageUri = recipeObj.getString("image");
                    cardItemModel = new CardItemModel(name, imageUri, recipeObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (cardItemModel != null) pinnedSection.addCardItemModel(cardItemModel);

                cursor.moveToNext();
            } while (!cursor.isAfterLast());
            cursor.close();
        }
        mSectionAdapter.updateSection(pinnedSection);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        try {
            URL url = new URL(BANNER_URL);
            return new DataAsyncLoader(this, url, true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            JSONArray dataArr = (new JSONObject(data)).getJSONArray("hits");

            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject obj = dataArr.getJSONObject(i);
                JSONObject recipe = obj.getJSONObject("recipe");

                String imgUrl = recipe.getString("image");
                String label = recipe.getString("label");

                // Add to banner
                CustomSliderView customSliderView = new CustomSliderView(this);
                Bundle bundle = new Bundle();
                bundle.putString("recipe", recipe.toString());

                customSliderView
                        .description(label)
                        .image(imgUrl)
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(this)
                        .bundle(bundle);

                mSliderLayout.addSlider(customSliderView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            if (mSliderLayout != null) {
                mSliderLayout.removeAllViews();
                mSliderLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Bundle bundle = slider.getBundle();

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe_bundle", bundle);
        startActivity(intent);
    }

    private class SectionRecentsAsyncTask extends AsyncTask<String, Void, SectionItemModel> {

        private ArrayList<String> mRecents;
        private SectionItemModel mSectionModel;

        SectionRecentsAsyncTask(ArrayList<String> recents) {
            this.mRecents = recents;
            mSectionModel = new SectionItemModel(getString(R.string.recents));
        }

        @Override
        protected SectionItemModel doInBackground(String... params) {
            for (int i = mRecents.size() - 1; i >= 0; i--) {
                String uri = mRecents.get(i);

                //Fetch String from url echo
                StringBuilder strBuilder = new StringBuilder();
                try {
                    URL url = new URL(Utility.buildRecipeUrl(uri));

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();

                    if (inputStream == null) {
                        Log.w(LOG_TAG, "InputStream is null");
                        return null;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String str;

                    while ((str = br.readLine()) != null) {
                        strBuilder.append(str);
                    }

                    inputStream.close();
                    br.close();

                    if (strBuilder.length() == 0) {
                        Log.w(LOG_TAG, "No String Received");
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String s = strBuilder.toString();
                try {
                    JSONArray arr = new JSONArray(s);
                    JSONObject recipeObj = arr.getJSONObject(0);

                    String label = recipeObj.getString("label");
                    String imageUrl = recipeObj.getString("image");

                    CardItemModel model = new CardItemModel(label, imageUrl, recipeObj);
                    mSectionModel.addCardItemModel(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return mSectionModel;
        }

        @Override
        protected void onPostExecute(SectionItemModel s) {
            if (s != null) {
//                mSectionAdapter.removeSection(s.getSectionTitle());
//                mSectionAdapter.addSection(s);
                mSectionAdapter.updateSection(s);
            }
        }
    }
}
