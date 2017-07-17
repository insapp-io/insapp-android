package fr.insapp.insapp.http.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.App;
import fr.insapp.insapp.models.User;
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
            credentialsPreferences.edit().putString("login", json).apply();
        }
        else if (request.url().toString().contains("login/user")) {
            credentialsPreferences.edit().putString("session", json).apply();

            try {
                final JSONObject userJson = new JSONObject(json).getJSONObject("user");

                userPreferences.edit().putString("user", userJson.toString()).apply();

                // save user data from server to local shared preferences

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
        else if (request.url().toString().contains("user") && request.method().equals("PUT")) {
            userPreferences.edit().putString("user", json).apply();
        }

        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), json)).build();
    }
}
