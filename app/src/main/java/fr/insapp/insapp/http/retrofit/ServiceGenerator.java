package fr.insapp.insapp.http.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LogInCredentials;
import fr.insapp.insapp.models.deserializer.Deserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thomas on 09/07/2017.
 */

public class ServiceGenerator {

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    static {
        gsonBuilder.registerTypeAdapter(LogInCredentials.class, new Deserializer<>("credentials"));
        gsonBuilder.registerTypeAdapter(SessionToken.class, new Deserializer<>("sessionToken"));
        gsonBuilder.registerTypeAdapter(User.class, new Deserializer<>("user"));
    }

    private static Gson gson = gsonBuilder.create();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Client.ROOT_URL).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}