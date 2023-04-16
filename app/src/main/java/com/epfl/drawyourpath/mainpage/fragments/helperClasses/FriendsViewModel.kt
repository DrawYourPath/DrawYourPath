package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.userProfile.UserModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FriendsViewModel(private val userModel: UserModel) : ViewModel() {

    // For now, create a dummy list of friends. TODO delete when userModel friends list is implemented
    val testFriends = listOf(
        Friend("1", "John Doe", R.drawable.ic_profile_placeholder, false),
        Friend("2", "Jane Smith", R.drawable.ic_profile_placeholder, true),


        // Add more friends here.
    )

    //empty list of Friend
    var allFriends = listOf<Friend>()

    // _friendsList is a MutableLiveData, which is a private mutable version of the LiveData.
    // This is used to update the value internally within the ViewModel.
    private val _friendsList = MutableLiveData<List<Friend>>()

    // The init block is executed when the ViewModel is created.
    init {

        // Load friends asynchronously
        loadFriends()




    }


    private fun loadFriends() {
        Log.w("Debug", "Load friends!!!!!!!!!!!!!!")
        // Get the friend list from the UserModel
        val friendsList = userModel.getFriendList()
        val database: Database = FireDatabase()

        // Use a mutable list to store the realFriends
        val realFriends = mutableListOf<Friend>()

        // Counter for tracking when all friends are loaded
        var friendsLoaded = 0

        // Iterate through each userId in friendsList
        for (userId in friendsList) {
            Log.w("Debug", "Got Username from database!!!!!!!!!!!!!!")
            // Get the username CompletableFuture
            val usernameFuture = database.getUsernameFromUserId(userId)


            // When the CompletableFuture completes, update the list of friends
            usernameFuture.whenComplete { username, exception ->
                if (exception == null) {

                    // Add the new Friend object to the realFriends list
                    realFriends.add(Friend(userId, username, R.drawable.ic_profile_placeholder, true))

                    // Increment the friendsLoaded counter
                    friendsLoaded++

                    // Check if all friends have been loaded
                    if (friendsLoaded == friendsList.size) {
                        // Concatenate the testFriends and realFriends lists
                        allFriends = testFriends + realFriends

                        // Set the initial value of _friendsList to allFriends.
                        _friendsList.postValue(allFriends)
                    }
                } else {
                    // Handle the exception (e.g., log the error, show a message to the user, etc.)
                    Log.w("Debug", "Error getting username from database!!!!!!!!!!!!!!")
                }
            }
        }
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
        userModel.addFriend(friend.id).whenComplete() { result, exception ->
            if (exception != null) {
                Log.w("Debug", "Error adding friend!!!!!!!!!!!!!!")
            }else{
                Log.w("Debug", "Friend added!!!!!!!!!!!!!!")
            }
        }
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

    /**
     * The addPotentialFriend() function adds a new potential friend to the list of friends.
     */
    fun addPotentialFriend(friend: Friend) {
        // Add the new friend to the list of all friends.
        allFriends = allFriends + friend
        // Update the friends list to show the new friend.
        search("")
    }
}