package fr.insapp.insapp.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.insapp.insapp.activities.MainActivity;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.credentials.SessionCredentials;

/**
 * Created by Antoine on 19/09/2016.
 */
public class HttpGet extends AsyncTask<String, Void, String> {

    /**
     * Interface pour call back
     */
    public AsyncResponse delegate = null;

    public static String info_user;

    public static final String ROOTURL;
    public static final String IMAGEURL;

    static {
        if (MainActivity.dev) {
            ROOTURL = "https://dev.insapp.fr/api/v1";
            IMAGEURL = "https://dev.insapp.fr/cdn/";
        } else {
            ROOTURL = "https://insapp.fr/api/v1";
            IMAGEURL = "https://insapp.fr/cdn/";
        }
    }

    public static final String ROOTSIGNIN = HttpGet.ROOTURL + "/signin/user";
    public static final String ROOTLOGIN = HttpGet.ROOTURL + "/login/user";
    public static final String ROOTPOST = HttpGet.ROOTURL + "/post";
    public static final String ROOTEVENT = HttpGet.ROOTURL + "/event";
    public static final String ROOTASSOCIATION = HttpGet.ROOTURL + "/association";
    public static final String ROOTUSER = HttpGet.ROOTURL + "/user";
    public static final String ROOTNOTIFICATION = HttpGet.ROOTURL + "/notification";

    public static final String ROOTSEARCHUSERS = HttpGet.ROOTURL + "/search/users";
    public static final String ROOTSEARCHPOSTS = HttpGet.ROOTURL + "/search/posts";
    public static final String ROOTSEARCHASSOCIAITIONS = HttpGet.ROOTURL + "/search/associations";
    public static final String ROOTSEARCHEVENTS = HttpGet.ROOTURL + "/search/events";

    public static final String ROOTSEACHUNIVERSAL = HttpGet.ROOTURL + "/search";

    public static SessionCredentials sessionCredentials;

    public static Map<String, Club> clubs = new HashMap<>();

    /**
     * Constructeur de la classe
     * @param asyncResponse Interface pour call back
     */
    public HttpGet(AsyncResponse asyncResponse) {
        delegate = asyncResponse; //Assigning call back interfacethrough constructor
    }

    public String get(String url){
        URL obj = null;
        HttpURLConnection con = null;
        StringBuffer response = null;
        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception e){

        }

        if(response == null)
            return "";

        return response.toString();
    }

    /**
     * Methode qui realise le traitement de maniere asynchrone dans un Thread separe
     * @param params url
     * @return Donnees au format JSON
     */
    protected String doInBackground(String... params) {
        return get(params[0]);
    }

    /**
     * Methode appelee apres le traitement
     * @param json Donnees JSON recuperees
     */
    protected void onPostExecute(String json) {
        delegate.processFinish(json);
    }
}
