package fr.insapp.insapp.http

import fr.insapp.insapp.BuildConfig
import fr.insapp.insapp.http.interceptors.JsonInterceptor
import fr.insapp.insapp.http.interceptors.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by thomas on 09/07/2017.
 * Kotlin rewrite on 02/09/2019.
 */

object ServiceGenerator {

    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val jsonInterceptor = JsonInterceptor()
    private val tokenInterceptor = TokenInterceptor()

    var ROOT_URL: String
    var CDN_URL: String

    init {
        if (BuildConfig.BUILD_TYPE === "debug") {
            ROOT_URL = "https://insapp.insa-rennes.fr/api/v1/"
            CDN_URL = "https://insapp.insa-rennes.fr/cdn/"
        } else {
            ROOT_URL = "https://insapp.fr/api/v1/"
            CDN_URL = "https://insapp.fr/cdn/"
        }
    }

    val client = createService()

    private fun createService(): Client {
        val retrofit = Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(jsonInterceptor)
        httpClient.addInterceptor(tokenInterceptor)
        httpClient.addInterceptor(loggingInterceptor)

        retrofit.client(httpClient.build())

        return retrofit.build().create(Client::class.java)
    }
}