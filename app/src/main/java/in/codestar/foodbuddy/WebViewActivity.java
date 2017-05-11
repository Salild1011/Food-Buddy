package in.codestar.foodbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.web_view) WebView mRecipeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

//        String url = getIntent().getStringExtra("recipe_url");
        String url = getIntent().getDataString();
        mRecipeWebView.getSettings().setJavaScriptEnabled(true);

        mRecipeWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprication")
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return false;
            }
        });

        mRecipeWebView.loadUrl(url);
    }
}
