package in.codestar.foodbuddy.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.database.RecipeContract.RecipeEntry;

/**
 * Service for setting up list of pinned recipes
 */

public class RecipeListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private ArrayList<String> mImageList;
    private ArrayList<String> mTitleList;
    private Cursor mCursor;

    private final String LOG_TAG = getClass().getSimpleName();

    RecipeRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mImageList = new ArrayList<>();
        mTitleList = new ArrayList<>();
        fetchData();
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        mImageList = new ArrayList<>();
        mTitleList = new ArrayList<>();
        fetchData();

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (!mCursor.isClosed()) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_layout);
        rv.setImageViewBitmap(R.id.recipe_image_view, getImageBitmap(mImageList.get(position)));
        rv.setTextViewText(R.id.recipe_title_text_view, mTitleList.get(position));

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void fetchData() {
        mCursor = mContext.getContentResolver().query(RecipeEntry.getContentUri(),
                null,
                null,
                null,
                null);

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                String name = mCursor.getString(mCursor.getColumnIndex(RecipeEntry.RECIPE_NAME));
                String recipeStr = mCursor.getString(mCursor.getColumnIndex(RecipeEntry.RECIPE_STR));
                String imageUri = "";

                try {
                    JSONObject recipeObj = new JSONObject(recipeStr);
                    imageUri = recipeObj.getString("image");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!mTitleList.contains(name)) {
                    mImageList.add(imageUri);
                    mTitleList.add(name);
                }

                mCursor.moveToNext();
            } while (!mCursor.isAfterLast());

            mCursor.close();
        }
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting bitmap", e);
        }
        return bm;
    }
}