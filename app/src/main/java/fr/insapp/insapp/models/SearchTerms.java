package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 13/07/2017.
 */

public class SearchTerms {

    @SerializedName("terms")
    private String terms;

    public SearchTerms(String terms) {
        this.terms = terms;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }
}
