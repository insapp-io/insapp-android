package fr.insapp.insapp.models;

import android.os.Parcelable;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by thomas on 12/07/2017.
 */

@AutoParcelGson
public abstract class ImageSize implements Parcelable {

    @SerializedName("width")
    abstract int width();

    @SerializedName("height")
    abstract int height();

    static ImageSize create(int width, int height) {
        return new AutoParcelGson_ImageSize(width, height);
    }

    public int getWidth() {
        return width();
    }

    public int getHeight() {
        return height();
    }
}
