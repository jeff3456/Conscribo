package com.codeu.app.conscribo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class StoryProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private StoryDBHelper mOpenHelper;

    static final int STORY_OBJECT_WITH_OBJECT_ID = 100;
    static final int STORY_OBJECT = 101;

    private static final SQLiteQueryBuilder sStoryQueryBuilder;

    static {
        sStoryQueryBuilder = new SQLiteQueryBuilder();

    }

    public StoryProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new StoryDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StoryContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, StoryContract.PATH_STORY_OBJECT + "/*", STORY_OBJECT_WITH_OBJECT_ID);

        matcher.addURI(authority, StoryContract.PATH_STORY_OBJECT, STORY_OBJECT);
        /*
        matcher.addURI(authority, StoryContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, StoryContract.PATH_WEATHER + "/* /#", WEATHER_WITH_LOCATION_AND_DATE);
        */

        return matcher;
    }
}
