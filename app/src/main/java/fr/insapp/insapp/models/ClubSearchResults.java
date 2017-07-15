package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 15/07/2017.
 */

public class ClubSearchResults {

    @SerializedName("associations")
    private List<Club> clubs;

    public ClubSearchResults(List<Club> clubs) {
        this.clubs = clubs;
    }

    public List<Club> getClubs() {
        return clubs;
    }
}
