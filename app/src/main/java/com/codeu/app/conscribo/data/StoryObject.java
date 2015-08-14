package com.codeu.app.conscribo.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by jeff on 7/30/15.
 *
 * Members of StoryObject {
 *     "objectId":      String
 *     "title":         String
 *     "genre":         String
 *     "tree":          Pointer<StoryTree>
 *     "listSentences": Array
 *     "listAuthors":   Array
 *     "likes":         Number
 *     "depth":         Number
 *     "createdAt":     Date
 *     "updatedAt":     Date
 * }
 *
 */
@ParseClassName("StoryObject")
public class StoryObject extends ParseObject implements Serializable {
    public void makeStoryObject(String title, String genre, List<String> authors, List<String> sentences, int depth, ParseUser user) {
        setTitle(title);
        setGenre(genre);
        addSentences(sentences);
        addAuthors(authors);
        setLikes();
        setDepth(depth);
        setUser(user);
    }

    // Setters
    public void setTitle(String value) {
        put("title", value);
    }
    public void setGenre(String value) {
        put("genre", value);
    }
    public void setTree(ParseObject tree) {
        put("tree", tree);
    }
    public void setUser(ParseUser user) {
        put("user", user);
    }
    public void addSentence(String sentence) {
        add("listSentences", sentence);
    }
    public void addSentences(List<String> sentences) {
        addAll("listSentences", sentences);
    }
    public void addAuthor(String author) {
        add("listAuthors", author);
    }
    public void addAuthors(List<String> authors) {
        addAll("listAuthors", authors);
    }
    public void setLikes() {
        put("likes", 0);
    }
    public void setDepth(int value) {
        put("depth", value);
    }


    // Increment and decrement
    public void addLike() {
        increment("likes");
    }
    public void addDepth() {
        increment("depth");
    }
    public void removeLike() {
        increment("likes", -1);
    }
    public void removeDepth() {
        increment("depth", -1);
    }

    // Getters
    public String getTitle() {
        return getString("title");
    }
    public String getGenre() {
        return getString("genre");
    }
    public ParseObject getTree() {
        return getParseObject("tree");
    }
    public ParseUser getUser() {return (ParseUser) get("user");}
    public JSONArray getSentencesJSONArray() {
        return getJSONArray("listSentences");
    }
    public JSONArray getAuthorsJSONArray() {
        return getJSONArray("listAuthors");
    }
    public int getLikes() {
        return getInt("likes");
    }
    public int getDepth() {
        return getInt("depth");
    }
    public Date getUpdatedTime() {
        return getUpdatedAt();
    }
    public Date getCreatedTime() {
        return getCreatedAt();
    }

    // StoryObject query
    public static ParseQuery<StoryObject> getQuery() {
        return ParseQuery.getQuery(StoryObject.class);
    }


}
