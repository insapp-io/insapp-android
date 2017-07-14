package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 13/07/2017.
 */

public class SearchResults {

    @SerializedName("users")
    private List<User> users;

    public SearchResults(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
