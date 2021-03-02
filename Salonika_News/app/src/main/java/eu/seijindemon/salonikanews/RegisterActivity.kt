package eu.seijindemon.salonikanews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

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
                Toast.makeText(this, "Input Email", Toast.LENGTH_LONG).show()
            }
            else if(passwordFormRegister.text.toString().trim().isEmpty() || password2FormRegister.text.toString().trim().isEmpty())
            {
                Toast.makeText(this, "Input Password or Comfirm Password", Toast.LENGTH_LONG).show()
            }
            else if (passwordFormRegister.text.toString().trim() != password2FormRegister.text.toString().trim())
            {
                Toast.makeText(this, "Passwords are different", Toast.LENGTH_LONG).show()
            }
            else if(firstNameFormRegister.text.toString().trim().isEmpty())
            {
                Toast.makeText(this, "Input FirstName", Toast.LENGTH_LONG).show()
            }
            else if(lastNameFormRegister.text.toString().trim().isEmpty())
            {
                Toast.makeText(this, "Input LastName", Toast.LENGTH_LONG).show()
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

                    Toast.makeText(this, "Registration Success. ", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Registration failed, please try again! ", Toast.LENGTH_LONG).show()
                }
            }
    }
}