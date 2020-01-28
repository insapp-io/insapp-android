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
import com.google.firebase.analytics.FirebaseAnalytics
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import kotlinx.android.synthetic.main.activity_credits.*

/**
 * Created by thomas on 09/09/2018.
 */

class HowToPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        setSupportActionBar(toolbar_credits)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        container?.visibility = View.VISIBLE
        progress_bar?.visibility = View.VISIBLE
        no_network?.visibility = View.GONE
        webview_credits?.visibility = View.GONE

        webview_credits.loadUrl(ServiceGenerator.ROOT_URL + "how-to-post")
        webview_credits.webViewClient = object : WebViewClient() {
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
                    webview_credits?.visibility = View.GONE
                } else {
                    container?.visibility = View.GONE
                    progress_bar?.visibility = View.GONE
                    no_network?.visibility = View.GONE
                    webview_credits?.visibility = View.VISIBLE

                    FirebaseAnalytics.getInstance(this@HowToPostActivity).logEvent("read_how_to_post", Bundle())
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                webview_credits.loadUrl("about:blank")
                super.onReceivedError(view, request, error)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
