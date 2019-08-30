package fr.insapp.insapp.http;

import fr.insapp.insapp.BuildConfig;
import fr.insapp.insapp.http.interceptors.JsonInterceptor;
import fr.insapp.insapp.http.interceptors.TokenInterceptor;
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
        if (BuildConfig.BUILD_TYPE == "debug") {
            ROOT_URL = "https://insapp.insa-rennes.fr/api/v1/";
            CDN_URL = "https://insapp.insa-rennes.fr/cdn/";
        }
        else {
            ROOT_URL = "https://insapp.fr/api/v1/";
            CDN_URL = "https://insapp.fr/cdn/";
        }
    }

    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static JsonInterceptor jsonInterceptor = new JsonInterceptor();
    private static TokenInterceptor tokenInterceptor = new TokenInterceptor();

    private static Client client;

     private static Client createService() {
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .baseUrl(ServiceGenerator.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(jsonInterceptor);
        httpClient.addInterceptor(tokenInterceptor);
        httpClient.addInterceptor(loggingInterceptor);

         retrofit.client(httpClient.build());

        return retrofit.build().create(Client.class);
    }

    public static Client create() {
        if (ServiceGenerator.client == null) {
            ServiceGenerator.client = ServiceGenerator.createService();
        }

        return ServiceGenerator.client;
    }
}