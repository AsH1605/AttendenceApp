package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.model.UserData
import com.example.notes.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var mloginemail:EditText
    private lateinit var mloginpassword:EditText
    private lateinit var mlogin:RelativeLayout
    private lateinit var mgotosignup:RelativeLayout
    private lateinit var mgotoforgotpassword:TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        mloginemail=findViewById(R.id.loginemail)
        mloginpassword=findViewById(R.id.loginpassword)
        mlogin=findViewById(R.id.login)
        mgotoforgotpassword=findViewById(R.id.gotoforgotpassword)
        mgotosignup=findViewById(R.id.gotosignup)

        firebaseAuth=FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        if(firebaseUser!=null){
            finish()
            startActivity(Intent(this,notesactivity::class.java))
        }

        mgotosignup.setOnClickListener {
            finish()
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        mgotoforgotpassword.setOnClickListener {
            finish()
            val intent = Intent(this, forgotpassword::class.java)
            startActivity(intent)
        }

        mlogin.setOnClickListener {
            val mail= mloginemail.text.toString()
            val password=mloginpassword.text.toString()
            if(mail.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()
            }
            else{
                CoroutineScope(Dispatchers.IO).launch {
                    //login the user
                    try {
                        firebaseAuth.signInWithEmailAndPassword(mail, password)
                        .addOnCompleteListener { task ->
                        checkmailverification(mail, password)
                        }
                    }catch (e:Exception){
                        Toast.makeText(
                            this@MainActivity,
                            "Login failed. Check your email and password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkmailverification(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    if (firebaseUser?.isEmailVerified == true) {
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this, notesactivity::class.java))
                    } else {
                        Toast.makeText(this, "Verify your mail first", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                    }
                } else {
                    // Handle the case where the login fails
                    Toast.makeText(this, "Login failed. Check your email and password.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}