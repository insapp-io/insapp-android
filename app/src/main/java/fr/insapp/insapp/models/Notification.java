package fr.insapp.insapp.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

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

    @Nullable
    @SerializedName("comment")
    abstract Comment comment();

    @SerializedName("message")
    abstract String message();

    @SerializedName("seen")
    abstract boolean seen();

    @Nullable
    @SerializedName("date")
    abstract Date date();

    @SerializedName("type")
    abstract String type();

    private transient Post post;
    private transient User user;
    private transient Event event;

    public static Notification create(String id, String sender, String receiver, String content, Comment comment, String message, boolean seen, Date date, String type) {
        return new AutoParcelGson_Notification(id, sender, receiver, content, comment, message, seen, date, type);
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

