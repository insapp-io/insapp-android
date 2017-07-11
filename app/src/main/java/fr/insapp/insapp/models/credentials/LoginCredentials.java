package fr.insapp.insapp.models.credentials;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 10/07/2017.
 */

public class LoginCredentials {

    @SerializedName("ID")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("authtoken")
    private String authToken;

    @SerializedName("user")
    private String user;

    @SerializedName("device")
    private String device;

    public LoginCredentials(String id, String username, String authToken, String user, String device) {
        this.id = id;
        this.username = username;
        this.authToken = authToken;
        this.user = user;
        this.device = device;
    }

    public void setId(String Id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUser() {
        return user;
    }

    public String getDevice() {
        return device;
    }
}
