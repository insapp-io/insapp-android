package fr.insapp.insapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class LegalConditionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_condition);


        ImageView cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LegalConditionActivity.this.finish();
            }
        });

        Button accepter = (Button) findViewById(R.id.accepter);
        accepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LegalConditionActivity.this, Signin.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });


        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("https://insapp.fr/api/v1/legal");
    }
}
