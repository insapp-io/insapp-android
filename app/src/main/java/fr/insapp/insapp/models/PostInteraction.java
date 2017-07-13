package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 12/07/2017.
 */

public class PostInteraction {

    @SerializedName("post")
    private Post post;

    @SerializedName("user")
    private User user;

    public PostInteraction(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
