package Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.biznoti0.Model.ChatMessage
import com.example.biznoti0.Model.User
import com.example.biznoti0.R
import com.example.biznoti0.ViewModels.AppointmentViewModel
import com.example.biznoti0.ViewModels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_appointment_setup.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppointmentSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppointmentSetupFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_appointment_setup, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppointmentSetupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentSetupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val model: AppointmentViewModel by activityViewModels()
    private var currentUser: User = User()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        model.selectedUser.observe(viewLifecycleOwner, Observer<User> { item ->
            currentUser = item
            val calendarDate = Calendar.getInstance()
            val year = calendarDate.get(Calendar.YEAR)
            val month = calendarDate.get(Calendar.MONTH)
            val day = calendarDate.get(Calendar.DAY_OF_MONTH)

            pickDateBtn.setOnClickListener {
                val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { viewIt :DatePicker , mYear :Int, mMonth :Int, mDay :Int ->
                    dateTextView.text = "$mDay/$mMonth/$mYear"
                }, year, month, day)
                datePickerDialog.show()
            }

            pickTimeBtn.setOnClickListener {
                val calenderTime = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog.OnTimeSetListener { timePicker :TimePicker, hour :Int, minute :Int ->
                    calenderTime.set(Calendar.HOUR_OF_DAY, hour)
                    calenderTime.set(Calendar.MINUTE, minute)
                    timeTextView.text = SimpleDateFormat("HH:MM").format(calenderTime.time)
                }
                TimePickerDialog(requireContext(), timePickerDialog, calenderTime.get(Calendar.HOUR_OF_DAY), calenderTime.get(Calendar.MINUTE), true).show()
            }

            requestAppointment.setOnClickListener {
                sendAppointmentToFirebase()
            }

        })


    }
    private val chatModel: ChatViewModel by activityViewModels()
    private fun sendAppointmentToFirebase() {
        val fromUser = ChatListFragment.currentUser
        val text = "Hello, ${fromUser?.FName + " " + fromUser?.LName} has requested you to an appointment " +
                "occuring in ${dateTextView.text.toString()} at ${timeTextView.text.toString()} with the following detail: \n\n" +
                "Title: ${appointment_title.text.toString()}\n " +
                "Detail: ${appointment_detail.text.toString()}\n" +
                "Do you accept the request? Yes or No"

        val fromId = FirebaseAuth.getInstance().uid
        val toId = currentUser.usersID

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId!!, System.currentTimeMillis())
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("AppointmentSetupFragment", "Message has been saved to firebase: ${reference.key}")
                Toast.makeText(requireContext(), "Appointment Request Message has been sent", Toast.LENGTH_LONG).show()
                if (fromUser != null) {
                    chatModel.select(currentUser)
                }
                findNavController().navigate(R.id.chatLogFragment)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)


    }
}
