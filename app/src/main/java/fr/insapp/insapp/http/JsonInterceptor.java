package fr.insapp.insapp.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

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
            credentialsPreferences.edit().putString("login", json).apply();
        }
        else if (request.url().toString().contains("login/user")) {
            credentialsPreferences.edit().putString("session", json).apply();

            try {
                userPreferences.edit().putString("user", new JSONObject(json).getJSONObject("user").toString()).apply();
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
