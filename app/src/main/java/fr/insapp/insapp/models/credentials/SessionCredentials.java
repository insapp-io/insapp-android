package fr.insapp.insapp.models.credentials;

import com.google.gson.annotations.SerializedName;

import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;

/**
 * Created by thomas on 11/07/2017.
 */

public class SessionCredentials {

    @SerializedName("credentials")
    private LoginCredentials loginCredentials;

    @SerializedName("sessionToken")
    private SessionToken sessionToken;

    @SerializedName("user")
    private User user;

    public SessionCredentials(LoginCredentials loginCredentials, SessionToken sessionToken, User user) {
        this.loginCredentials = loginCredentials;
        this.sessionToken = sessionToken;
        this.user = user;
    }

    public LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    public void setLoginCredentials(LoginCredentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }

    public SessionToken getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(SessionToken sessionToken) {
        this.sessionToken = sessionToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
