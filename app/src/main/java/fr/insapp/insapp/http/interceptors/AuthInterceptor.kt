package fr.insapp.insapp.http.interceptors

import fr.insapp.insapp.utility.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by thomas on 10/07/2017.
 * Kotlin rewrite on 05/11/2019.
 */
class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code() == 401 || response.code() == 403) {
            Utils.clearAndDisconnect()
        }

        return response
    }
}
