package com.codeu.app.conscribo.data;

import com.codeu.app.conscribo.Utility;
import com.parse.ParseUser;

/**
 * Created by jeff on 8/2/15.
 * This class is used in TreeBranchActivity to order the StoryObjects appropriately
 */
public class BranchNode {
    private StoryObject story;
    private String author;
    private ParseUser user;
    private String sentence;
    private int depth;
    private BranchNode next;

    public BranchNode(StoryObject story) {
        setStory(story);
        setSentence( Utility.getLastStringFromJSONArray( story.getSentencesJSONArray() ) );
        setAuthor( Utility.getLastStringFromJSONArray( story.getAuthorsJSONArray() ) );
        setUser(story.getUser());
    }

    // Setters
    public void setStory(StoryObject value) {
        this.story = value;
    }
    public void setAuthor(String value) {
        this.author = value;
    }
    public void setSentence(String value) {
        this.sentence = value;
    }
    public void setNext(BranchNode node) {
        this.next = node;
    }
    public void setUser(ParseUser user) {
        this.user = user;
    }

    // Getters
    public StoryObject getStory() {
        return this.story;
    }
    public String getAuthor() {
        return this.author;
    }
    public String getSentence() {
        return this.sentence;
    }
    public String generateKey() {
        return story.getDepth() + author + sentence;
    }
    public String generateParentKey() {
        int parentDepth = this.story.getDepth() - 1;
        //Get parent key from parentDepth + parentAuthor + parentSentence
        return parentDepth +
                Utility.getStringFromJSONArray( this.story.getAuthorsJSONArray(), parentDepth) +
                Utility.getStringFromJSONArray( this.story.getSentencesJSONArray(), parentDepth);
    }
    public BranchNode getNext(){
        return this.next;
    }
    public int getDepth() {
        return this.story.getDepth();
    }
    public ParseUser getUser() {
        return this.user;
    }
    public int getLikes(){
        return story.getLikes();
    }
}
