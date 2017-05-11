package in.codestar.foodbuddy.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import in.codestar.foodbuddy.R;

/**
 * Recipes Provider for Food Buddy widget
 */

public class RecipeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.recipe_widget);

            // Setup the ListView
            Intent serviceIntent = new Intent(context, RecipeListWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.empty_view);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
