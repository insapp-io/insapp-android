package fr.insapp.insapp;

/**
 * Created by tomatrocho on 17/11/16.
 */

public class Comment {
    public int avatar_id;
    public String username;
    public String text;

    public Comment(int avatar_id, String username, String text) {
        this.avatar_id = avatar_id;
        this.username = username;
        this.text = text;
    }
}
