package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Antoine on 19/09/2016.
 */

public class User implements Parcelable{

    @SerializedName("ID")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("description")
    private String description;

    @SerializedName("email")
    private String email;

    @SerializedName("emailpublic")
    private boolean emailPublic;

    @SerializedName("promotion")
    private String promotion;

    @SerializedName("gender")
    private String gender;

    @SerializedName("events")
    private ArrayList<String> events;

    @SerializedName("postsliked")
    private ArrayList<String> postsLiked;

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };

    public User(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.description = in.readString();
        this.email = in.readString();
        this.emailPublic = in.readByte() != 0;
        this.promotion = in.readString();
        this.gender = in.readString();

        this.events = new ArrayList<>();
        int nb_events = in.readInt();
        if(nb_events > 0) {
            in.readStringList(this.events);
        }

        this.postsLiked = new ArrayList<>();
        int nb_postsLiked = in.readInt();
        if(nb_postsLiked > 0) {
            in.readStringList(this.postsLiked);
        }
    }

    public User(String id, String name, String username, String description, String email, boolean emailPublic, String promotion, String gender, ArrayList<String> events, ArrayList<String> postsLiked) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.description = description;
        this.email = email;
        this.emailPublic = emailPublic;
        this.promotion = promotion;
        this.gender = gender;
        this.events = events;
        this.postsLiked = postsLiked;
    }

    public User(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.username = json.getString("usernameTextView");
        this.description = json.getString("description");
        this.email = json.getString("email");
        this.emailPublic = json.getBoolean("emailpublic");
        this.promotion = json.getString("promotion");
        this.gender = json.getString("gender");

        events = new ArrayList<>();

        JSONArray jsonarray = json.optJSONArray("events");
        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++) {
                events.add(jsonarray.getString(i));
            }
        }

        postsLiked = new ArrayList<>();

        JSONArray jsonarray2 = json.optJSONArray("postsliked");
        if(jsonarray2 != null){
            for(int i = 0; i < jsonarray2.length(); i++) {
                postsLiked.add(jsonarray2.getString(i));
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeByte((byte) (emailPublic ? 1 : 0));
        dest.writeString(promotion);
        dest.writeString(gender);

        dest.writeInt(events.size());
        if (events.size() > 0)
            dest.writeStringList(events);

        dest.writeInt(postsLiked.size());
        if (postsLiked.size() > 0)
            dest.writeStringList(postsLiked);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailPublic() {
        return emailPublic;
    }

    public void setEmailPublic(boolean emailPublic) {
        this.emailPublic = emailPublic;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }

    public ArrayList<String> getPostsLiked() {
        return postsLiked;
    }

    public void setPostsLiked(ArrayList<String> postsLiked) {
        this.postsLiked = postsLiked;
    }

    @Override
    public String toString() {
        return username;
    }
}