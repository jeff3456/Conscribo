package com.codeu.app.conscribo.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Tommy on 8/11/2015.
 */

@ParseClassName("UserData")
public class UserData extends ParseObject {

    public void makeData(int numLikes, ArrayList<ParseUser> subscribers) {
        setLikes(numLikes);
        setSubscribers(subscribers);
    }

    // Setters
    public void setLikes(int num) {
        put("numLikes", num);
    }

    public void setSubscribers(ArrayList<ParseUser> subs) {
        put("subscribers", subs);
    }

    // Getters
    public int getLikes() {
        return (int) get("numLikes");
    }

    public ArrayList<ParseUser> getSubscribers() {
        return (ArrayList<ParseUser>) get("subscribers");
    }

    public void addLike() {
        increment("numLikes");
    }

    public void addSubscriber(ParseUser user) {
        ArrayList<ParseUser> subscribers = (ArrayList<ParseUser>) get("subscribers");
        if (!subscribers.contains(user)) {
            add("subscribers", user);
        }
    }

}


