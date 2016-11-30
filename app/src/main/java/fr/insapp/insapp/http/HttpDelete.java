package fr.insapp.insapp.http;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Antoine on 25/09/2016.
 */
public class HttpDelete extends AsyncTask<String, Void, String> {

    /**
     * Interface pour call back
     */
    public AsyncResponse delegate = null;

    public HttpDelete(AsyncResponse asyncResponse) {
        delegate = asyncResponse; //Assigning call back interfacethrough constructor
    }

    /**
     * Methode qui realise le traitement de maniere asynchrone dans un Thread separe
     *
     * @param params url
     * @return Donnees au format JSON
     */
    protected String doInBackground(String... params) {

        URL url = null;
        try {
            url = new URL(params[0]);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }

        HttpURLConnection httpURLConnection = null;
        StringBuffer response = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("DELETE");

            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        if(response != null)
            return response.toString();

        return "";
    }

    /**
     * Methode appelee apres le traitement
     *
     * @param jsonObject Donnees JSON recuperees
     */
    protected void onPostExecute(String jsonObject) {
        delegate.processFinish(jsonObject);
    }
}