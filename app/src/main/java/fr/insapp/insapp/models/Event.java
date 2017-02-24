package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by Antoine on 25/02/2016.
 */

public class Event implements Parcelable, Comparable<Event> {

    private String id;
    private String name, association, description;

    private ArrayList<String> attendees;
    private ArrayList<String> maybe;
    private ArrayList<String> notgoing;

    private ArrayList<Comment> comments;

    private String status;
    private Date dateStart, dateEnd;
    private String image, bgColor, fgColor;

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }

    };

    public Event(JSONObject json) throws JSONException {
        System.out.println(json);

        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.association = json.getString("association");
        this.description = json.getString("description");

        this.attendees = new ArrayList<>();
        this.maybe = new ArrayList<>();
        this.notgoing = new ArrayList<>();

        JSONArray jsonarray1 = json.optJSONArray("participants");
        if (jsonarray1 != null) {
            for (int i = 0; i < jsonarray1.length(); i++)
                attendees.add(jsonarray1.getString(i));
        }

        JSONArray jsonarray2 = json.optJSONArray("maybe");
        if (jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++)
                maybe.add(jsonarray2.getString(i));
        }

        JSONArray jsonarray3 = json.optJSONArray("notgoing");
        if (jsonarray3 != null) {
            for (int i = 0; i < jsonarray3.length(); i++)
                notgoing.add(jsonarray3.getString(i));
        }

        this.comments = new ArrayList<>();

        JSONArray jsonarray4 = json.optJSONArray("comments");
        if (jsonarray4 != null) {
            for (int i = 0; i < jsonarray4.length(); i++)
                comments.add(new Comment(jsonarray4.getJSONObject(i)));
        }

        this.status = json.getString("status");
        this.dateStart = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("dateStart"), true);
        this.dateEnd = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("dateEnd"), true);
        this.image = json.getString("image");
        this.bgColor = json.getString("bgColor");
        this.fgColor = json.getString("fgColor");
    }

    public Event(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.association = in.readString();
        this.description = in.readString();

        this.attendees = new ArrayList<>();
        this.maybe = new ArrayList<>();
        this.notgoing = new ArrayList<>();

        final int nbAttendees = in.readInt();
        if (nbAttendees > 0)
            in.readStringList(this.attendees);

        final int nbMaybe = in.readInt();
        if (nbMaybe > 0)
            in.readStringList(this.maybe);

        final int nbNotgoing = in.readInt();
        if (nbNotgoing > 0)
            in.readStringList(this.notgoing);

        this.comments = new ArrayList<>();

        final int nbComments = in.readInt();
        if (nbComments > 0)
            in.readTypedList(comments, Comment.CREATOR);

        this.status = in.readString();

        this.dateStart = new Date(in.readLong());
        this.dateEnd = new Date(in.readLong());

        this.image = in.readString();
        this.bgColor = in.readString();
        this.fgColor = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(association);
        dest.writeString(description);

        dest.writeInt(attendees.size());
        if (attendees.size() > 0)
            dest.writeStringList(attendees);

        dest.writeInt(maybe.size());
        if (maybe.size() > 0)
            dest.writeStringList(maybe);

        dest.writeInt(notgoing.size());
        if (notgoing.size() > 0)
            dest.writeStringList(notgoing);

        dest.writeInt(comments.size());
        if (comments.size() > 0)
            dest.writeTypedList(comments);

        dest.writeString(status);

        dest.writeLong(dateStart.getTime());
        dest.writeLong(dateEnd.getTime());

        dest.writeString(image);
        dest.writeString(bgColor);
        dest.writeString(fgColor);
    }

    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Event)) return false;

        Event otherMyClass = (Event) other;

        return otherMyClass.getId().equals(this.id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssociation() {
        return association;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public ArrayList<String> getMaybe() {
        return maybe;
    }

    public ArrayList<String> getNotgoing() {
        return notgoing;
    }

    public String getStatus() {
        return status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public String getImage() {
        return image;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getFgColor() {
        return fgColor;
    }

    public int describeContents() {
        // on renvoie 0, car notre classe ne contient pas de FileDescriptor
        return 0;
    }

    @Override
    public int compareTo(@NonNull Event another) {
        return this.getDateStart().compareTo(another.getDateStart());
    }
}
