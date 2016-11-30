package fr.insapp.insapp.modeles;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Antoine on 20/09/2016.
 */
public class Credentials {

    private String id;
    private String userID;
    private String username;
    private String sessionToken;

    public Credentials(String id, String userID, String username, String sessionToken) {
        this.id = id;
        this.userID = userID;
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public Credentials(JSONObject json) throws JSONException {
        this.id = json.getJSONObject("sessionToken").getString("Id");
        this.userID = json.getJSONObject("user").getString("ID");
        this.username = json.getJSONObject("credentials").getString("username");
        this.sessionToken = json.getJSONObject("sessionToken").getString("Token");
    }

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionToken() {
        return sessionToken;
    }

}
