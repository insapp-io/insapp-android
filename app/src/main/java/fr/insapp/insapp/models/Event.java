package fr.insapp.insapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by Antoine on 25/02/2016.
 *
 * type Event struct {
 ID bson.ObjectId `bson:"_id,omitempty"`
 Name string `json:"name"`
 Association bson.ObjectId `json:"association" bson:"association"`
 Description string `json:"description"`
 Participants []bson.ObjectId `json:"participants" bson:"participants,omitempty"`
 Status string `json:"status"`
 DateStart time.Time `json:"dateStart"`
 dateEnd time.Time `json:"dateEnd"`
 PhotoURL string `json:"image"`
 BgColor string `json:"bgColor"`
 FgColor string `json:"fgColor"`
 }
 */
public class Event implements Parcelable, Comparable<Event> {

    private String id;
    private String name, association, description;
    private ArrayList<String> participants;
    private String status;
    private Date dateStart, dateEnd;
    private String image, bgColor, fgColor;

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }

    };

    public Event(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.association = in.readString();
        this.description = in.readString();

        this.participants = new ArrayList<String>();
        int nb_participants = in.readInt();
        if(nb_participants > 0) {
            in.readStringList(this.participants);
        }

        this.status = in.readString();

        this.dateStart = new Date(in.readLong());
        this.dateEnd = new Date(in.readLong());

        this.image = in.readString();
        this.bgColor = in.readString();
        this.fgColor = in.readString();
    }

    public Event(String id, String name, String association, String description, ArrayList<String> participants, String status, Date dateStart, Date dateEnd, String image, String bgColor, String fgColor) {
        this.id = id;
        this.name = name;
        this.association = association;
        this.description = description;
        this.participants = participants;
        this.status = status;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.image = image;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public Event(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.association = json.getString("association");
        this.description = json.getString("description");
        this.participants = new ArrayList<String>();

        JSONArray jsonarray = json.optJSONArray("participants");
        if(jsonarray != null){
            for(int i=0; i<jsonarray.length(); i++) {
                participants.add(jsonarray.getString(i));
            }
        }

        this.status = json.getString("status");
        System.out.println(json.getString("dateStart") + " et " + json.getString("dateEnd"));
        this.dateStart = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("dateStart"), true);
        this.dateEnd = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("dateEnd"), true);
        this.image = json.getString("image");
        this.bgColor = json.getString("bgColor");
        this.fgColor = json.getString("fgColor");
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

    public ArrayList<String> getParticipants() {
        return participants;
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
        return 0; //On renvoie 0, car notre classe ne contient pas de FileDescriptor
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(association);
        dest.writeString(description);

        dest.writeInt(participants.size());
        if(participants.size() > 0)
            dest.writeStringList(participants);

        dest.writeString(status);
        dest.writeLong(dateStart.getTime());
        dest.writeLong(dateEnd.getTime());
        dest.writeString(image);
        dest.writeString(bgColor);
        dest.writeString(fgColor);
    }

    @Override
    public int compareTo(Event another) {
        return this.getDateStart().compareTo(another.getDateStart());
    }
}
