package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by thomas on 11/07/2017.
 */

public class SessionToken {

    @SerializedName("ExpireAt")
    private Date expireAt;

    @SerializedName("Token")
    private String token;

    @SerializedName("Id")
    private String id;

    public SessionToken(Date expireAt, String token, String id) {
        this.expireAt = expireAt;
        this.token = token;
        this.id = id;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
