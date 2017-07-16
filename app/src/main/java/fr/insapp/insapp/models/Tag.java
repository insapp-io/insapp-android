package fr.insapp.insapp.models;

import android.os.Parcelable;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by Antoine on 12/10/2016.
 */

@AutoParcelGson
public abstract class Tag implements Parcelable {

    @SerializedName("ID")
    abstract String id();

    @SerializedName("user")
    abstract String user();

    @SerializedName("name")
    abstract String name();

    static Tag create(String id, String user, String name) {
        return new AutoParcelGson_Tag(id, user, name);
    }

    public String getId() {
        return id();
    }

    public String getUser() {
        return user();
    }

    public String getName() {
        return name();
    }
}
