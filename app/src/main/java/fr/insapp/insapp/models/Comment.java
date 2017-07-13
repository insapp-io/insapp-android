package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by thomas on 17/11/16.
 */

public class Comment implements Parcelable {

    @SerializedName("ID")
    private String id;

    @SerializedName("user")
    private String user;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private Date date;

    @SerializedName("tags")
    private List<Tag> tags;

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }

    };

    public Comment(Parcel in){
        this.id = in.readString();
        this.user = in.readString();
        this.content = in.readString();

        this.tags = new ArrayList<>();

        int nb_tags = in.readInt();
        if(nb_tags > 0) {
            in.readTypedList(tags, Tag.CREATOR);
        }

        this.date = new Date(in.readLong());
    }

    public Comment(String id, String user, String content, List<Tag> tags, Date date) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.tags = tags;
        this.date = date;
    }

    public Comment(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.user = json.getString("user");
        this.content = json.getString("content");

        this.tags = new ArrayList<>();

        JSONArray jsonarray = json.optJSONArray("tags");
        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++)
                tags.add(new Tag(jsonarray.getJSONObject(i)));
        }

        this.date = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", json.getString("dateTextView"), true);
        //if(this.dateTextView == null)
        //    this.dateTextView = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", json.getString("dateTextView"));
        //if(this.dateTextView == null)
        //    this.dateTextView = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("dateTextView"));
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Date getDate() {
        return date;
    }

    public int describeContents() {
        return 0; //On renvoie 0, car notre classe ne contient pas de FileDescriptor
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(content);

        dest.writeInt(tags.size());
        if (tags.size() > 0)
            dest.writeTypedList(tags);

        dest.writeLong(date.getTime());
    }
}
