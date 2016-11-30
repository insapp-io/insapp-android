package fr.insapp.insapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.utility.File;

public class Signin extends Activity {

    public static final int REQUEST_READ_PHONE_STATE = 10;
    final String url_site = "https://cas.insa-rennes.fr/cas/login?service=https://insapp.fr/";

    //GoogleCloudMessaging gcm;
    Context context;
    String regId;
    public static final String REG_ID = "regId";
    public static final String SENDER_ID = "451191722739";

    public static boolean refreshing;
    public static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        refreshing = false;

        SharedPreferences.Editor prefs = getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
        prefs.putBoolean("notifications", true);
        prefs.commit();

        /*ImageView credits = (ImageView) findViewById(R.id.credits);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Signin.this, Credits.class);
                startActivity(activity);
            }
        });
*/
        ImageView cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signin.this, TutoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        TextView title = (TextView) findViewById(R.id.heading_text);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Signin.this, Signin.class);
                startActivity(activity);
            }
        });

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        final WebView webView = (WebView) findViewById(R.id.webview);

        webView.loadUrl(url_site);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementById('username').value);");
                int id = url.lastIndexOf("?ticket=");
                if (url.contains("?ticket=")) {
                    String ticket = url.substring(id + "?ticket=".length(), url.length());
                    System.out.println(url + " et ticket=" + ticket);
                    signin(ticket);
                    webView.setVisibility(View.INVISIBLE);
                }
            }
        });

        context = getApplicationContext();
/*
        if (TextUtils.isEmpty(regId)) {
            // Récupération du registerId du terminal ou enregistrement de ce dernier
            regId = registerGCM();
        }*/
    }

    public void signin(final String ticket){

        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signin.this);

        // set title
        alertDialogBuilder.setTitle("Connexion");

        // set dialog message
        alertDialogBuilder
                .setMessage("Si ton compte existe déjà sur un autre téléphone, tu seras deconnecté(e) de ce dernier. Tu ne perdras aucune donnée. Souhaites-tu continuer ?")
                .setCancelable(false)
                .setPositiveButton("OUI",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert,int id) {

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

                                                File.writeSettings(Signin.this, text);

                                                Intent i = new Intent(Signin.this, Login.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                i.putExtra("signin", true);
                                                startActivity(i);
                                                finish();

                                            } else
                                                Toast.makeText(Signin.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(HttpPost.responseCode >= 400 && HttpPost.responseCode < 500){
                                            //if(!output.isEmpty())
                                            Toast.makeText(Signin.this, "Erreur : " + output, Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e){
                                        System.out.println(e.getMessage());
                                        Toast.makeText(Signin.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                                    }// catch (Throwable throwable) {
                                     //   throwable.printStackTrace();
                                    //}
                                } else
                                    Toast.makeText(Signin.this, "Problème de connexion", Toast.LENGTH_SHORT).show();

                            }
                        });
                        signin.execute(HttpGet.ROOTSIGNIN + "/" + ticket, json.toString());

                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert, int id) {
                        // On annule donc on retourne dans les settings
                        dialogAlert.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /************** * Cette méthode récupère le registerId dans les SharedPreferences via
     * la méthode getRegistrationId(context).
     * S'il n'existe pas alors on enregistre le terminal via
     * la méthode registerInBackground()
     **/
    /*public String registerGCM() {
        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {
            registerInBackground();
            //Log.d("registerGCM - enregistrement auprès du GCM server OK - regId: " + regId);
        } else {
            System.out.println(regId);
            //Toast.makeText(getApplicationContext(), "RegId existe déjà. RegId: " + regId, Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            //Log.i(TAG, "registrationId non trouvé.");
            return "";
        }
        // On peut aussi ajouter un contrôle sur la version de l'application.
        // Lors d'un changement de version d'application le register Id du terminal ne sera plus valide.
        // Ainsi, s'il existe un registerId dans les SharedPreferences, mais que la version
        // de l'application a évolué alors on retourne un registrationId="" forçant ainsi
        // l'application à enregistrer de nouveau le terminal.

        return registrationId;
    }
*/
    /** * Cette méthode permet l'enregistrement du terminal */
    /*private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    System.out.println(regId);
                    msg = "Terminal enregistré, register ID=" + regId;
                    // On enregistre le registerId dans les SharedPreferences
                    SharedPreferences.Editor prefs = getSharedPreferences(
                            Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                    prefs.putString(REG_ID, regId);
                    prefs.commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    //Log.d("Error: " + msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }*/
}
