package fr.insapp.insapp.http.token;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;

import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.http.retrofit.TypeAdapter;
import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

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

        if (response.code() == 401) {
            LoginCredentials loginCredentials = new Gson().fromJson(preferences.getString("login", ""), LoginCredentials.class);

            TypeAdapter loginCredentialsTypeAdapter = new TypeAdapter("credentials", LoginCredentials.class);
            TypeAdapter sessionTokenTypeAdapter = new TypeAdapter("sessionToken", SessionToken.class);
            TypeAdapter userTypeAdapter = new TypeAdapter("user", User.class);

            Call<SessionCredentials> call = ServiceGenerator.createService(Client.class, loginCredentialsTypeAdapter, sessionTokenTypeAdapter, userTypeAdapter).logUser(loginCredentials);
            SessionCredentials refreshedSessionCredentials = call.execute().body();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", refreshedSessionCredentials.getSessionToken().getToken()).build();
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        }

        return response;
    }
}