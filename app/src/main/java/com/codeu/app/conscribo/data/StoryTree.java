package com.codeu.app.conscribo.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jeff on 7/30/15.
 *
 * Members of StoryTree {
 *     "objectId":      String
 *     "title":         String
 *     "genre":         String
 *     "creator":       String
 *     <REMOVED> "priorityQueueStoryList": Relation< StoryObject> </REMOVED>
 *     "createdAt":     Date
 *     "updatedAt":     Date
 * }
 */
@ParseClassName("StoryTree")
public class StoryTree extends ParseObject {

    public void makeStoryTree(String title, String genre, String creator, ParseUser user){
        setTitle(title);
        setGenre(genre);
        setCreator(creator);
        setUser(user);
        setSubscribers();
    }


    // Setters
    public void setTitle(String value) {
        put("title", value);
    }
    public void setGenre(String value) {
        put("genre", value);
    }
    public void setCreator(String value) {
        put("creator", value);
    }
    public void setUser(ParseUser user) {
        put("user", user);
    }
    public void setSubscribers() {put("subscribers", new ArrayList<ParseUser>());}

    // Getters
    public String getTitle() {
        return getString("title");
    }
    public String getGenre() {
        return getString("genre");
    }
    public String getCreator() {
        return getString("creator");
    }
    public Date getUpdatedTime() {
        return getUpdatedAt();
    }
    public Date getCreatedTime() {
        return getCreatedAt();
    }
    public ParseUser getUser() {return getParseUser("user");}
    public ArrayList<ParseUser> getSubscribers() {return (ArrayList<ParseUser>) get("subscribers");}

    // Query
    public static ParseQuery<StoryTree> getQuery() {
        return ParseQuery.getQuery(StoryTree.class);
    }
    public void addSubscriber (ParseUser user) {
        ArrayList<ParseUser> subscribers = (ArrayList<ParseUser>) get("subscribers");
        if (!subscribers.contains(user)) {
            add("subscribers", user);
        }
    }
}
