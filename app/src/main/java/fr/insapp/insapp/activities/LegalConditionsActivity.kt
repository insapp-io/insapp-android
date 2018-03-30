package fr.insapp.insapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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

        webview_conditions.loadUrl(ServiceGenerator.ROOT_URL + "legal")
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
