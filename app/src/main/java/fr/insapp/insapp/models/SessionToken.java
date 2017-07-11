package fr.insapp.insapp.models;

import java.util.Date;

/**
 * Created by thomas on 11/07/2017.
 */

public class SessionToken {

    private Date expireAt;
    private String token;
    private String ID;

    public SessionToken(Date expireAt, String token, String ID) {
        this.expireAt = expireAt;
        this.token = token;
        this.ID = ID;
    }
}
