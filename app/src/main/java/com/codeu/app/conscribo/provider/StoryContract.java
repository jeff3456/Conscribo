package com.codeu.app.conscribo.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jeff on 8/13/15.
 */
public class StoryContract {

    public static final String CONTENT_AUTHORITY = "com.codeu.app.conscribo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_STORY_OBJECT = "story_object";
    public static final String PATH_STORY_TREE = "tree";
    public static final String PATH_USER = "user";

    public static final class StoryObjectEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STORY_OBJECT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORY_OBJECT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORY_OBJECT;

        public static final String TABLE_NAME = "story_object";

        // Columns for StoryObjects
        public static final String COLUMN_OBJECT_ID = "object_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_TEXT = "text";

        public static final String COLUMN_GENRE = "genre";

        public static final String COLUMN_TREE_ID = "tree_id";

        public static final String COLUMN_LIKES = "likes";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
