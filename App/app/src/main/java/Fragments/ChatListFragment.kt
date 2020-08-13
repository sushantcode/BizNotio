package Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.biznoti0.Model.ChatMessage
import com.example.biznoti0.Model.User
import com.example.biznoti0.R
import com.example.biznoti0.ViewModels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.layout_chat_list_element.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null


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
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var currentUser: User? = null
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val model: ChatViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chat_list_recycler_view.adapter = adapter
        chat_list_recycler_view.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener {item, _ ->
            Log.d("ChatListFragment", "clicked")
            val row = item as LatestMessageRow

            model.select(row.user!!)

            findNavController().navigate(R.id.chatLogFragment, null)
        }
//        setupDummyRows()
        listenForLatestMessages()
        fetchCurrentUser()
        // Recycler view node initialized here

//        initRecyclerView()
//        addDataSet()

        // search button will trigger the new message fragment
        val searchButton = view.findViewById<RelativeLayout>(R.id.search_button)
        searchButton?.setOnClickListener {
            findNavController().navigate(R.id.chatNewMessageFragment, null)
        }

        val appointmentButton = view.findViewById<RelativeLayout>(R.id.appointment_button)
        appointmentButton?.setOnClickListener {
            findNavController().navigate(R.id.appointmentSelectUser, null)
        }

    }

    class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
        var user: User? = null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.chat_list_element_message_preview.text = chatMessage.text

            val chatPartnerId: String = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                chatMessage.toId
            } else {
                chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/usersID/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                    viewHolder.itemView.chat_list_element_user_name.text = "${user?.FName} ${user?.LName} "

                    Glide.with(viewHolder.itemView).load(user?.profileImageUrl).into(viewHolder.itemView.chat_list_element_image_circle)
                    viewHolder.itemView.chat_list_element_image.alpha = 0f

                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        }

        override fun getLayout(): Int {
            return R.layout.layout_chat_list_element
        }

    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/usersID/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                currentUser = snapshot.getValue(User::class.java)
                Log.d("ChatListFragment", "Current user: ${currentUser?.usersID}")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
