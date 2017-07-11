package fr.insapp.insapp.http.retrofit;

import android.app.Service;
import android.content.SharedPreferences;

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

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static JsonInterceptor json;

    public static void setPreferences(SharedPreferences preferences) {
        ServiceGenerator.json = new JsonInterceptor(preferences);
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        if (!httpClient.interceptors().contains(json)) {
            httpClient.addInterceptor(json);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}