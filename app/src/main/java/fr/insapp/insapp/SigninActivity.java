package fr.insapp.insapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LogInCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.models.credentials.SignInCredentials;
import fr.insapp.insapp.models.deserializer.Deserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SigninActivity extends AppCompatActivity {

    private final String CAS_URL = "https://cas.insa-rennes.fr/cas/login?service=https://insapp.fr/";

    public static boolean refreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        refreshing = false;

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        final WebView webView = (WebView) findViewById(R.id.webview_conditions);

        webView.loadUrl(CAS_URL);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                final int id = url.lastIndexOf("?ticket=");
                if (url.contains("?ticket=")) {
                    final String ticket = url.substring(id + "?ticket=".length(), url.length());

                    System.out.println("URL: " + url);
                    System.out.println("Ticket: " + ticket);

                    signin(ticket);
                    webView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void signin(final String ticket) {
        SignInCredentials signInCredentials = new SignInCredentials(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        Call<LogInCredentials> call = ServiceGenerator.createService(Client.class).signUser(ticket, signInCredentials);
        call.enqueue(new Callback<LogInCredentials>() {
            @Override
            public void onResponse(Call<LogInCredentials> call, Response<LogInCredentials> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SigninActivity.this, "Signed in !", Toast.LENGTH_LONG).show();
                    login(response.body());
                }
                else {
                    Toast.makeText(SigninActivity.this, "SigninActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogInCredentials> call, Throwable t) {
                Toast.makeText(SigninActivity.this, "SigninActivity", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void login(LogInCredentials logInCredentials) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LogInCredentials.class, new Deserializer<>("credentials"));
        gsonBuilder.registerTypeAdapter(SessionToken.class, new Deserializer<>("sessionToken"));
        gsonBuilder.registerTypeAdapter(User.class, new Deserializer<>("user"));

        Gson gson = gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Client.ROOT_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();

        Call<SessionCredentials> call = retrofit.create(Client.class).logUser(logInCredentials);
        call.enqueue(new Callback<SessionCredentials>() {
            @Override
            public void onResponse(Call<SessionCredentials> call, Response<SessionCredentials> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(SigninActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(SigninActivity.this, "SigninActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SessionCredentials> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(SigninActivity.this, "SigninActivity", Toast.LENGTH_LONG).show();
            }
        });
    }
}
