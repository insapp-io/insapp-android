package fr.insapp.insapp.models.credentials;

import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;

/**
 * Created by thomas on 11/07/2017.
 */

public class SessionCredentials {

    private LogInCredentials logInCredentials;
    private SessionToken sessionToken;
    private User user;

    public SessionCredentials(LogInCredentials logInCredentials, SessionToken sessionToken, User user) {
        this.logInCredentials = logInCredentials;
        this.sessionToken = sessionToken;
        this.user = user;
    }
}
