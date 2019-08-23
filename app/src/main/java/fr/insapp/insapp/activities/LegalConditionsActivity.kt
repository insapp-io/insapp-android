package fr.insapp.insapp.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import kotlinx.android.synthetic.main.activity_legal_conditions.*

/**
 * Created by thomas on 13/12/2016.
 */

class LegalConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_conditions)

        setSupportActionBar(toolbar_conditions)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        container?.visibility = View.VISIBLE
        progress_bar?.visibility = View.VISIBLE
        no_network?.visibility = View.GONE
        webview_conditions?.visibility = View.GONE

        webview_conditions.loadUrl(ServiceGenerator.ROOT_URL + "legal")
        webview_conditions.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                container?.visibility = View.VISIBLE
                progress_bar?.visibility = View.VISIBLE
                no_network?.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url == "about:blank") {
                    container?.visibility = View.VISIBLE
                    progress_bar?.visibility = View.GONE
                    no_network?.visibility = View.VISIBLE
                    webview_conditions?.visibility = View.GONE
                } else {
                    container?.visibility = View.GONE
                    progress_bar?.visibility = View.GONE
                    no_network?.visibility = View.GONE
                    webview_conditions?.visibility = View.VISIBLE
                    Answers.getInstance().logCustom(CustomEvent("Read Legal Conditions"))
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                webview_conditions.loadUrl("about:blank")
                super.onReceivedError(view, request, error)
            }
        }
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
