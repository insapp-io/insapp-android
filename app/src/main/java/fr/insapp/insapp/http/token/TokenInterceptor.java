package fr.insapp.insapp.http.token;

import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;

import com.google.gson.Gson;

import java.io.IOException;

import fr.insapp.insapp.models.credentials.SessionCredentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    private SharedPreferences preferences;

    public TokenInterceptor(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        SessionCredentials sessionCredentials = new Gson().fromJson(preferences.getString("session", ""), SessionCredentials.class);

        if (sessionCredentials != null) {
            final String sessionToken = sessionCredentials.getSessionToken().getToken();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", sessionToken).build();
            request = request.newBuilder().url(url).build();
        }

        Response response = chain.proceed(request);

        // does the token need to be refreshed ? (unauthorized)

        /*
        if (response.code() == 401) {
            String newToken = tokenManager.refreshToken();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", newToken).build();
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        }
        */

        return response;
    }
}