package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        goRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{
            if(emailFormLogin.text.toString().trim().isNotEmpty() || passwordFormLogin.text.toString().trim().isNotEmpty())
            {
                loginUser(emailFormLogin.text.toString().trim(), passwordFormLogin.text.toString().trim())
            }
            else
            {
                Toast.makeText(this, "Input Required", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun loginUser(email: String, password: String)
    {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful)
                {
                    Log.e("Task Message", "Successful...")

                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Log.e("Task Message", "Failed..." +task.exception)
                }
            }
    }

}