package fr.insapp.insapp.http.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.insapp.insapp.App;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by thomas on 11/07/2017.
 */

public class JsonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        SharedPreferences credentialsPreferences = App.getAppContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        SharedPreferences userPreferences = App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        Response response = chain.proceed(request);
        String json = response.body().string();

        if (request.url().toString().contains("signin/user")) {
            if (isJsonValid(json)) {
                credentialsPreferences.edit().putString("login", json).apply();
            }
        }
        else if (request.url().toString().contains("login/user")) {
            if (isJsonValid(json)) {
                credentialsPreferences.edit().putString("session", json).apply();
            }

            // retrieve user data from server

            try {
                final JSONObject userJson = new JSONObject(json).getJSONObject("user");

                userPreferences.edit().putString("user", userJson.toString()).apply();

                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
                SharedPreferences.Editor editor = defaultSharedPreferences.edit();

                editor.putString("name", userJson.getString("name"));
                editor.putString("description", userJson.getString("description"));
                editor.putString("email", userJson.getString("email"));
                editor.putString("class", userJson.getString("promotion"));
                editor.putString("sex", userJson.getString("gender"));

                editor.apply();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        // save user data from server to local shared preferences

        else if (request.url().toString().contains("user") && request.method().equals("PUT")) {
            userPreferences.edit().putString("user", json).apply();
        }

        else if (request.url().toString().contains("participant") && request.method().equals("POST") ||
                 request.url().toString().contains("like") && (request.method().equals("POST") || request.method().equals("DELETE"))) {
            try {
                userPreferences.edit().putString("user", new JSONObject(json).getJSONObject("user").toString()).apply();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), json)).build();
    }

    private boolean isJsonValid(String json) {
        try {
            new JSONObject(json);
        }
        catch (JSONException ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }
}
