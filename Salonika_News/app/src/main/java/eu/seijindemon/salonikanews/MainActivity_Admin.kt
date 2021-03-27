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
import android.view.View
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
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.navigation_header_admin.*
import kotlinx.android.synthetic.main.navigation_header_admin.view.*
import www.sanju.motiontoast.MotionToast
import java.util.*


class MainActivity_Admin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var usersReference :  DatabaseReference? = null
    private var userReference :  DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadLocale()
        setupFirebase()
        checkUser()
        setContentView(R.layout.activity_main_admin)
        drawNavTool() // DrawLayout Menu, Navigation, Toolbar
        loadHeader()


    }

    // // DrawLayout Menu, Navigation, Toolbar
    private fun drawNavTool()
    {
        // DrawLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        findViewById<ImageView>(R.id.imageMenu).setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Nav
        val navigationView_Admin: NavigationView = findViewById(R.id.navigationView_Admin)
        navigationView_Admin.itemIconTintList = null
        val navController: NavController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupWithNavController(navigationView_Admin, navController)
        // Change Title in Toolbar
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            textTitle.text = destination.label
        }
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }
    // End DrawLayout Menu, Navigation, Toolbar

    // Firebase objects setup
    private fun setupFirebase()
    {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        usersReference = FirebaseDatabase.getInstance().getReference("profile")
        userReference = usersReference?.child(user.uid)!!
    }
    // End Firebase objects setup

    // Load Header
    @SuppressLint("SetTextI18n")
    private fun loadHeader()
    {
        val headView: View = navigationView_Admin.getHeaderView(0)
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                headView.header_first_last_name_admin.text = snapshot.child("firstname").value.toString() + " " + snapshot.child("lastname").value.toString()
                headView.header_email_admin.text = snapshot.child("email").value.toString()
                if (snapshot.hasChild("profile")) {
                    val loadImage = snapshot.child("profile").value.toString()
                    Glide.with(applicationContext).load(loadImage).into(headView.imageProfile_admin)
                } else {
                    Glide.with(applicationContext).load(R.drawable.default_profile).into(headView.imageProfile_admin)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    // End Load Header

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
        val mBuilder = AlertDialog.Builder(this@MainActivity_Admin)
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

    // Check User
    private fun checkUser()
    {
        val user = auth.currentUser
        if (user == null)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        if(auth.currentUser?.email != "georgekara2010@yahoo.gr")
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    // End Check User
}