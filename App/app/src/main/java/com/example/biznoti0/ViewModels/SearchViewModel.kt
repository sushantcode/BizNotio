package com.example.biznoti0.ViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biznoti0.Model.ProfileUser


class SearchViewModel : ViewModel() {
    val selectedUser: MutableLiveData<ProfileUser> = MutableLiveData<ProfileUser>()
    val currentlyLoggedInUser: MutableLiveData<ProfileUser> = MutableLiveData<ProfileUser>()

    // when we do selectedUser.value we will get the User object that was passed in
    fun select(user : ProfileUser) {
        selectedUser.value = user
    }

    fun setCurrentUser(user: ProfileUser) {
        currentlyLoggedInUser.value = user
    }



//    companion object {
//        private lateinit var sInstance: SearchViewModel
//
//        @MainThread
//        fun get():SearchViewModel {
//            sInstance = if(::sInstance.isInitialized) sInstance else SearchViewModel()
//            return sInstance
//        }
//    }

}