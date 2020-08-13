package com.example.biznoti0.ViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biznoti0.Model.User


class AppointmentViewModel : ViewModel() {
    val selectedUser: MutableLiveData<User> = MutableLiveData<User>()

    // when we do selectedUser.value we will get the User object that was passed in
    fun select(user : User) {
        selectedUser.value = user
    }

}