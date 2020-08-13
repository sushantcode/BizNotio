package Fragments


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biznoti0.Model.ProfileUser
import com.example.biznoti0.R
import com.example.biznoti0.SettingActivity
import com.example.biznoti0.SignInActivity
import com.example.biznoti0.ViewModels.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class ProfileFragment : Fragment() {
    private lateinit var IDforprofile:String
    private lateinit var firebaseuser:FirebaseUser

    private val model: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.currentlyLoggedInUser.observe(viewLifecycleOwner, Observer<ProfileUser> { item ->
            Log.d("ProfileFragment", "currentlyLoggedInUser: ${item.toString()}")
            val currentlyLoggedInUser = item
            model.selectedUser.observe(viewLifecycleOwner, Observer<ProfileUser> { item ->
                Log.d("ProfileFragment", "selectedUser: ${item.toString()}")
                if (currentlyLoggedInUser.getusersID() != item.getusersID()) {
                    val ref = FirebaseDatabase.getInstance().getReference("/Connect/${currentlyLoggedInUser.getusersID()}")
                    ref.child("Connected").child(item.getusersID()).addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue<Boolean?>()
                            if (edit_button != null) {
                                if (value == null) {
                                    edit_button.text = "Connect"
                                }
                                else if (value == true) {
                                    edit_button.text = "Connected"
                                }

                                else if (value == false) {
                                    edit_button.text = "Connect"
                                }
                            }


                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


                    edit_button.text = "Connect"
                }
            })

        })
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private var progressDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Profile button extracted from the layout file
        val profileButton = view.findViewById<ImageView>(R.id.imageView)



        profileButton?.setOnClickListener{
            val intent = Intent (Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)


        }


        Rating.setOnClickListener {
            findNavController().navigate(R.id.Rating, null)


        }


        // Settings Button
        val button = view.findViewById<Button>(R.id.edit_button)

        model.currentlyLoggedInUser.observe(viewLifecycleOwner, Observer<ProfileUser> { item ->
            Log.d("ProfileFragment", "currentlyLoggedInUser: ${item.toString()}")
            val currentlyLoggedInUser = item
            model.selectedUser.observe(viewLifecycleOwner, Observer<ProfileUser> { item ->
                button?.setOnClickListener {
                    val buttoninfo = view.edit_button.text.toString()

                    if(buttoninfo=="Edit Profile") {
                        val intent = Intent (this@ProfileFragment.context, SettingActivity::class.java)
                        startActivity(intent)


                    }

                    else if(buttoninfo=="Connect")
                    {
                        Log.d("ProfileFragment", "Pressed Connect button")
                        val ref = FirebaseDatabase.getInstance().getReference("/Connect/${currentlyLoggedInUser.getusersID()}")
                        ref.child("Connected").child(item.getusersID()).setValue(true)
                        button.text = "Connected"
                    }

                    else if(buttoninfo == "Connected")
                    {
                        Log.d("ProfileFragment", "Pressed Connect when already connect button")
                        val ref = FirebaseDatabase.getInstance().getReference("/Connect/${currentlyLoggedInUser.getusersID()}")
                        ref.child("Connected").child(item.getusersID()).removeValue()
                        button.text = "Connect"
                    }
                }
            })

        })


        // Logout Button extracted from the layout file
        imagelogoutbutton?.setOnClickListener {

            progressDialog = ProgressDialog(this@ProfileFragment.context)
            progressDialog!!.setMessage("Logging out")
            progressDialog!!.show()

            FirebaseAuth.getInstance().signOut()

            val intent = Intent (this@ProfileFragment.context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
        val preference = context?.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
        if(preference!=null) {
            this.IDforprofile = preference.getString("IDforprofile", "none")!!

        }

        //Comparing online user and firebase current user if they are the same;ie own profile page.
        //if not same user, means re-directing from search to user's profile page

        firebaseuser = FirebaseAuth.getInstance().currentUser!!


        if (IDforprofile == firebaseuser.uid)
        {

             view.edit_button.text = "Edit Profile"
        }



        else if(IDforprofile != firebaseuser.uid)
        {
            connect()
        }

        getconnected()



        storeuserData()



    }

    private fun connect()
    {
        val connection = firebaseuser?.uid.let { lambda ->
            FirebaseDatabase.getInstance().reference
                .child("Connect").child(lambda.toString())
                .child("Connected")
        }



        connection.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.child(IDforprofile).exists()) {
                    view?.edit_button?.text = "Connected"
                } else {
                    view?.edit_button?.text = "Connect"
                }

            }

        })

    }


    private fun getconnected()
    {
        val connected = FirebaseDatabase.getInstance().reference
                .child("Connect").child(IDforprofile)
                .child("Connected")


            connected.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        view?.numberofconnections?.text = snapshot.childrenCount.toString()
                    }

                }


            })



            }



    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was

            Log.d("ProfileFragment", "Photo was selected")

            // where the image is stored on the machine
            selectedPhotoUri = data.data


            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)


            circle_image_profile.setImageBitmap(bitmap)


            imageView.alpha = 0f


            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return


        val filename = UUID.randomUUID().toString()


        val ref = FirebaseStorage.getInstance().getReference("/userStorage/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("ProfileFragment", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("ProfileFragment", "File Download URL Location: $it")

                    saveImageToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("ProfileFragment", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveImageToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/usersID/$uid")

        ref.child("profileImageUrl").setValue(profileImageUrl)
            .addOnSuccessListener {
                Log.d("ProfileFragment", "Finally we saved the profile image to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("ProfileFragment", "Failed to set value to database: ${it.message}")
            }
    }


    private fun storeuserData()
    {


        model.selectedUser.observe(viewLifecycleOwner, Observer<ProfileUser> { item ->
            view?.textView?.text = item!!.getFNAME()  + " " + item.getLName()
            view?.Education?.text= item.getEducation()
            view?.Goals?.text= item.getBizNotioGoals()
            view?.Interests?.text= item.getInterests()
            view?.profession?.text= item.getProfession()
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)

            // So the tutorial was using picasso but I found better loading times with glide
            Glide.with(view?.context!!)
                .applyDefaultRequestOptions(requestOptions)
                .load(item.getProfileImageUrl())
                .into(view?.findViewById<CircleImageView>(R.id.circle_image_profile)!!)
            try {
                imageView.alpha = 0f
            }
            catch (e: Exception) {
                Log.d("ProfileFragment", "Prevented fatal crash, imageView is null...")
            }

        })

    }


    override fun onStop() {
        super.onStop()

        val preference = context?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)?.edit()
        preference?.putString("IDforprofile", firebaseuser.uid)
        preference?.apply()
    }



    override fun onPause() {
        super.onPause()

        val preference = context?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)?.edit()
        preference?.putString("IDforprofile", firebaseuser.uid)
        preference?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing()) {
            progressDialog!!.dismiss()
        }
        val preference = context?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)?.edit()
        preference?.putString("IDforprofile", firebaseuser.uid)
        preference?.apply()
    }

    private fun setCurrentProfilePicture(view: View){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/usersID/$uid")

        var profileImageUrl: String

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)

                // So the tutorial was using picasso but I found better loading times with glide
                Glide.with(view.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(profileImageUrl)
                    .into(view.findViewById<CircleImageView>(R.id.circle_image_profile))
                try {
                    imageView.alpha = 0f
                }
                catch (e: Exception) {
                    Log.d("ProfileFragment", "Prevented fatal crash, imageView is null...")
                }

                Log.d("ProfileFragment", "currently set to: $profileImageUrl")
            }
        })
    }

}
