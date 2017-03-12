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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.utility.File;

public class SigninActivity extends AppCompatActivity {

    public static final int REQUEST_READ_PHONE_STATE = 10;
    final String url_site = "https://cas.insa-rennes.fr/cas/login?service=https://insapp.fr/";

    GoogleCloudMessaging gcm;
    Context context;
    String regId;
    public static final String REG_ID = "regId";
    public static final String SENDER_ID = "451191722739";

    public static boolean refreshing;
    public static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        refreshing = false;

        /*
            ImageView credits = (ImageView) findViewById(R.id.credits);
            credits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activity = new Intent(SigninActivity.this, Credits.class);
                    startActivity(activity);
                }
            });
        */

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        final WebView webView = (WebView) findViewById(R.id.webview_conditions);

        webView.loadUrl(url_site);
        webView.getSettings().setJavaScriptEnabled(true);

        //webView.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");

        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementById('username').value);");
                int id = url.lastIndexOf("?ticket=");
                if (url.contains("?ticket=")) {
                    String ticket = url.substring(id + "?ticket=".length(), url.length());

                    System.out.println("URL: " + url);
                    System.out.println("Ticket: " + ticket);

                    signin(ticket);
                    webView.setVisibility(View.INVISIBLE);
                }
            }
        });

        context = getApplicationContext();
    }

    public void signin(final String ticket){

        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SigninActivity.this);

        // set title
        alertDialogBuilder.setTitle("Connexion");

        // set dialog message
        alertDialogBuilder
                .setMessage("Si ton compte existe déjà sur un autre téléphone, tu seras déconnecté(e) de ce dernier. Tu ne perdras aucune donnée. Souhaites-tu continuer ?")
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert, int id) {

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
                                        if(HttpPost.responseCode == 200) {
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
                                        else if(HttpPost.responseCode >= 400 && HttpPost.responseCode < 500){
                                            if (MainActivity.dev)
                                                Toast.makeText(SigninActivity.this, "Erreur " + HttpPost.responseCode + " : " + output, Toast.LENGTH_LONG).show();
                                            else
                                                Toast.makeText(SigninActivity.this, "Erreur : " + output, Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e){
                                        System.out.println(e.getMessage());
                                        Toast.makeText(SigninActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    Toast.makeText(SigninActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                            }
                        });
                        signin.execute(HttpGet.ROOTSIGNIN + "/" + ticket, json.toString());

                    }
                })
                .setNegativeButton(getResources().getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert, int id) {
                        // On annule donc on retourne dans les settings
                        dialogAlert.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
