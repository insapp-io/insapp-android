package fr.insapp.insapp.http.retrofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;

import fr.insapp.insapp.activities.IntroActivity;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.models.credentials.SigninCredentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    private Context context;
    private SharedPreferences preferences;

    public TokenInterceptor(Context context, SharedPreferences preferences) {
        this.context = context;
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

        if (response.code() == 401) {
            LoginCredentials loginCredentials = new Gson().fromJson(preferences.getString("login", ""), LoginCredentials.class);

            Call<SessionCredentials> call = ServiceGenerator.create().logUser(loginCredentials);
            SessionCredentials refreshedSessionCredentials = call.execute().body();

            // if the user connected from another device, display signin form

            if (!new Gson().fromJson(preferences.getString("signin", ""), SigninCredentials.class).getDevice().equals(refreshedSessionCredentials.getLoginCredentials().getDevice())) {
                context.startActivity(new Intent(context, IntroActivity.class));
            }

            HttpUrl url = request.url().newBuilder().setQueryParameter("token", refreshedSessionCredentials.getSessionToken().getToken()).build();
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        }

        return response;
    }
}