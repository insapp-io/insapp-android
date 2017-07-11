package fr.insapp.insapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by thomas on 11/07/2017.
 */

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSharedPreferences("Credentials", MODE_PRIVATE).contains("login")) {
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        }
        else {
            startActivity(new Intent(LauncherActivity.this, IntroActivity.class));
        }

        finish();
    }
}
