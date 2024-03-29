package eu.seijindemon.salonikanews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import www.sanju.motiontoast.MotionToast


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var userReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupFirebase()

        register()

    }

    // Firebase objects setup
    private fun setupFirebase()
    {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database?.reference!!.child("profile")
    }
    // End Firebase objects setup

    private fun register()
    {
        registerButton.setOnClickListener{
            if(emailFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createColorToast(
                    this,
                    "Warning",
                    "Input Email",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
            else if(passwordFormRegister.text.toString().trim().isEmpty() || password2FormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createColorToast(
                    this,
                    "Warning",
                    "Input Password or Confirm Password",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
            else if (passwordFormRegister.text.toString().trim() != password2FormRegister.text.toString().trim())
            {
                MotionToast.Companion.createColorToast(
                    this,
                    "Warning",
                    "Passwords are different",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
            else if(firstNameFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createColorToast(
                    this,
                    "Warning",
                    "Input FirstName",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
            else if(lastNameFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createColorToast(
                    this,
                    "Warning",
                    "Input LastName",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
            else
            {
                createUser(
                    emailFormRegister.text.toString().trim(),
                    passwordFormRegister.text.toString().trim(),
                    firstNameFormRegister.text.toString().trim(),
                    lastNameFormRegister.text.toString().trim()
                )
            }
        }
    }

    private fun createUser(email: String, password: String, firstName: String, lastName: String)
    {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful)
                {
                    val currentUser = FirebaseAuth.getInstance().currentUser!!

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName").build()

                    currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.e("TAG", "Updated Profile")
                            }
                        }

                    val currentUSerDb = userReference?.child((currentUser.uid))
                    currentUSerDb?.child("firstname")?.setValue(firstName)
                    currentUSerDb?.child("lastname")?.setValue(lastName)
                    currentUSerDb?.child("email")?.setValue(email)

                    currentUser.sendEmailVerification().addOnCompleteListener{
                        MotionToast.Companion.createColorToast(
                            this,
                            "Successful",
                            "Verification Email has been sent.",
                            MotionToast.Companion.TOAST_SUCCESS,
                            MotionToast.Companion.GRAVITY_BOTTOM,
                            MotionToast.Companion.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                    }
                    MotionToast.Companion.createColorToast(
                        this,
                        "Successful",
                        "Registration successful",
                        MotionToast.Companion.TOAST_SUCCESS,
                        MotionToast.Companion.GRAVITY_BOTTOM,
                        MotionToast.Companion.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else
                {
                    MotionToast.Companion.createColorToast(
                        this,
                        "Failed",
                        "Registration failed, please try again!",
                        MotionToast.Companion.TOAST_ERROR,
                        MotionToast.Companion.GRAVITY_BOTTOM,
                        MotionToast.Companion.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                }
            }
    }
}