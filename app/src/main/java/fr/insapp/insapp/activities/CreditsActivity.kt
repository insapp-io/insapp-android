package fr.insapp.insapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView

import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator

/**
 * Created by thomas on 13/12/2016.
 */

class CreditsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        val toolbar = findViewById<View>(R.id.toolbar_credits) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val webView = findViewById<View>(R.id.webview_credits) as WebView
        webView.loadUrl(ServiceGenerator.ROOT_URL + "credit")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
