package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.utils.Utils

class FriendsViewModel(private val userId: String, private val database: Database) : ViewModel() {

    // empty list of Friend
    var allFriends = listOf<Friend>()

    // _friendsList is a MutableLiveData, which is a private mutable version of the LiveData.
    // This is used to update the value internally within the ViewModel.
    private val _friendsList = MutableLiveData<List<Friend>>()

    /**
     * Used to upadte the friend list value with the new friend list give in argument
     * @param newFriendsList new value of the friendsList
     */
    fun loadFriends(newFriendsList: List<String>) {
        // Use a mutable list to store the realFriends
        val realFriends = mutableListOf<Friend>()

        // Counter for tracking when all friends are loaded
        var friendsLoaded = 0

        // Iterate through each userId in friendsList
        for (userId in newFriendsList) {
            // Get the username CompletableFuture and when the CompletableFuture completes, update the list of friends
            database.getUsername(userId).whenComplete { username, exception ->
                if (exception == null) {
                    database.getUserData(userId).whenComplete { userdata, exception ->
                        if (exception == null) {
                            // Add the new Friend object to the realFriends list
                            realFriends.add(
                                Friend(
                                    userdata.userId!!,
                                    username,
                                    userdata.picture?.let { pic -> Utils.decodePhoto(pic) },
                                    true,
                                ),
                            )

                            // Increment the friendsLoaded counter
                            friendsLoaded++

                            // Check if all friends have been loaded
                            if (friendsLoaded == newFriendsList.size) {
                                // Concatenate the testFriends and realFriends lists
                                allFriends = realFriends

                                // Set the initial value of _friendsList to allFriends.
                                _friendsList.postValue(allFriends)
                            }
                        } else {
                            // Handle the exception (e.g., log the error, show a message to the user, etc.)
                        }
                    }
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
     * The addOrRemoveFriend() function adds or removes a friend from the list of friends and updated the database.
     */
    fun addOrRemoveFriend(friend: Friend, isFriend: Boolean) {
        Log.i("Friends", "Performing action for ${friend.id}")

        if (isFriend) {
            database.removeFriend(userId, friend.id)
                .thenApplyAsync { Log.w("Debug", "Friend removed!") }
                .exceptionally { Log.w("Debug", "Error removing friend!") }
        } else {
            database.addFriend(userId, friend.id)
                .thenApplyAsync { Log.w("Debug", "Friend added!") }
                .exceptionally { Log.w("Debug", "Error adding friend!") }
        }

        val updatedFriendsList = allFriends.map {
            if (it.id == friend.id) {
                it.isFriend = !isFriend
                it
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
        // Check if the friend is already in the list.
        val isFriendAlreadyInList = allFriends.any { it.id == friend.id }

        // If the friend is not already in the list, add them.
        if (!isFriendAlreadyInList) {
            // Add the new friend to the list of all friends.
            allFriends = allFriends + friend
            // Update the friends list to show the new friend.
            search("")
        } else {
            // You can show a message here that the user is already in the list if you want.
        }
    }
}
