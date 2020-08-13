package com.example.biznoti0

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))

        }

        login.setOnClickListener {
            SignIn();
        }

        resetBut.setOnClickListener {
            startActivity(Intent(this, passwordResetActivity::class.java))
        }

    }
    private var progressDialog: ProgressDialog? = null

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }
    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isEmailVerified)
        {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun SignIn() {
        val mfirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val emails = email.text.toString()
        val passwords = Password.text.toString()

        if (emails.isEmpty()) {
            Toast.makeText(this, "Email is must", Toast.LENGTH_LONG).show()
        } else if (passwords.isEmpty()) {
            Toast.makeText(this, "Password is must", Toast.LENGTH_LONG).show()
        } else {

            progressDialog = ProgressDialog(this@SignInActivity)
            progressDialog!!.setTitle("Logging In")
            progressDialog!!.setMessage("Logging-in In progress")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()

            mfirebaseAuth.signInWithEmailAndPassword(emails, passwords)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show();
                        progressDialog!!.dismiss()
                    } else {
                        if (mfirebaseAuth.currentUser!!.isEmailVerified) {
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                            progressDialog!!.dismiss()
                        }
                    }
                }
        }
    }

}
