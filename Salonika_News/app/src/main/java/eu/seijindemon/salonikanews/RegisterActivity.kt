package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener{
            if(emailFormRegister.text.toString().trim().isNotEmpty() || passwordFormRegister.text.toString().trim().isNotEmpty())
            {
                createUser(emailFormRegister.text.toString().trim(), passwordFormRegister.text.toString().trim())
            }
            else
            {
                Toast.makeText(this, "Input Required", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun createUser(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful)
                {
                    Log.e("Task Message", "Successful...")

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Log.e("Task Message", "Failed..." +task.exception)
                }
            }
    }

//    override fun onStart()
//    {
//        super.onStart()
//        val user = auth.currentUser
//
//        if(user != null)
//        {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//    }

}