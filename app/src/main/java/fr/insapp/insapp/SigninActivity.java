package fr.insapp.insapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.Client;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.File;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        JSONObject json = new JSONObject();
        try {
            json.put("Device", android_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final HttpPost signin = new HttpPost(new AsyncResponse() {

            public void processFinish(String output) {
                JSONObject json = null;
                System.out.println(output);
                if (output != null) {
                    try {
                        if (HttpPost.responseCode == 200) {
                            json = new JSONObject(output);
                            if (!json.has("error")) {
                                String text = json.getString("username") + " " + json.getString("authtoken");

                                System.out.println("Text: " + text);

                                File.writeSettings(SigninActivity.this, text);

                                Intent i = new Intent(SigninActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("signin", true);
                                startActivity(i);
                                finish();

                            } else
                                Toast.makeText(SigninActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                        }
                        else if (HttpPost.responseCode >= 400 && HttpPost.responseCode < 500) {
                            if (MainActivity.dev)
                                Toast.makeText(SigninActivity.this, "Erreur " + HttpPost.responseCode + " : " + output, Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SigninActivity.this, "Erreur : " + output, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(SigninActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(SigninActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
            }
        });
        signin.execute(HttpGet.ROOTSIGNIN + "/" + ticket, json.toString());


        /*
        Call<Post> call = ServiceGenerator.createService(Client.class).signUser(ticket);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    post = response.body();

                    generateActivity();
                }
                else {
                    Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
            }
        });
        */
    }
}
