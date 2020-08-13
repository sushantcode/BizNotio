package com.example.biznoti0

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUp : AppCompatActivity() {
    private lateinit var mFireAuth: FirebaseAuth
    private lateinit var userreference: DatabaseReference
    lateinit var acType: String
    lateinit var radioGroup: RadioGroup
    lateinit var rb_investee: RadioButton
    lateinit var rb_investor: RadioButton
    lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        radioGroup = findViewById(R.id.Radiogroup) as RadioGroup
        rb_investee = findViewById(R.id.AT_investee) as RadioButton
        rb_investor = findViewById(R.id.AT_investor) as RadioButton
        registerButton = findViewById(R.id.register) as Button

        signUpIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        mFireAuth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener(View.OnClickListener {
            if (radioGroup.checkedRadioButtonId != -1){
                if (rb_investee.isChecked)
                    acType = "Investee"
                if (rb_investor.isChecked)
                    acType = "Investor"
                Registration()
            } else {
                Toast.makeText(this, "Account Type selection is required", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun Registration() {
        val Fnames = SignUpFName.text.toString()
        val Lnames = SignUpLName.text.toString()
        val Mnames = SignUpMName.text.toString()
        val emails = SignUpEmail.text.toString()
        val passwords = SignUpPassword.text.toString()

        if (Fnames.isEmpty()) {
            Toast.makeText(this, "First Name is required", Toast.LENGTH_LONG).show()
        } else if (Lnames.isEmpty()) {
            Toast.makeText(this, "Last Name is required", Toast.LENGTH_LONG).show()
        } else if (emails.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
        } else if (passwords.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show()
        } else {
            val progressDialog = ProgressDialog(this@SignUp)
            progressDialog.setTitle("SignUp")
            progressDialog.setMessage("Sign up Process in Progress.....")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            mFireAuth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = mFireAuth.currentUser
                        currentUser!!.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    store(Fnames, Mnames, Lnames, emails, progressDialog)
                                }
                            }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss()
                    }
                }
        }


    }

    private fun store(Fnames: String, Mnames: String, Lnames: String, emails: String, progressDialog: ProgressDialog) {

        var curruserId = mFireAuth.currentUser!!.uid
        userreference = FirebaseDatabase.getInstance().reference.child("usersID").child(curruserId)

        val currUserHashMap = HashMap<String, Any>()

        currUserHashMap["usersID"] = curruserId
        currUserHashMap["ACType"] = acType.toLowerCase()
        currUserHashMap["FName"] = Fnames.toLowerCase()
        currUserHashMap["MName"] = Mnames.toLowerCase()
        currUserHashMap["LName"] = Lnames.toLowerCase()
        currUserHashMap["Email"] = emails
        currUserHashMap["Profession"] = "Full-time BizNotio User"
        currUserHashMap["Education"] = "World University"
        currUserHashMap["BizNotioGoals"] = "Serve the world"
        currUserHashMap["Interests"] = "Learning"


        currUserHashMap["Image"] = "gs://bitnoti0.appspot.com/user Info/profile.png"
        userreference.updateChildren(currUserHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign up Success. Check your email and verify it.", Toast.LENGTH_LONG).show();
                    startActivity(Intent(this@SignUp, SignInActivity::class.java))
                    finish()
                }
                else
                {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss()
                }
            }

    }


}


