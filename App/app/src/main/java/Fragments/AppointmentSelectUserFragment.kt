package Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biznoti0.Model.User
import com.example.biznoti0.R
import com.example.biznoti0.ViewModels.AppointmentViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_appointment_select_user.*
import kotlinx.android.synthetic.main.layout_chat_new_message_user_row.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppointmentSelectUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppointmentSelectUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_appointment_select_user, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentSelectUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    private val model: AppointmentViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView_selectUser.adapter = adapter
        fetchUsers()
    }


    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/usersID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("AppointmentSelectUserFragment", it.toString())
                    val user = it.getValue<User>()
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener {item, view ->

                    val userItem = item as UserItem
                    model.select(userItem.user!!)
                    findNavController().navigate(R.id.appointmentSetup, null)
                }
                recyclerView_selectUser.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

// this is the class we use to represent items for our recycler view and it communicates with the GroupieAdapter
class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.FName
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)

        // So the tutorial was using picasso but I found better loading times with glide
        Glide.with(viewHolder.itemView)
            .applyDefaultRequestOptions(requestOptions)
            .load(user.profileImageUrl)
            .into(viewHolder.itemView.imageview_new_message)

    }

    override fun getLayout(): Int {
        return R.layout.layout_chat_new_message_user_row
    }
}