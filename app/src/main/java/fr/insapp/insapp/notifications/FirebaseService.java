package fr.insapp.insapp.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.App;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.NotificationUser;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 18/07/2017.
 */

public class FirebaseService extends FirebaseInstanceIdService {

    public final static String TAG = "FA";

    public static boolean SHOULD_REGISTER_TOKEN = false;

    @Override
    public void onTokenRefresh() {
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences firebaseCredentialsPreferences = App.getAppContext().getSharedPreferences("FirebaseCredentials", Context.MODE_PRIVATE);
        firebaseCredentialsPreferences.edit().putString("token", refreshedToken).apply();

        Log.d(FirebaseService.TAG, "Refreshed token: " + refreshedToken);

        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
        final SharedPreferences userPreferences = App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        if (gson.fromJson(userPreferences.getString("user", ""), User.class) != null) {
            FirebaseService.registerToken(firebaseCredentialsPreferences.getString("token", ""));
        }
        else {
            FirebaseService.SHOULD_REGISTER_TOKEN = true;
        }
    }

    public static void registerToken(final String token) {
        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
        final User user = gson.fromJson(App.getAppContext().getSharedPreferences("User", MODE_PRIVATE).getString("user", ""), User.class);

        NotificationUser notificationUser = new NotificationUser(null, user.getId(), token, "android");

        Call<NotificationUser> call = ServiceGenerator.create().registerNotification(notificationUser);
        call.enqueue(new Callback<NotificationUser>() {
            @Override
            public void onResponse(@NonNull Call<NotificationUser> call, @NonNull Response<NotificationUser> response) {
                if (response.isSuccessful()) {
                    Log.d(FirebaseService.TAG, "Firebase token successfully registered on server: " + token);
                }
                else {
                    Toast.makeText(App.getAppContext(), "FirebaseService", Toast.LENGTH_LONG).show();

                    FirebaseService.SHOULD_REGISTER_TOKEN = true;
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationUser> call, @NonNull Throwable t) {
                Toast.makeText(App.getAppContext(), "FirebaseService", Toast.LENGTH_LONG).show();

                FirebaseService.SHOULD_REGISTER_TOKEN = true;
            }
        });
    }
}
