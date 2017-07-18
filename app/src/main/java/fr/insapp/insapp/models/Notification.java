package fr.insapp.insapp.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by Antoine on 10/10/2016.
 */

@AutoParcelGson
public abstract class Notification implements Parcelable {

    @Nullable
    @SerializedName("ID")
    abstract String id();

    @SerializedName("sender")
    abstract String sender();

    @SerializedName("receiver")
    abstract String receiver();

    @SerializedName("content")
    abstract String content();

    @SerializedName("comment")
    abstract Comment comment();

    @SerializedName("message")
    abstract String message();

    @SerializedName("seen")
    abstract boolean seen();

    @SerializedName("date")
    abstract Date date();

    @SerializedName("type")
    abstract String type();

    private transient Post post;
    private transient Club club;
    private transient User user;
    private transient Event event;

    public static Notification create(String id, String sender, String receiver, String content, Comment comment, String message, boolean seen, Date date, String type) {
        return new AutoParcelGson_Notification(id, sender, receiver, content, comment, message, seen, date, type);
    }

    public static Notification create(Map<String, String> data) {
        Notification notification = null;

        try {
            JSONObject jsonComment = new JSONObject(data.get("comment"));
            JSONArray jsonTags = new JSONArray(jsonComment.getJSONArray("tags"));

            List<Tag> tags = new ArrayList<>();

            for (int i = 0; i < jsonTags.length(); ++i) {
                JSONObject jsonTag = jsonTags.getJSONObject(i);

                tags.add(Tag.create(
                        jsonTag.getString("ID"),
                        jsonTag.getString("user"),
                        jsonTag.getString("name")
                ));
            }

            Comment comment = Comment.create(
                    jsonComment.getString("ID"),
                    jsonComment.getString("user"),
                    jsonComment.getString("content"),
                    new Date(jsonComment.getString("date")),
                    tags);

            notification = Notification.create(
                    data.get("ID"),
                    data.get("sender"),
                    data.get("receiver"),
                    data.get("content"),
                    comment,
                    data.get("message"),
                    Boolean.parseBoolean(data.get("seen")),
                    new Date(data.get("date")),
                    data.get("type"));
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return notification;
    }

    public String getId() {
        return id();
    }

    public String getSender() {
        return sender();
    }

    public String getReceiver() {
        return receiver();
    }

    public String getContent() {
        return content();
    }

    public Comment getComment() {
        return comment();
    }

    public String getMessage() {
        return message();
    }

    public boolean isSeen() {
        return seen();
    }

    public Date getDate() {
        return date();
    }

    public String getType() {
        return type();
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

