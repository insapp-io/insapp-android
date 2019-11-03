package fr.insapp.insapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setSupportActionBar(toolbar_sign_in)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //refresh_webpage.setOnRefreshListener(this)

        val cookieManager = CookieManager.getInstance()
        cookieManager.removeSessionCookies { res ->
            Log.d("CAS", "Cookie removed: $res")
        }

        webview_conditions.loadUrl(CAS_URL)
        webview_conditions.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                progress_bar.visibility = View.VISIBLE

                val id = url.lastIndexOf("?ticket=")
                if (url.contains("?ticket=")) {
                    val ticket = url.substring(id + "?ticket=".length, url.length)

                    Log.d("CAS", "URL: $url")
                    Log.d("CAS", "Ticket: $ticket")

                    logIn(ticket)
                    webview_conditions.visibility = View.INVISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar.visibility = View.GONE
            }
        }
    }

    fun logIn(ticket: String) {
        val call = ServiceGenerator.client.logUser(ticket)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                if (response.isSuccessful && user != null) {
                    val userPreferences = this@SignInActivity.getSharedPreferences("User", Context.MODE_PRIVATE)
                    userPreferences.edit().putString("user", Gson().toJson(user)).apply()

                    val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@SignInActivity)
                    val editor = defaultSharedPreferences.edit()
                    editor.putString("name", user.name)
                    editor.putString("description", user.description)
                    editor.putString("email", user.email)
                    editor.putString("class", user.promotion)
                    editor.putString("gender", user.gender)
                    editor.apply()

                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignInActivity, TAG, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignInActivity, TAG, Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {
        private const val CAS_URL = "https://cas.insa-rennes.fr/cas/login?service=https://insapp.fr/"

        const val TAG = "SignInActivity"
    }
}
