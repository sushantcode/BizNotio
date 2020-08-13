package com.example.biznoti0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class passwordResetActivity : AppCompatActivity() {
    lateinit var resetEmail: EditText
    lateinit var resetBut : Button
    lateinit var  mFireAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        resetEmail = findViewById(R.id.passRestEmail)
        resetBut = findViewById(R.id.passResetButton)
        mFireAuth = FirebaseAuth.getInstance()

        resetBut.setOnClickListener {
            val getEmail = resetEmail.text.toString()
            if (getEmail.isEmpty()) {
                Toast.makeText(this, "Email is must", Toast.LENGTH_LONG).show()
            }
            else {
                sendResetLink(getEmail)
            }
        }
    }

    private fun sendResetLink(email: String) {
        mFireAuth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->
            if (task.isComplete) {
                val signInintent = Intent(this@passwordResetActivity, SignInActivity::class.java)
                startActivity(signInintent)

                Toast.makeText(this, "Reset link has been sent to your email.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Please enter valid email.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
