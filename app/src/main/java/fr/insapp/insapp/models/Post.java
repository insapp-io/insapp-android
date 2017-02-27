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
 * Created by thoma on 29/10/2016.
 */

public class Post implements Parcelable {

    private String id;
    private String title, association, description;
    private Date date;

    private ArrayList<String> likes;
    private ArrayList<Comment> comments;

    private String image;
    private int width;
    private int height;

    public static final Creator<Post> CREATOR = new Creator<Post>() {

        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Post(JSONObject json) throws JSONException {
        refresh(json);
    }

    protected Post(Parcel in) {
        id = in.readString();
        title = in.readString();
        association = in.readString();
        description = in.readString();
        date = new Date(in.readLong());

        this.likes = new ArrayList<>();

        final int nbLikes = in.readInt();
        if (nbLikes > 0)
            in.readStringList(likes);

        this.comments = new ArrayList<>();

        final int nbComments = in.readInt();
        if (nbComments > 0)
            in.readTypedList(comments, Comment.CREATOR);

        image = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    public void refresh(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.title = json.getString("title");
        this.association = json.getString("association");
        this.description = json.getString("description");
        this.date = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", json.getString("date"), true);

        this.likes = new ArrayList<>();

        final JSONArray jsonarray1 = json.optJSONArray("likes");
        if (jsonarray1 != null) {
            for (int i = 0; i < jsonarray1.length(); i++)
                likes.add(jsonarray1.getString(i));
        }

        this.comments = new ArrayList<>();

        final JSONArray jsonarray2 = json.optJSONArray("comments");
        if (jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++)
                comments.add(new Comment(jsonarray2.getJSONObject(i)));
        }

        this.image = json.getString("image");
        this.width = json.getJSONObject("imageSize").getInt("width");
        this.height = json.getJSONObject("imageSize").getInt("width");
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(association);
        dest.writeString(description);
        dest.writeLong(date.getTime());

        dest.writeInt(likes.size());
        if (likes.size() > 0)
            dest.writeStringList(likes);

        dest.writeInt(comments.size());
        if (comments.size() > 0)
            dest.writeTypedList(comments);

        dest.writeString(image);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Post)) return false;

        final Post otherMyClass = (Post) other;

        return otherMyClass.getId().equals(this.id);
    }

    public boolean isPostLikedBy(String userID) {
        for (String idUser : likes) {
            if (idUser.equals(userID))
                return true;
        }

        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
