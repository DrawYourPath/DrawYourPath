package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.userProfile.UserModel

class FriendsViewModel(/*private val userModel: UserModel* TODO uncomment later*/) : ViewModel() {

    // For now, create a dummy list of friends. TODO Replace with real data.
    val allFriends = listOf(
        Friend(1, "John Doe", R.drawable.ic_profile_placeholder, false),
        Friend(2, "Jane Smith", R.drawable.ic_profile_placeholder, true),
        // Add more friends here.
    )

    // _friendsList is a MutableLiveData, which is a private mutable version of the LiveData.
    // This is used to update the value internally within the ViewModel.
    private val _friendsList = MutableLiveData<List<Friend>>()

    // The init block is executed when the ViewModel is created.
    init {
        /*TODO Uncomment this code when you have a UserModel class instance.
        // Get the friend list from the UserModel
        val friendsMap = userModel.getFriendList()

        // Convert the friendsMap to a list of Friend objects
         val allFriends = friendsMap.map { (username, userId) ->
            Friend(userId.toInt(), username, R.drawable.ic_profile_placeholder, true)
        }
        */

        // Set the initial value of _friendsList to allFriends.
        _friendsList.value = allFriends
    }




    // friendsList is a LiveData that the UI will observe for changes.
    // It is exposed to the UI to prevent modification from outside the ViewModel.
    val friendsList: LiveData<List<Friend>> = _friendsList


    /**
     * The search() function filters the friends list based on the search query.
     */
    fun search(query: String) {
        if (query.isEmpty()) {
            // If the query is empty, show the full list of friends.
            _friendsList.value = allFriends
        } else {
            // If the query is not empty, show only friends whose names contain the query.
            _friendsList.value = allFriends.filter { friend ->
                friend.name.contains(query, ignoreCase = true)
            }
        }
    }

    /**
     * The addFriend() function updates the isFriend property of a Friend object.
     */
    fun addFriend(friend: Friend) {
        // Map through all friends and update the isFriend property for the matching friend.
        val updatedFriendsList = allFriends.map {
            if (it.id == friend.id) {
                it.copy(isFriend = true)
            } else {
                it
            }
        }
        // Update _friendsList with the updated friends list.
        _friendsList.value = updatedFriendsList
    }
}