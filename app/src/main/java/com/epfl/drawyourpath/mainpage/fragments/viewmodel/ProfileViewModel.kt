package com.epfl.drawyourpath.mainpage.fragments.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class UserState(
    val username: String = ""
)

class ProfileViewModel : ViewModel() {
    private val userState = MutableStateFlow(UserState())
}