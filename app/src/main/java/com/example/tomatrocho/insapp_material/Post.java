package com.example.tomatrocho.insapp_material;

/**
 * Created by thoma on 29/10/2016.
 */

public class Post {
    public int avatar_id;
    public String title;
    public String text;
    public int image_id;

    public Post(int avatar_id, String title, String text, int image_id) {
        this.avatar_id = avatar_id;
        this.title = title;
        this.text = text;
        this.image_id = image_id;
    }
}
