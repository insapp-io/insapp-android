package fr.insapp.insapp.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thoma on 31/10/2016.
 */

public class ClubThumb {
    private String id;
    private String name, email, description;
    private ArrayList<String> events;
    private ArrayList<String> posts;
    private String profilPicture;
    private String cover;
    private String bgColor;
    private String fgColor;

    public ClubThumb(String id, String name, String email, String description, ArrayList<String> events, ArrayList<String> posts, String profilPicture, String cover, String bgColor, String fgColor) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.events = events;
        this.posts = posts;

        this.profilPicture = profilPicture;
        this.cover = cover;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public ClubThumb(JSONObject json) throws JSONException
    {
        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.email = json.getString("email");
        this.description = json.getString("description");

        this.events = new ArrayList<String>();
        JSONArray jsonarray = json.optJSONArray("events");
        if(jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++) {
                events.add(jsonarray.getString(i));
            }
        }

        this.posts = new ArrayList<String>();
        JSONArray jsonarray2 = json.optJSONArray("posts");
        if(jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++) {
                posts.add(jsonarray2.getString(i));
            }
        }

        this.profilPicture = json.getString("profile");
        this.cover = json.getString("cover");
        this.bgColor = json.getString("bgcolor");
        this.fgColor = json.getString("fgcolor");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public String getProfilPicture() {
        return profilPicture;
    }

    public String getCover() {
        return cover;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getFgColor() {
        return fgColor;
    }
}
