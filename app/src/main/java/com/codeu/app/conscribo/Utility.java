package com.codeu.app.conscribo;

import android.util.Log;

import com.codeu.app.conscribo.data.UserData;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jeff on 8/1/15.
 *
 * This contains all of the different functions utilized when creating a StoryObject or
 * getting the appropriate strings from a StoryObject.
 */
public class Utility {
    private static final String LOGTAG = Utility.class.getSimpleName();

    /* Checks the input sentence for a period at the end and no other place */
    public static boolean hasSentenceEnd(String inputString) {
        // Check to see if last char is the end of a sentence . ! ? "

        if(inputString == null || inputString.length() <= 0) {
            return false;
        }

        char lastChar = inputString.charAt(inputString.length() - 1);
        if( lastChar == '.' || lastChar == '!' || lastChar == '?' || lastChar == '"' ) {
            return true;
        }

        return false;
    }

    // Used for getting the last String in the JSONArray
    public static String getLastStringFromJSONArray(JSONArray array) {
        String author = "";
        try {
            author = array.getString( array.length() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return author;
    }

    // Used for getting an individual element from JSONArray
    public static String getStringFromJSONArray(JSONArray array, int index) {
        if (index >= array.length() || index < 0 ) {
            Log.e(LOGTAG, "index = " + index + " array.length() = " + array.length());
            throw new ArrayIndexOutOfBoundsException();
        }
        try {
            return array.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Used to combine all Strings from JSONArray
    public static String generateStringFromJSONArray(JSONArray array) {
        String stringsFromArray = "";
        for(int i = 0; i < array.length(); i++) {
            try {
                stringsFromArray += array.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return stringsFromArray;
    }

    public static int findGenreDrawable(String genre) {

        // Switchcase to match genre and return drawable id
        switch (genre) {
            case "Sci Fi":
                return R.drawable.scifi;
            case "Fairy Tale":
                return R.drawable.fairytale;
            case "Horror":
                return R.drawable.horror;
            case "Mystery":
                return R.drawable.mystery;
            case "Fantasy":
                return R.drawable.fantasy;
            case "Romance":
                return R.drawable.romance;
            case "Satire":
                return R.drawable.satire;
            case "Western":
                return R.drawable.western;
        }

        return R.drawable.other;
    }


    public static ArrayList<String> convertJSONArrayToStringArrayList(JSONArray array) throws JSONException {
        ArrayList<String> stringList = new ArrayList<String>();

        for(int i = 0; i < array.length(); i++) {
            stringList.add(array.getString(i));
        }

        return stringList;
    }

    public static boolean userLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public static UserData getUserData(String objectId) {
        UserData retrieved = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserData");
        try {
            retrieved = (UserData) query.get(objectId);
        }
        catch (ParseException e) {

        }

        return retrieved;
    }
}
