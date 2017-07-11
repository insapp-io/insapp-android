package fr.insapp.insapp.http.token;

import android.widget.Toast;

import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenManager {

    private String token;

    public String refreshToken() {

        /*
        Call<SessionCredentials> call = ServiceGenerator.createService(Client.class).logUser();
        call.enqueue(new Callback<SessionCredentials>() {
            @Override
            public void onResponse(Call<SessionCredentials> call, Response<SessionCredentials> response) {
                if (response.isSuccessful()) {

                }
                else {
                    Toast.makeText(, "TokenManager", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SessionCredentials> call, Throwable t) {
                Toast.makeText(, "TokenManager", Toast.LENGTH_LONG).show();
            }
        });
        */

        return null;
    }

    public boolean hasToken() {
        return !token.isEmpty();
    }

    public String getToken() {
        return token;
    }
}
