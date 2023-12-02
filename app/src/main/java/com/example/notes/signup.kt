package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class signup : AppCompatActivity() {

    private lateinit var msignupemail:EditText
    private lateinit var msignuppassword:EditText
    private lateinit var msignup: RelativeLayout
    private lateinit var mgotologin:TextView

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()
        msignupemail=findViewById(R.id.signupemail)
        msignuppassword=findViewById(R.id.signuppassword)
        msignup=findViewById(R.id.signup)
        mgotologin=findViewById(R.id.gotologin)

        firebaseAuth=FirebaseAuth.getInstance()

        mgotologin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        msignup.setOnClickListener {
            val mail= msignupemail.text.toString()
            val password=msignuppassword.text.toString()
            if(mail.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()
            }
            else if (password.length<7){
                Toast.makeText(this,"Password too short",Toast.LENGTH_SHORT).show()
            }
            else{
                //registered the user to firebase
                firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Registration successful",Toast.LENGTH_SHORT).show()
                        sendEmailVerification()
                    }
                    else{
//                        Toast.makeText(this,"Failed to register",Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //send email verification
    private fun sendEmailVerification(){
        val firebaseUser = firebaseAuth?.currentUser
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            Toast.makeText(this,"Verification Email is sent, Verify and log in again",Toast.LENGTH_SHORT).show()
            firebaseAuth.signOut()
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}