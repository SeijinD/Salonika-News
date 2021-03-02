package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

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
                Toast.makeText(this, "Input Email", Toast.LENGTH_LONG).show()
            }
            else if(passwordFormLogin.text.toString().trim().isEmpty())
            {
                Toast.makeText(this, "Input Password", Toast.LENGTH_LONG).show()
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
                    Log.e("Task Message", "Successful...")

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Login failed, please try again! ", Toast.LENGTH_LONG).show()
                }
            }
    }

}