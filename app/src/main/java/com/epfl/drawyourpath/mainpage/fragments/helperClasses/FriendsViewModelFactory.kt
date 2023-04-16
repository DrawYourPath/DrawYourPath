package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.userProfile.UserModel


/**
 * Factory for creating a [FriendsViewModel] with a constructor that takes a [UserModel].
 */
class FriendsViewModelFactory(private val userModel: UserModel, private val database: Database) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            return FriendsViewModel(userModel, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}