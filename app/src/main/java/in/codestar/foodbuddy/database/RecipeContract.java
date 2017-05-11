package in.codestar.foodbuddy.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines all column names and table name
 */

public class RecipeContract {

    public static final String CONTENT_AUTHORITY = "in.codestar.foodbuddy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PINNED_RECIPES = "recipes";

    public RecipeContract() { }

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PINNED_RECIPES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PINNED_RECIPES;

        public static final String TABLE_NAME = "pinned_recipes";

        public static final String ID = "_id";
        public static final String RECIPE_URI = "recipe_uri";
        public static final String RECIPE_NAME = "recipe_name";
        public static final String RECIPE_STR = "recipe_str";

        public static Uri buildRecipesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri getContentUri() {
            return CONTENT_URI;
        }
    }
}
