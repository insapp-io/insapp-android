package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 18/07/2017.
 */

public class NotificationUser {

    @SerializedName("ID")
    private String id;

    @SerializedName("userid")
    private String userId;

    @SerializedName("token")
    private String token;

    @SerializedName("os")
    private String os;

    public NotificationUser(String id, String userId, String token, String os) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.os = os;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getOs() {
        return os;
    }
}
