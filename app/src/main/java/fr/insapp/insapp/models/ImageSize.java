package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 12/07/2017.
 */

public class ImageSize {

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public ImageSize(int width, int height) {
        this.width = width;
        this.height = height;
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
}
