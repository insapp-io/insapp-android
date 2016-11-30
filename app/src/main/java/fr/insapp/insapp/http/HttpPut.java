package fr.insapp.insapp.http;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Antoine on 01/10/2016.
 */
public class HttpPut extends AsyncTask<String, Void, String> {

    /**
     * Interface pour call back
     */
    public AsyncResponse delegate = null;

    public HttpPut(AsyncResponse asyncResponse) {
        delegate = asyncResponse; //Assigning call back interfacethrough constructor
    }

    /**
     * Methode qui realise le traitement de maniere asynchrone dans un Thread separe
     *
     * @param params url
     * @return Donnees au format JSON
     */
    protected String doInBackground(String... params) {

        StringBuffer response = null;
        try {
            URL obj = new URL(params[0]);

            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("PUT");

            if(params.length > 1) {
                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());

                out.write(params[1]);
                out.close();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception e){
            System.out.println(e.getMessage());
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