package fr.insapp.insapp.modeles;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Antoine on 12/10/2016.
 */
public class Tag implements Parcelable {
    private String id;
    private String user, name;

    public Tag(String id, String user, String name) {
        this.id = id;
        this.user = user;
        this.name = name;
    }

    public Tag(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.user = json.getString("user");
        this.name = json.getString("name");

        //System.out.println("NEW TAG from json : " + name);
    }

    protected Tag(Parcel in) {
        id = in.readString();
        user = in.readString();
        name = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(name);
    }
}
