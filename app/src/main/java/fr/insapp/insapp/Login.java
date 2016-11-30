package fr.insapp.insapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.utility.File;
import fr.insapp.insapp.utility.Utils;


public class Login extends Activity {

    private static int nb_try = 0;

    public void onResume() {
        super.onResume();  // Always call the superclass method first

        login();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //login();
    }

    public void login(){
        HttpGet.info_user = File.readSettings(this);

        if(!HttpGet.info_user.equals("")) {

            String[] params = HttpGet.info_user.split(" ");

            JSONObject json = new JSONObject();
            try {
                json.put("Username", params[0]);
                json.put("AuthToken", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final HttpPost login = new HttpPost(new AsyncResponse() {

                public void processFinish(String output) {
                    JSONObject json = null;
                    if (output != null) {
                        try {
                            json = new JSONObject(output);
                            if (!json.has("error")) {
                                HttpGet.credentials = new Credentials(json);
                                nb_try = 0; // we can login

                                SharedPreferences prefs = getSharedPreferences(
                                        Signin.class.getSimpleName(), getApplicationContext().MODE_PRIVATE);

                                String token = prefs.getString(Signin.REG_ID, "");

                                JSONObject notuser = new JSONObject();
                                notuser.put("userid", HttpGet.credentials.getUserID());
                                notuser.put("token", token);
                                notuser.put("os", "android");

                                if(!token.isEmpty()) {
                                    HttpPost post = new HttpPost(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            if (isTaskRoot() | getIntent().getBooleanExtra("signin", false)) {
                                                Intent i = new Intent(Login.this, MainActivity.class);
                                                startActivity(i);
                                                //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                                finish();
                                            } else {
                                                finish(); // back to last activity
                                                //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                            }
                                        }
                                    });
                                    post.execute(HttpGet.ROOTNOTIFICATION + "?token=" + HttpGet.credentials.getSessionToken(), notuser.toString());
                                }
                                else{
                                    if (isTaskRoot() | getIntent().getBooleanExtra("signin", false)) {
                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                        finish();
                                    } else {
                                        finish(); // back to last activity
                                        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            cantLogin();
                            e.printStackTrace();
                        }
                    }
                    else
                        cantLogin();
                }
            });
            login.execute(HttpGet.ROOTLOGIN, json.toString());

        }
        else{
            cantLogin();
        }
    }

    public void cantLogin(){

        if(Utils.isNetworkAvailable(Login.this)) {
            //if(HttpPost.responseCode != 0) {
            nb_try++;
            if (nb_try <= 5) {
                Intent i = new Intent(Login.this, TutoActivity.class);
                startActivity(i);
                finish();
            } else
                Toast.makeText(this, "Problème avec le serveur...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Internet indisponible", Toast.LENGTH_LONG).show();
            //Thread.sleep(1000);
            //Intent i = new Intent(Login.this, TutoActivity.class);
            //startActivity(i);
            //finish();
        }
        //}
        //else
        //    Toast.makeText(this, "Problème d'accès à Internet...", Toast.LENGTH_LONG).show();
    }
}
