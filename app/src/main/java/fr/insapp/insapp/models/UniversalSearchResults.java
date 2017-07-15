package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 15/07/2017.
 */

public class UniversalSearchResults {

    @SerializedName("associations")
    private List<Club> clubs;

    @SerializedName("posts")
    private List<Post> posts;

    @SerializedName("events")
    private List<Event> events;

    @SerializedName("users")
    private List<User> users;

    public UniversalSearchResults(List<Club> clubs, List<Post> posts, List<Event> events, List<User> users) {
        this.clubs = clubs;
        this.posts = posts;
        this.events = events;
        this.users = users;
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<User> getUsers() {
        return users;
    }
}
