package fr.insapp.insapp.models;

import android.os.Parcelable;

import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

@AutoParcelGson
public abstract class Club implements Parcelable {

    @SerializedName("ID")
    abstract String id();

    @SerializedName("name")
    abstract String name();

    @SerializedName("email")
    abstract String email();

    @SerializedName("description")
    abstract String description();

    @SerializedName("events")
    abstract List<String> events();

    @SerializedName("posts")
    abstract List<String> posts();

    @SerializedName("profile")
    abstract String profilePicture();

    @SerializedName("cover")
    abstract String cover();

    @SerializedName("bgcolor")
    abstract String bgColor();

    @SerializedName("fgcolor")
    abstract String fgColor();


    static Club create(String id, String name, String email, String description, List<String> events, List<String> posts, String profilePicture, String cover, String bgColor, String fgColor) {
        return new AutoParcelGson_Club(id, name, email, description, events, posts, profilePicture, cover, bgColor, fgColor);
    }

    public String getId() {
        return id();
    }

    public String getName() {
        return name();
    }

    public String getEmail() {
        return email();
    }

    public String getDescription() {
        return description();
    }

    public List<String> getEvents() {
        return events();
    }

    public List<String> getPosts() {
        return posts();
    }

    public String getProfilePicture() {
        return profilePicture();
    }

    public String getCover() {
        return cover();
    }

    public String getBgColor() {
        return bgColor();
    }

    public String getFgColor() {
        return fgColor();
    }
}