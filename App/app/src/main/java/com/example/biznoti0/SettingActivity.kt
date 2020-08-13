package com.example.biznoti0

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.biznoti0.Model.ProfileUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
      private lateinit var firebaseuser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        firebaseuser = FirebaseAuth.getInstance().currentUser!!

        deletebutton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser!!

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, SignInActivity::class.java))
                        Toast.makeText(this, "This Account has been deleted.", Toast.LENGTH_LONG).show()
                    }
                }

        }

        savebutton.setOnClickListener{
            savechanges()

        }


        storeuserData()

    }

     private fun savechanges() {

         val userreference = FirebaseDatabase.getInstance().reference.child("usersID")

         val currUserHashMap = HashMap<String, Any>()


         currUserHashMap["Profession"] = EnterProfession.text.toString()
         currUserHashMap["Education"] = EnterEducation.text.toString()
         currUserHashMap["BizNotioGoals"] = EnterBizNotioGoals.text.toString()
         currUserHashMap["Interests"] = EnterInterest.text.toString()


         userreference.child(firebaseuser.uid).updateChildren(currUserHashMap)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Toast.makeText(this, "Information successfully updated.", Toast.LENGTH_LONG).show();
                     startActivity(Intent(this@SettingActivity, MainActivity::class.java))
                 } else {
                     Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show();
                 }


             }
     }

          private fun storeuserData()
        {
            val userdata = FirebaseDatabase.getInstance().getReference().child("usersID").child(firebaseuser.uid)
            userdata.addValueEventListener(object : ValueEventListener

            {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {


                    if(snapshot.exists())
                    {
                        val newuser = snapshot.getValue<ProfileUser>(ProfileUser::class.java)
                        EnterEducation?.setText(newuser!!.getEducation())
                        EnterBizNotioGoals?.setText(newuser!!.getBizNotioGoals())
                        EnterInterest?.setText(newuser!!.getInterests())
                        EnterProfession?.setText(newuser!!.getProfession())


                    }

                }

            }


            )
        }



}
