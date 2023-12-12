package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class forgotpassword : AppCompatActivity() {

    private lateinit var mforgotpassword: EditText
    private lateinit var mpasswordrecoverbutton: Button
    private lateinit var mgobacktologin: TextView

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        supportActionBar?.hide()
        mforgotpassword=findViewById(R.id.forgotpassword)
        mpasswordrecoverbutton=findViewById(R.id.passwordrecoverbutton)
        mgobacktologin=findViewById(R.id.gobacktologin)
        firebaseAuth=FirebaseAuth.getInstance()

        mgobacktologin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        mpasswordrecoverbutton.setOnClickListener {
            val mail= mforgotpassword.text.toString()
            if(mail.isEmpty()){
                Toast.makeText(this,"Enter your email first",Toast.LENGTH_LONG).show()
            }
            else{
                //send password recover email
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {task->
                    Toast.makeText(this, "You can recover your password using email if registered.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}