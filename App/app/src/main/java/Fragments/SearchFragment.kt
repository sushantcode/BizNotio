package Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biznoti0.Adapter.ProfileAdapter
import com.example.biznoti0.Model.ProfileUser
import com.example.biznoti0.R
import com.example.biznoti0.ViewModels.SearchViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var searchedView: RecyclerView? = null
    private var profileadapter: ProfileAdapter? = null
    private var userlist: MutableList<ProfileUser>? = null

    private val model: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchedView = view.findViewById(R.id.recycler_view)
        searchedView?.setHasFixedSize(true)
        searchedView?.layoutManager = LinearLayoutManager(context)
        userlist = ArrayList()

        profileadapter = context?.let { ProfileAdapter(model, it, userlist as ArrayList<ProfileUser>, true) }
        searchedView?.adapter = profileadapter
        view.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (view.searchView.text.toString() == "") {

                } else {
                    searchedView?.visibility = View.VISIBLE
                    getUsers()
                    results(p0.toString().toLowerCase())

                }
            }


        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun results(enteredtext: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("usersID")

            .orderByChild("ACType").startAt(enteredtext).endAt(enteredtext+"\uf8ff")


        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userlist?.clear()
                    for (snapshots in snapshot.children) {
                        val datauser = snapshots.getValue(ProfileUser::class.java)
                        Log.d("SearchFragment", "${datauser.toString()}")
                        if (datauser != null) {
                            userlist?.add(datauser)
                        }
                    profileadapter?.notifyDataSetChanged()
                }


            }

        }


        )
    }


    private fun getUsers() {
        val getusersdata = FirebaseDatabase.getInstance().getReference().child("UsersID")
        getusersdata.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (view?.searchView?.text.toString() == "") {
                    userlist?.clear()
                    for (snapshots in snapshot.children) {
                        val datauser = snapshots.getValue(ProfileUser::class.java)
                        if (datauser != null) {
                            userlist?.add(datauser)
                        }
                    }
                    profileadapter?.notifyDataSetChanged()
                }
            }

        }


        )
    }
}




