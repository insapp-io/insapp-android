package fr.insapp.insapp.models;

import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;
import fr.insapp.insapp.App;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.utility.Operation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static Notification create(Map<String, String> data) {
        Notification notification = null;

        try {
            JSONObject jsonComment = new JSONObject(data.get("comment"));
            JSONArray jsonTags = null;

            if (!jsonComment.isNull("tags")) {
                jsonTags = jsonComment.getJSONArray("tags");
            }

            List<Tag> tags = new ArrayList<>();

            if (jsonTags != null) {
                for (int i = 0; i < jsonTags.length(); ++i) {
                    JSONObject jsonTag = jsonTags.getJSONObject(i);

                    tags.add(Tag.create(
                            jsonTag.getString("ID"),
                            jsonTag.getString("user"),
                            jsonTag.getString("name")
                    ));
                }
            }

            Comment comment = null;

            if (!jsonComment.getString("ID").equals("")) {
                comment = Comment.create(
                        jsonComment.getString("ID"),
                        jsonComment.getString("user"),
                        jsonComment.getString("content"),
                        Operation.parseMongoDate(jsonComment.getString("date")),
                        tags);
            }

            notification = Notification.create(
                    data.get("ID"),
                    data.get("sender"),
                    data.get("receiver"),
                    data.get("content"),
                    comment,
                    data.get("message"),
                    Boolean.parseBoolean(data.get("seen")),
                    Operation.parseMongoDate(data.get("date")),
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

