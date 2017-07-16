package fr.insapp.insapp.models;

import android.os.Parcelable;

import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by Antoine on 19/09/2016.
 */

@AutoParcelGson
public abstract class User implements Parcelable {

    @SerializedName("ID")
    abstract String id();

    @SerializedName("name")
    abstract String name();

    @SerializedName("username")
    abstract String username();

    @SerializedName("description")
    abstract String description();

    @SerializedName("email")
    abstract String email();

    @SerializedName("emailpublic")
    abstract boolean emailPublic();

    @SerializedName("promotion")
    abstract String promotion();

    @SerializedName("gender")
    abstract String gender();

    @SerializedName("events")
    abstract List<String> events();

    @SerializedName("postsliked")
    abstract List<String> postsLiked();

    static User create(String id, String name, String username, String description, String email, boolean emailPublic, String promotion, String gender, List<String> events, List<String> postsLiked) {
        return new AutoParcelGson_User(id, name, username, description, email, emailPublic, promotion, gender, events, postsLiked);
    }

    public String getId() {
        return id();
    }

    public String getName() {
        return name();
    }

    public String getUsername() {
        return username();
    }

    public String getDescription() {
        return description();
    }

    public String getEmail() {
        return email();
    }

    public boolean isEmailPublic() {
        return emailPublic();
    }

    public String getPromotion() {
        return promotion();
    }

    public String getGender() {
        return gender();
    }

    public List<String> getEvents() {
        return events();
    }

    public List<String> getPostsLiked() {
        return postsLiked();
    }
}