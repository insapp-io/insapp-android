package fr.insapp.insapp.http.token;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public TokenInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (tokenManager.hasToken()) {
            HttpUrl url = request.url().newBuilder().addQueryParameter("token", tokenManager.getToken()).build();
            request = request.newBuilder().url(url).build();
        }

        Response response = chain.proceed(request);

        // does the token need to be refreshed ? (unauthorized)

        if (response.code() == 401) {
            String newToken = tokenManager.refreshToken();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", newToken).build();
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        }

        return response;
    }
}