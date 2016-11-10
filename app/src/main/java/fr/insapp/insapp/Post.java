package fr.insapp.insapp;

/**
 * Created by thoma on 29/10/2016.
 */

public class Post {
    public int avatar_id;
    public String title;
    public String text;
    public int image_id;
    public int heart_counter;

    public Post(int avatar_id, String title, String text, int image_id, int heart_counter) {
        this.avatar_id = avatar_id;
        this.title = title;
        this.text = text;
        this.image_id = image_id;
        this.heart_counter = heart_counter;
    }
}
