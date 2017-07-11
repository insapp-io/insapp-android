package fr.insapp.insapp.http.retrofit;

import android.content.SharedPreferences;

import fr.insapp.insapp.http.token.TokenInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thomas on 09/07/2017.
 */

public class ServiceGenerator {

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Client.ROOT_URL).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    // interceptors

    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static JsonInterceptor jsonInterceptor;
    private static TokenInterceptor tokenInterceptor;

    public static void setPreferences(SharedPreferences preferences) {
        ServiceGenerator.jsonInterceptor = new JsonInterceptor(preferences);
        ServiceGenerator.tokenInterceptor = new TokenInterceptor(preferences);
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(jsonInterceptor)) {
            httpClient.addInterceptor(jsonInterceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        if (!httpClient.interceptors().contains(tokenInterceptor)) {
            httpClient.addInterceptor(tokenInterceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        if (!httpClient.interceptors().contains(loggingInterceptor)) {
            httpClient.addInterceptor(loggingInterceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}