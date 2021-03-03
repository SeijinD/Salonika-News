package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import www.sanju.motiontoast.MotionToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        register()
    }

    private fun register()
    {
        registerButton.setOnClickListener{
            if(emailFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Input Email",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else if(passwordFormRegister.text.toString().trim().isEmpty() || password2FormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Input Password or Confirm Password",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else if (passwordFormRegister.text.toString().trim() != password2FormRegister.text.toString().trim())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Passwords are different",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else if(firstNameFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Input FirstName",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else if(lastNameFormRegister.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Input LastName",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else
            {
                createUser(emailFormRegister.text.toString().trim(),
                    passwordFormRegister.text.toString().trim(),
                    firstNameFormRegister.text.toString().trim(),
                    lastNameFormRegister.text.toString().trim())
            }
        }
    }

    private fun createUser(email: String, password: String, firstName: String, lastName: String)
    {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful)
                {
                    val currentUser = auth.currentUser
                    val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
                    currentUSerDb?.child("firstname")?.setValue(firstName)
                    currentUSerDb?.child("lastname")?.setValue(lastName)
                    currentUSerDb?.child("email")?.setValue(email)

                    MotionToast.Companion.createToast(
                        this,
                        "Successful",
                        "Registration successful",
                        MotionToast.Companion.TOAST_SUCCESS,
                        MotionToast.Companion.GRAVITY_BOTTOM,
                        MotionToast.Companion.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular))

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    MotionToast.Companion.createToast(
                        this,
                        "Failed",
                        "Registration failed, please try again!",
                        MotionToast.Companion.TOAST_ERROR,
                        MotionToast.Companion.GRAVITY_BOTTOM,
                        MotionToast.Companion.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular))
                }
            }
    }
}