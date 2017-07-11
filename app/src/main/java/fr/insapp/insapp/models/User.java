package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Antoine on 19/09/2016.
 *
 * type User struct {
 ID          bson.ObjectId   `bson:"_id,omitempty"`
 Name        string          `json:"name"`
 Username    string          `json:"username"`
 Description string          `json:"description"`
 Email       string          `json:"email"`
 EmailPublic bool            `json:"emailpublic"`
 Promotion   string          `json:"promotion"`
 Gender 			string					`json:"gender"`
 Events      []bson.ObjectId `json:"events"`
 PostsLiked []bson.ObjectId `json:"postsliked"`
 }
 */

public class User implements Parcelable{

    private String ID;
    private String name, username, description, email;
    private boolean emailPublic;
    private String promotion, gender;
    private ArrayList<String> events, postsLiked;

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
        this.ID = in.readString();
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
        this.ID = id;
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
        this.ID = json.getString("ID");
        this.name = json.getString("name");
        this.username = json.getString("username");
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
            for(int i=0; i<jsonarray2.length(); i++) {
                postsLiked.add(jsonarray2.getString(i));
            }
        }
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailPublic() {
        return emailPublic;
    }

    public String getPromotion() {
        return promotion;
    }

    public String getGender() {
        return gender;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public ArrayList<String> getPostsLiked() {
        return postsLiked;
    }


    public int describeContents() {
        return 0; //On renvoie 0, car notre classe ne contient pas de FileDescriptor
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
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

    @Override
    public String toString() {
        return username;
    }
}