package eu.seijindemon.salonikanews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.navigation_header.*
import www.sanju.motiontoast.MotionToast
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var databaseReference :  DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()

        // Setup Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")
        // End Setup Firebase
        checkUser()
        loadHeader()

        setContentView(R.layout.activity_main)

        // DrawLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        findViewById<ImageView>(R.id.imageMenu).setOnClickListener{
                drawerLayout.openDrawer(GravityCompat.START)
        }
        // Nav
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.itemIconTintList = null
        val navController: NavController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupWithNavController(navigationView, navController)
        // Change Title in Toolbar
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            textTitle.text = destination.label
        }
        // End Nav

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        // End Toolbar



    }

    @SuppressLint("SetTextI18n")
    private fun loadHeader()
    {
        val user = auth.currentUser!!
        val userRef = databaseReference?.child(user.uid)!!

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                header_first_last_name.text = user.displayName
                header_email.text = user.email
                if (snapshot.hasChild("profile")) {
                    Picasso.get().load(snapshot.child("profile").value.toString()).into(imageProfile)
                } else {
                    Picasso.get().load(R.drawable.default_profile).into(imageProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


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
                    MotionToast.Companion.createColorToast(
                            this,
                            "Warning",
                            "Privacy Policy Not Found!",
                            MotionToast.Companion.TOAST_WARNING,
                            MotionToast.Companion.GRAVITY_BOTTOM,
                            MotionToast.Companion.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular))
                }
                true
            }
            else -> false
        }
    }

    // Popup Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.popup_menu, menu)
        return true
    }
    // End Popup Menu

    // Show Icons with correct color
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            if (menu is MenuBuilder) {
                try {
                    val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
                    field.isAccessible = true
                    field.setBoolean(menu, true)
                } catch (ignored: Exception) {
                    // ignored exception
                    //logger.debug("ignored exception: ${ignored.javaClass.simpleName}")
                }
            }
            for (item in 0 until menu.size()) {
                val menuItem = menu.getItem(item)
                menuItem.icon.setIconColor(
                        if (menuItem.getShowAsAction() == 0) Color.BLACK
                        else Color.WHITE
                )
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    fun MenuItem.getShowAsAction(): Int {
        val f = this.javaClass.getDeclaredField("mShowAsAction")
        f.isAccessible = true
        return f.getInt(this)
    }

    fun Drawable.setIconColor(color: Int) {
        mutate()
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
    // End Show Icons with correct color

    // Change Language
    fun showChangeLanguageDialog() {
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

    // Back Pressed
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    // End Back Pressed

    // Check User
    private fun checkUser()
    {
        val user = auth.currentUser
        if (user == null)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    // End Check User
}