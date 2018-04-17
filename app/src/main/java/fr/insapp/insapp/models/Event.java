package fr.insapp.insapp.models;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.annotations.SerializedName;

@AutoParcelGson
public abstract class Event implements Parcelable, Comparable<Event> {

    @SerializedName("ID")
    abstract String id();

    @SerializedName("name")
    abstract String name();

    @SerializedName("association")
    abstract String association();

    @SerializedName("description")
    abstract String description();

    @Nullable
    @SerializedName("participants")
    abstract List<String> attendees();

    @Nullable
    @SerializedName("maybe")
    abstract List<String> maybe();

    @Nullable
    @SerializedName("notgoing")
    abstract List<String> notgoing();

    @SerializedName("comments")
    abstract List<Comment> comments();

    @SerializedName("status")
    abstract String status();

    @SerializedName("dateStart")
    abstract Date dateStart();

    @SerializedName("dateEnd")
    abstract Date dateEnd();

    @SerializedName("image")
    abstract String image();

    @Nullable
    @SerializedName("promotions")
    abstract List<String> promotions();

    @Nullable
    @SerializedName("plateforms")
    abstract List<String> plateforms();

    @SerializedName("bgColor")
    abstract String bgColor();

    @SerializedName("fgColor")
    abstract String fgColor();

    public enum PARTICIPATE {
        YES,
        MAYBE,
        NO,
        UNDEFINED
    }

    public static Event create(String id, String name, String association, String description, List<String> attendees, List<String> maybe, List<String> notgoing, List<Comment> comments, String status, Date dateStart, Date dateEnd, String image, List<String> promotions, List<String> plateforms, String bgColor, String fgColor) {
        return new AutoParcelGson_Event(id, name, association, description, attendees, maybe, notgoing, comments, status, dateStart, dateEnd, image, promotions, plateforms, bgColor, fgColor);
    }

    public PARTICIPATE getStatusForUser(String userId) {
        if (notgoing() != null) {
            for (final String id : notgoing()) {
                if (userId.equals(id)) {
                    return Event.PARTICIPATE.NO;
                }
            }
        }

        if (maybe() != null) {
            for (final String id : maybe()) {
                if (userId.equals(id)) {
                    return Event.PARTICIPATE.MAYBE;
                }
            }
        }

        if (attendees() != null) {
            for (final String id : attendees()) {
                if (userId.equals(id)) {
                    return Event.PARTICIPATE.YES;
                }
            }
        }

        return PARTICIPATE.UNDEFINED;
    }

    @Override
    public int compareTo(@NonNull Event another) {
        return dateStart().compareTo(another.dateStart());
    }

    public String getId() {
        return id();
    }

    public String getName() {
        return name();
    }

    public String getAssociation() {
        return association();
    }

    public String getDescription() {
        return description();
    }

    public List<String> getAttendees() {
        return attendees();
    }

    public List<String> getMaybe() {
        return maybe();
    }

    public List<String> getNotGoing() {
        return notgoing();
    }

    public List<Comment> getComments() {
        return comments();
    }

    public String getStatus() {
        return status();
    }

    public Date getDateStart() {
        return dateStart();
    }

    public Date getDateEnd() {
        return dateEnd();
    }

    public String getImage() {
        return image();
    }

    public List<String> getPromotions() {
        return promotions();
    }

    public List<String> getPlateforms() {
        return plateforms();
    }

    public String getBgColor() {
        return bgColor();
    }

    public String getFgColor() {
        return fgColor();
    }
}
