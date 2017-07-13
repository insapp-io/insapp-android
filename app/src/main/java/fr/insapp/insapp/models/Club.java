package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Antoine on 25/02/2016.
 */

public class Club implements Parcelable {

    @SerializedName("ID")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("description")
    private String description;

    @SerializedName("events")
    private List<String> events;

    @SerializedName("posts")
    private List<String> posts;

    @SerializedName("profile")
    private String profilPicture;

    @SerializedName("cover")
    private String cover;

    @SerializedName("bgcolor")
    private String bgColor;

    @SerializedName("fgcolor")
    private String fgColor;

    public static final Parcelable.Creator<Club> CREATOR = new Parcelable.Creator<Club>() {

        @Override
        public Club createFromParcel(Parcel source) {
            return new Club(source);
        }

        @Override
        public Club[] newArray(int size) {
            return new Club[size];
        }

    };

    public Club(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.description = in.readString();

        this.events = new ArrayList<>();
        int nb_events = in.readInt();
        if (nb_events > 0)
            in.readStringList(this.events);

        this.posts = new ArrayList<>();
        int nb_posts = in.readInt();
        if (nb_posts > 0)
            in.readStringList(this.posts);

        this.profilPicture = in.readString();
        this.cover = in.readString();
        this.bgColor = in.readString();
        this.fgColor = in.readString();
    }

    public Club(String id, String name, String email, String description, ArrayList<String> events, ArrayList<String> posts, String profilPicture, String cover, String bgColor, String fgColor) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.events = events;
        this.posts = posts;
        this.profilPicture = profilPicture;
        this.cover = cover;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public Club(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.email = json.getString("email");
        this.description = json.getString("description");
        this.events = new ArrayList<>();

        JSONArray jsonarray = json.optJSONArray("events");

        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++)
                events.add(jsonarray.getString(i));
        }

        this.posts = new ArrayList<>();

        JSONArray jsonarray2 = json.optJSONArray("posts");

        if (jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++)
                posts.add(jsonarray2.getString(i));
        }

        this.profilPicture = json.getString("profile");
        this.cover = json.getString("cover");
        this.bgColor = json.getString("bgcolor");
        this.fgColor = json.getString("fgcolor");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getEvents() {
        return events;
    }

    public List<String> getPosts() {
        return posts;
    }

    public String getProfilPicture() {
        return profilPicture;
    }

    public String getCover() {
        return cover;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getFgColor() {
        return fgColor;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(description);
        dest.writeInt(events.size());

        if (events.size() > 0)
            dest.writeStringList(events);

        dest.writeInt(posts.size());

        if (posts.size() > 0)
            dest.writeStringList(posts);

        dest.writeString(profilPicture);
        dest.writeString(cover);
        dest.writeString(bgColor);
        dest.writeString(fgColor);
    }
}