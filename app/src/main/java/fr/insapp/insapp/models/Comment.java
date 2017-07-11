package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by tomatrocho on 17/11/16.
 */

public class Comment implements Parcelable {

    private String id;
    private String user;
    private String content;
    private ArrayList<Tag> tags;
    private Date date;

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

        this.tags = new ArrayList<Tag>();

        int nb_tags = in.readInt();
        if(nb_tags > 0) {
            //in.readTypedList(tags, Tag.CREATOR);
            in.readTypedList(tags, Tag.CREATOR);
        }

        this.date = new Date(in.readLong());
    }

    public Comment(String id, String user, String content, ArrayList<Tag> tags, Date date) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.tags = tags;
        this.date = date;
    }

    public Comment(JSONObject json) throws JSONException {
        //System.out.println("JE RECOIS=" + json.toString());
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

    public String getId() {
        return id;
    }

    public String getUserId() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Tag> getTags() {
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
