package fr.insapp.insapp.http;

import android.content.Context;
import android.content.SharedPreferences;

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
        SharedPreferences preferences = App.getAppContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE);

        Response response = chain.proceed(request);
        String json = response.body().string();

        if (request.url().toString().contains("signin/user")) {
            preferences.edit().putString("login", json).apply();
        }
        else if (request.url().toString().contains("login/user")) {
            preferences.edit().putString("session", json).apply();
        }

        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), json)).build();
    }
}
