package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 15/07/2017.
 */

public class PostSearchResults {

    @SerializedName("posts")
    private List<Post> posts;

    public PostSearchResults(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
