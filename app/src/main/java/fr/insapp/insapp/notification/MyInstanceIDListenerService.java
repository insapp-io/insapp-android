package fr.insapp.insapp.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;

/**
 * Created by Antoine on 06/03/2017.
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token){

        System.out.println("TOKEN : " + token);
        JSONObject notuser = new JSONObject();
        try {
            /*
            notuser.put("userid", HttpGet.sessionCredentials.getUserID());
            */
            notuser.put("token", token);
            notuser.put("os", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpPost post = new HttpPost(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                System.out.println(output);
            }
        });
        /*
        post.execute(HttpGet.ROOTNOTIFICATION + "?token=" + HttpGet.sessionCredentials.getSessionToken(), notuser.toString());
        */
    }
}