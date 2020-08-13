package Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.biznoti0.AddPost
import com.example.biznoti0.Model.ChatMessage
import com.example.biznoti0.Model.User
import com.example.biznoti0.R
import com.example.biznoti0.VideoActivity
import com.example.biznoti0.ViewModels.ChatViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.layout_chat_log_from_row.view.*
import kotlinx.android.synthetic.main.layout_chat_log_to_row.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatLogFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var filePath: Uri? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    companion object {


        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    private val model: ChatViewModel by activityViewModels()
    private var toId: String? = null
    private var userObject: User = User()
    private var adapter = GroupAdapter<GroupieViewHolder>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // call button
        val callButton = view.findViewById<ImageButton>(R.id.chat_log_call_button)
        callButton.setOnClickListener {
            Log.d("ChatLogFragment", "Call button pressed")
            val intent = Intent(this@ChatLogFragment.context, VideoActivity::class.java)
            startActivity(intent)
        }

        // back button
        val backButton =  view.findViewById<ConstraintLayout>(R.id.chat_log_back_button)
        backButton.setOnClickListener {
            Log.d("ChatLogFragment", "Back button pressed")
            findNavController().navigate(R.id.chatListFragment, null)
        }


        text_field_pay_button.setOnClickListener {
            Log.d("ChatLogFragment", "Pay Button Pressed")
            findNavController().navigate(R.id.Payment, null)

        }

        text_field_attachment_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            startActivityForResult(intent, 1000)

        }


        text_field_send_button.visibility = View.GONE
        chat_log_recycler_view.adapter = adapter
        model.selectedUser.observe(viewLifecycleOwner, Observer<User> { item ->
            chat_header_user_text.text = item.FName
            toId = item.usersID
            userObject = item
        })

//        setupDummyData()
        listenForMessages()
        text_field_text_view.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text_field_microphone_button.visibility = View.GONE

                text_field_send_button.alpha = 1f
                text_field_send_button.visibility = View.VISIBLE

                if (count == 0) {
                    text_field_microphone_button.visibility = View.VISIBLE
                    text_field_send_button.visibility = View.GONE
                }
            }
        })

        text_field_send_button.setOnClickListener {
            Log.d("ChatLogFragment", "Attempt to send message...")
            performSendMessage()
        }

    }

    private fun listenForMessages() {

        model.selectedUser.observe(viewLifecycleOwner, Observer<User> { item ->
            chat_header_user_text.text = item.FName
            toId = item.usersID
            userObject = item
            val fromId = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

            ref.addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatMessage = snapshot.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        Log.d("ChatLogFragment", chatMessage.text)

                        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                            val currentUser = ChatListFragment.currentUser ?: return
                            adapter.add(ChatFromItem(chatMessage.text, currentUser))
                        } else {
                            adapter.add(ChatToItem(chatMessage.text, userObject))
                        }
                    }
//                    val currentCount = adapter.itemCount
                    try {
                        chat_log_recycler_view.scrollToPosition(adapter.itemCount - 1)
                    } catch (e: Exception) {
                        Log.d("ChatLogFragment", "Avoided fatal crash")
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }
            })
        })


    }



    private fun performSendMessage() {
        model.selectedUser.observe(viewLifecycleOwner, Observer<User> { item ->
            chat_header_user_text.text = item.FName
            toId = item.usersID
            userObject = item
            val text = text_field_text_view.text.toString()
            val fromId = FirebaseAuth.getInstance().uid

            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

            val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

            val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId!!, System.currentTimeMillis())
            reference.setValue(chatMessage)
                    .addOnSuccessListener {
                        Log.d("ChatLogFragment", "Message has been saved to firebase: ${reference.key}")
                        text_field_text_view.text.clear()
                        chat_log_recycler_view.scrollToPosition(adapter.itemCount - 1)
                    }
            toReference.setValue(chatMessage)

            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessageRef.setValue(chatMessage)

            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessageToRef.setValue(chatMessage)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == AddPost.IMAGE_PICK_CODE) {
            if (data != null && data.data != null) {
// val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
                filePath = data.data
                val storageref = FirebaseStorage.getInstance().reference.child("Images chat")
                val ref = FirebaseDatabase.getInstance().reference
                val message = ref.push().key

                val filepath = storageref.child("$message.jpg")
                var uploadTask: StorageTask<*>
                uploadTask = filepath.putFile(filePath!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {

                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filepath.downloadUrl

                }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        val url = downloadUrl.toString()
                        Log.d("ChatLogFragment", "$url")

                        model.selectedUser.observe(viewLifecycleOwner, Observer<User> { item ->
                            chat_header_user_text.text = item.FName
                            toId = item.usersID
                            userObject = item
                            val text = "attachment file: $url"
                            val fromId = FirebaseAuth.getInstance().uid

                            val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

                            val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

                            val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId!!, System.currentTimeMillis())
                            reference.setValue(chatMessage)
                                .addOnSuccessListener {
                                    Log.d("ChatLogFragment", "Message has been saved to firebase: ${reference.key}")
                                    text_field_text_view.text.clear()
                                    chat_log_recycler_view.scrollToPosition(adapter.itemCount - 1)
                                }
                            toReference.setValue(chatMessage)

                            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                            latestMessageRef.setValue(chatMessage)

                            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                            latestMessageToRef.setValue(chatMessage)
                        })

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = message!!
                        postMap["message"]="sent an image"
                        postMap["sender"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["receiver"] = ""
                        postMap["url"]=url


                        ref.child("user-messages").child(id.toString()!!).setValue(postMap)
                            .addOnCompleteListener {
                                task ->
                                if(task.isSuccessful)
                                {
                                    val chatref = FirebaseDatabase.getInstance().reference.child("Images Chat")


                                    chatref.addListenerForSingleValueEvent(object :ValueEventListener
                                    {
                                        override fun onCancelled(error: DatabaseError) {
                                            //TODO("Not yet implemented")
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()) {

                                                chatref.child("id").setValue(firebaseUser!!.uid)
                                            }






                                        }

                                    }
                                    )

                                    val chatreff = FirebaseDatabase.getInstance().reference.child("Images Chat")
//                                        .child("firebaseUser!!.uid")

//                                    chatreff.child("id").setValue(firebaseUser!!.uid)
                                }
                            }

                    }
                })
    }
}
    }
}












class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        val profileImageUrl = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_from_row

        Glide.with(viewHolder.itemView).load(profileImageUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.layout_chat_log_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        val profileImageUrl = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_to_row

        Glide.with(viewHolder.itemView).load(profileImageUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.layout_chat_log_to_row
    }
}




