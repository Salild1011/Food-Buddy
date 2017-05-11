package in.codestar.foodbuddy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.codestar.foodbuddy.database.RecipeContract.RecipeEntry;

/**
 * Database Helper class for Pinned Recipes database
 */
public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipes.db";

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeEntry.RECIPE_URI + " TEXT NOT NULL, " +
                RecipeEntry.RECIPE_NAME + " TEXT NOT NULL, " +
                RecipeEntry.RECIPE_STR + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_RECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
