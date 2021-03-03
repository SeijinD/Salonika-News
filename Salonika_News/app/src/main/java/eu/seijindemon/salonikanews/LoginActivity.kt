package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val currentuser = auth.currentUser
        if(currentuser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        goRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        login()
    }

    private fun login()
    {
        loginButton.setOnClickListener{
            if(emailFormLogin.text.toString().trim().isEmpty())
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
            else if(passwordFormLogin.text.toString().trim().isEmpty())
            {
                MotionToast.Companion.createToast(
                    this,
                    "Warning",
                    "Input Password",
                    MotionToast.Companion.TOAST_WARNING,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular))
            }
            else
            {
                loginUser(emailFormLogin.text.toString().trim(), passwordFormLogin.text.toString().trim())
            }
        }

        guestButton.setOnClickListener{
            val intent = Intent(this, MainActivity_Guest::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String)
    {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful)
                {
                    MotionToast.Companion.createToast(
                        this,
                        "Successful",
                        "Login...",
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
                        "Try Again...",
                        MotionToast.Companion.TOAST_ERROR,
                        MotionToast.Companion.GRAVITY_BOTTOM,
                        MotionToast.Companion.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular))
                }
            }
    }

}