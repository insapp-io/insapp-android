package fr.insapp.insapp.http.interceptors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.App;
import fr.insapp.insapp.activities.IntroActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.notifications.FirebaseService;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        final SharedPreferences credentialsPreferences = App.getAppContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();

        // if user is stored, register firebase token

        if (FirebaseService.SHOULD_REGISTER_TOKEN) {
            final SharedPreferences userPreferences = App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE);
            final SharedPreferences firebaseCredentialsPreferences = App.getAppContext().getSharedPreferences("FirebaseCredentials", Context.MODE_PRIVATE);

            if (gson.fromJson(userPreferences.getString("user", ""), User.class) != null) {
                FirebaseService.SHOULD_REGISTER_TOKEN = false;

                FirebaseService.registerToken(firebaseCredentialsPreferences.getString("token", ""));
            }
        }

        // add session token in a query parameter

        SessionCredentials sessionCredentials = gson.fromJson(credentialsPreferences.getString("session", ""), SessionCredentials.class);

        if (sessionCredentials != null) {
            final String sessionToken = sessionCredentials.getSessionToken().getToken();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", sessionToken).build();
            request = request.newBuilder().url(url).build();
        }

        Response response = chain.proceed(request);

        switch (response.code()) {

            // does the token need to be refreshed ? (unauthorized)

            case 401:
                LoginCredentials loginCredentials = new Gson().fromJson(credentialsPreferences.getString("login", ""), LoginCredentials.class);

                Call<SessionCredentials> call = ServiceGenerator.create().logUser(loginCredentials);
                retrofit2.Response<SessionCredentials> res = call.execute();

                if (res.code() == 404) {
                    Context context = App.getAppContext();
                    context.startActivity(new Intent(context, IntroActivity.class));

                    return response;
                }

                SessionCredentials refreshedSessionCredentials = res.body();

                HttpUrl url = request.url().newBuilder().setQueryParameter("token", refreshedSessionCredentials.getSessionToken().getToken()).build();
                request = request.newBuilder().url(url).build();

                return chain.proceed(request);

            // did the user log in somewhere else ?

            case 404:
                Context context = App.getAppContext();
                context.startActivity(new Intent(context, IntroActivity.class));

            default:
                return response;
        }
    }
}