package Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.biznoti0.AddPost
import com.example.biznoti0.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_create_post.*
import java.util.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatePost.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatePost : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatePost.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePost().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendPost = view.findViewById<MaterialButton>(R.id.CreatePost)
        val proposalId = UUID.randomUUID().toString()
        sendPost?.setOnClickListener {
            saveProposalToFirebaseDatabase(proposalId)
        }

        val proposalNameField = view.findViewById<EditText>(R.id.ProposalName)

        proposalNameField?.setOnClickListener {

        }

        select.setOnClickListener {
            val intent = Intent(activity, AddPost::class.java)
            intent.putExtra("ProposalId", proposalId)
            startActivity(intent)

        }




    }

    private fun saveProposalToFirebaseDatabase(proposalId: String) {
        val dbRef: CollectionReference = FirebaseFirestore.getInstance().collection("proposals")

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val proposalName: String = ProposalName.text.toString()
        val proposalType: String = ProposalType.text.toString()
        val proposalDescription: String = ProposalDescription.text.toString()
        val minimumCase: String = MinimumCase.text.toString()
        val owner: String = uid


        if (proposalName.isBlank()) {
            Toast.makeText(requireContext(), "Proposal Name is must", Toast.LENGTH_LONG).show()
        }

        else if (proposalType.isBlank()) {
            Toast.makeText(requireContext(), "Proposal Type is must", Toast.LENGTH_LONG).show()
        }

        else if (proposalDescription.isBlank()) {
            Toast.makeText(requireContext(), "Proposal Description is must", Toast.LENGTH_LONG).show()
        }
        else if (minimumCase.isBlank()) {
            Toast.makeText(requireContext(), "minimumCase is must", Toast.LENGTH_LONG).show()
        }

        else {
            try {
                val proposalItem = HashMap<String, Any>()
                val getImgLink = FirebaseDatabase.getInstance().reference.child("ImagePosts").child(proposalId).child("postImage")
                getImgLink.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val imgLink = snapshot.value.toString()
                        proposalItem.put("link", imgLink)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
                proposalItem.put("owner", owner)
                proposalItem.put("proposalId", proposalId)
                proposalItem.put("proposalName", proposalName)
                proposalItem.put("proposalType", proposalType)
                proposalItem.put("proposalDescription", proposalDescription)
                proposalItem.put("minimumCase", minimumCase)

                proposalItem.put("timeCreated", System.currentTimeMillis())
                dbRef.document(proposalId).set(proposalItem).addOnSuccessListener { void: Void? ->
                    Toast.makeText(requireContext(), "Proposal has been Posted", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.navigation_home, null)
                }.addOnFailureListener {
                    exception: java.lang.Exception -> Toast.makeText(requireContext(), "Proposal has been Posted", Toast.LENGTH_LONG).show()

                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

}
