package fr.insapp.insapp.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

/**
 * Created by thomas on 29/10/2016.
 */

@AutoParcelGson
public abstract class Post implements Parcelable {

    @SerializedName("ID")
    abstract String id();

    @SerializedName("title")
    abstract String title();

    @SerializedName("association")
    abstract String association();

    @SerializedName("description")
    abstract String description();

    @SerializedName("date")
    abstract Date date();

    @SerializedName("likes")
    abstract List<String> likes();

    @SerializedName("comments")
    abstract List<Comment> comments();

    @Nullable
    @SerializedName("promotions")
    abstract List<String> promotions();

    @Nullable
    @SerializedName("plateforms")
    abstract List<String> plateforms();

    @SerializedName("image")
    abstract String image();

    @SerializedName("imageSize")
    abstract ImageSize imageSize();

    @SerializedName("nonotification")
    abstract boolean noNotification();

    static Post create(String id, String title, String association, String description, Date date, List<String> likes, List<Comment> comments, List<String> promotions, List<String> plateforms, String image, ImageSize imageSize, boolean noNotification) {
        return new AutoParcelGson_Post(id, title, association, description, date, likes, comments, promotions, plateforms, image, imageSize, noNotification);
    }

    public boolean isPostLikedBy(String userID) {
        for (String idUser : likes()) {
            if (idUser.equals(userID))
                return true;
        }

        return false;
    }

    public String getId() {
        return id();
    }

    public String getTitle() {
        return title();
    }

    public String getAssociation() {
        return association();
    }

    public String getDescription() {
        return description();
    }

    public Date getDate() {
        return date();
    }

    public List<String> getLikes() {
        return likes();
    }

    public List<Comment> getComments() {
        return comments();
    }

    public List<String> getPromotions() {
        return promotions();
    }

    public List<String> getPlateforms() {
        return plateforms();
    }

    public String getImage() {
        return image();
    }

    public ImageSize getImageSize() {
        return imageSize();
    }

    public boolean isNoNotification() {
        return noNotification();
    }
}
