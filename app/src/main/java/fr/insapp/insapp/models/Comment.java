package fr.insapp.insapp.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by thomas on 17/11/16.
 */

@AutoParcelGson
public abstract class Comment implements Parcelable {

    @Nullable
    @SerializedName("ID")
    abstract String id();

    @SerializedName("user")
    abstract String user();

    @SerializedName("content")
    abstract String content();

    @Nullable
    @SerializedName("date")
    abstract Date date();

    @SerializedName("tags")
    abstract List<Tag> tags();

    public static Comment create(String id, String user, String content, Date date, List<Tag> tags) {
        return new AutoParcelGson_Comment(id, user, content, date, tags);
    }

    public String getId() {
        return id();
    }

    public String getUser() {
        return user();
    }

    public String getContent() {
        return content();
    }

    public Date getDate() {
        return date();
    }

    public List<Tag> getTags() {
        return tags();
    }
}
