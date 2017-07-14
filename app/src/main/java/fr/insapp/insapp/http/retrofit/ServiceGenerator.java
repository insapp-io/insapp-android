package fr.insapp.insapp.http.retrofit;

import android.content.SharedPreferences;

import com.google.gson.GsonBuilder;

import fr.insapp.insapp.activities.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thomas on 09/07/2017.
 */

public class ServiceGenerator {

    public static String ROOT_URL;
    public static String CDN_URL;

    static {
        if (MainActivity.dev) {
            ROOT_URL = "https://dev.insapp.fr/api/v1/";
            CDN_URL = "https://dev.insapp.fr/cdn/";
        }
        else {
            ROOT_URL = "https://insapp.fr/api/v1/";
            CDN_URL = "https://insapp.fr/cdn/";
        }
    }

    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static JsonInterceptor jsonInterceptor;
    private static TokenInterceptor tokenInterceptor;

    public static void setPreferences(SharedPreferences preferences) {
        ServiceGenerator.jsonInterceptor = new JsonInterceptor(preferences);
        ServiceGenerator.tokenInterceptor = new TokenInterceptor(preferences);
    }

    public static <S> S createService(Class<S> serviceClass, TypeAdapter... adapters) {
        Retrofit.Builder builder;

        if (adapters.length > 0) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            for (TypeAdapter adapter : adapters) {
                gsonBuilder.registerTypeAdapter(adapter.getType(), adapter.getDeserializer());
            }

            builder = new Retrofit.Builder().baseUrl(ServiceGenerator.ROOT_URL).addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
        }
        else {
            builder = new Retrofit.Builder().baseUrl(ServiceGenerator.ROOT_URL).addConverterFactory(GsonConverterFactory.create());
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(jsonInterceptor);
        httpClient.addInterceptor(tokenInterceptor);
        httpClient.addInterceptor(loggingInterceptor);

        builder.client(httpClient.build());

        return builder.build().create(serviceClass);
    }

    public static Client create() {
        return ServiceGenerator.createService(Client.class);
    }
}