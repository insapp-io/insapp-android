package fr.insapp.insapp.models.credentials;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 10/07/2017.
 */

public class LogInCredentials {

    @SerializedName("ID")
    private String ID;

    @SerializedName("username")
    private String username;

    @SerializedName("authtoken")
    private String authtoken;

    @SerializedName("user")
    private String user;

    @SerializedName("device")
    private String device;

    public LogInCredentials(String ID, String username, String authtoken, String user, String device) {
        this.ID = ID;
        this.username = username;
        this.authtoken = authtoken;
        this.user = user;
        this.device = device;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public String getUser() {
        return user;
    }

    public String getDevice() {
        return device;
    }
}
