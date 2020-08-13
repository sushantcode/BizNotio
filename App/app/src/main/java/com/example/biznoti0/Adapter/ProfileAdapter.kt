package com.example.biznoti0.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biznoti0.Model.ProfileUser
import com.example.biznoti0.R
import com.example.biznoti0.ViewModels.SearchViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ProfileAdapter (
                        private var model: SearchViewModel,
                        private var usercontext: Context,
                        private var userlist:List<ProfileUser>,
                        private var Fragment: Boolean = false) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>()

{

    //View holder to return views on the layout created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ViewHolder {
        val view = LayoutInflater.from(usercontext).inflate(R.layout.result_layout,parent,false)
        return ProfileAdapter.ViewHolder(view)
    }

    //gets the total item of searches obtained from the database
    override fun getItemCount(): Int {
        return userlist.size

    }
 //Displays the data obtained from search
    override fun onBindViewHolder(holder: ProfileAdapter.ViewHolder, position: Int) {
      val userobtainer = userlist[position]

     holder.Name.text = userobtainer.getFNAME() + "  "+ userobtainer.getLName()
     holder.AccountType.text = userobtainer.getACType()

     val requestOptions = RequestOptions()
         .placeholder(R.drawable.profile)
         .error(R.drawable.profile)

     // So the tutorial was using picasso but I found better loading times with glide
     Glide.with(holder.itemView)
         .applyDefaultRequestOptions(requestOptions)
         .load(userobtainer.getProfileImageUrl())
         .into(holder.itemView.findViewById<CircleImageView>(R.id.search_list_element_image)!!)

//     Picasso.get().load(userobtainer.getProfileImageUrl()).placeholder(R.drawable.profile).into(holder.ProfilePicture)



      holder.itemView.setOnClickListener(View.OnClickListener {
          Log.d("ProfileAdapter", "userobtainer: ${userobtainer.toString()}")
          Log.d("ProfileAdapter", "context: $usercontext")
          model.select(userobtainer)

          val imm = usercontext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
          imm.hideSoftInputFromWindow(it.windowToken, 0)

          it.findNavController().navigate(R.id.navigation_profile, null)





      })
    }

class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val Name:TextView = itemView.findViewById(R.id.search_list_element_user_name)
        val AccountType:TextView = itemView.findViewById(R.id.search_list_element_account_type)
        val ProfilePicture:ImageView = itemView.findViewById(R.id.search_list_element_image)

    }

}