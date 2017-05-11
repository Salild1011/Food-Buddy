package in.codestar.foodbuddy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codestar.foodbuddy.adapter.SearchAdapter;
import in.codestar.foodbuddy.model.SearchItemModel;
import in.codestar.foodbuddy.util.Utility;

/**
 * Activity to perform searches on server data
 */
public class SearchActivity extends AppCompatActivity {

    // data
    private final String LOG_TAG = getClass().getSimpleName();

    // views
    @BindView(R.id.search_edit_text) EditText mSearchEditText;
    @BindView(R.id.search_results_recycler_view) RecyclerView mSearchResultsRecyclerView;
    @BindView(R.id.adView) AdView mBottomBannerAdView;

    // adapter
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mSearchEditText.addTextChangedListener(new SearchActionListener());
        mSearchEditText.setOnEditorActionListener(new SearchEditorActionListener());

        mSearchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(this);
        mSearchResultsRecyclerView.setAdapter(mSearchAdapter);

        // Initialize ad
        AdRequest adRequest = new AdRequest.Builder().build();
        mBottomBannerAdView.loadAd(adRequest);
    }

    private class SearchActionListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String search = Utility.buildSearchUrl(SearchActivity.this, s.toString());
            Log.d("Text", search);

            SearchAsyncTask task = new SearchAsyncTask(search);
            task.execute();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class SearchEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("Perform", "Search");
                return true;
            }
            return false;
        }
    }

    /**
     * AsyncTask to perform search action and fetch data
     */
    private class SearchAsyncTask extends AsyncTask<String, Void, String> {

        private String mSearchUrl;

        SearchAsyncTask(String searchUrl) {
            this.mSearchUrl = searchUrl;
        }

        @Override
        protected String doInBackground(String... params) {
            //Fetch String from url echo
            StringBuilder strBuilder = new StringBuilder();
            try {
                URL url = new URL(mSearchUrl);

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

            return strBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                ArrayList<SearchItemModel> itemModels = Utility.parseSearchResults(s);

                if (itemModels == null) {
                    Log.w(LOG_TAG, "Error loading data from server");
                    Toast.makeText(SearchActivity.this, getString(R.string.error_loading_data),
                            Toast.LENGTH_SHORT).show();
                }
//                else if (itemModels.size() == 0) {
//                    Toast.makeText(SearchActivity.this, getString(R.string.no_result_data),
//                            Toast.LENGTH_SHORT).show();
//                }
                else {
                    mSearchAdapter.updateData(itemModels);
                }
            }
            else {
                Log.w(LOG_TAG, "Error loading data from server");
                Toast.makeText(SearchActivity.this, getString(R.string.error_loading_data),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
