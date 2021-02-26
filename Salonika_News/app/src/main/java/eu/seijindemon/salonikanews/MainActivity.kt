package eu.seijindemon.salonikanews

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import java.util.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // End Toolbar


    }

    // Change Language
    private fun showChangeLanguageDialog() {
        val listItems = arrayOf("English", "Greek")
        val mBuilder = AlertDialog.Builder(this@MainActivity)
        mBuilder.setTitle(R.string.change_language)
        mBuilder.setSingleChoiceItems(listItems, -1)
        { dialog, i ->
            if (i == 0) {
                setLocale("en")
                recreate()
            } else if (i == 1) {
                setLocale("el")
                recreate()
            }
            dialog.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun setLocale(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocale(language)
        }
    }
    // End Change Language

    // Popup Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.popup_menu, menu)
        return true
    }
    // End Popup Menu

    // Menu Item Click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language -> {
                // Open Change Language
                showChangeLanguageDialog()
                true
            }
            R.id.share -> {
                // Open Share It
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Salonika News")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Download this Application now: http://play.google.com/store/apps/details?id=$packageName"
                )
                startActivity(Intent.createChooser(intent, "ShareVia"))
                true
            }
            R.id.rate -> {
                // Open Rate URL
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
                true
            }
            R.id.privacy_policy -> {
                // Open Privacy Police URL
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://my-informations.flycricket.io/privacy.html")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Privacy Policy Not Found!", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> false
        }
    }
    // End Menu Item Click

    // Back Pressed
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    // End Back Pressed
}