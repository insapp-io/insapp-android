package fr.insapp.insapp.http.interceptors;

import java.io.IOException;

import fr.insapp.insapp.utility.Utils;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401 || response.code() == 403) {
            Utils.INSTANCE.clearAndDisconnect();
        }

        return response;
    }
}
