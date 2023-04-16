package com.epfl.drawyourpath.mainpage.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.Friend
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsListAdapter
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsViewModel
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsViewModelFactory
import com.epfl.drawyourpath.userProfile.UserModel

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private lateinit var viewModel: FriendsViewModel
    private lateinit var friendsListAdapter: FriendsListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database: Database = FireDatabase()

        // Set up the RecyclerView with an empty adapter initially
        val recyclerView: RecyclerView = view.findViewById(R.id.friends_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        friendsListAdapter = FriendsListAdapter { friend, isFriend ->
            viewModel.addOrRemoveFriend(friend, isFriend)
        }
        recyclerView.adapter = friendsListAdapter

        // Initialize the ViewModel
        val userAccountFuture = database.getLoggedUserAccount()
        Log.w("Debug", "View is created!!!!!!!!!!!!!!")

        userAccountFuture.thenApply { userModel ->

            // Initialize the ViewModel with the userModel
            val factory = FriendsViewModelFactory(userModel)
            viewModel = ViewModelProvider(this, factory).get(FriendsViewModel::class.java)

            // Update the adapter with the fetched data
            recyclerView.adapter = friendsListAdapter

            // Observe the friendsList LiveData from the ViewModel
            viewModel.friendsList.observe(viewLifecycleOwner) { friends ->
                friendsListAdapter.updateFriendsList(friends)
            }

            // Set up the search functionality
            val searchView: SearchView = view.findViewById(R.id.friends_search_bar)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                /*(override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.search(query ?: "")
                    return true
                }*/

                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null && query.isNotBlank()) {
                        Log.d("Debug", "Submitting query!!!")
                        database.isUsernameAvailable(query).thenApply { isAvailable ->
                            Log.d("Debug", "fount if username is available! ")

                            if (isAvailable == true) {
                                Toast.makeText(
                                    requireContext(),
                                    "Username not found.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("Debug", "username not found! ")
                            } else {
                                Log.d("Debug", "fount that username is available! ")
                                database.getUserIdFromUsername(query).thenApply { userId ->
                                    Log.d("Debug", "Got user ID! ")
                                    database.getUserAccount(userId).thenApply { userModel ->
                                        Log.d("Debug", "Got user account! ")
                                        val newFriend = Friend(
                                            userModel.getUserId(),
                                            userModel.getUsername(),
                                            R.drawable.ic_profile_placeholder,
                                            false
                                        )

                                        Log.d("Debug", "Added user to list! ")

                                        // Add the new friend to the list and update the UI
                                        viewModel.addPotentialFriend(newFriend)

                                    }
                                }
                            }
                        }

                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.search(newText ?: "")
                    return true
                }



            })
        }.exceptionally { exception ->
            Log.d("Debug", "exceptionally called")
            // Handle any exceptions that occurred during the CompletableFuture execution
            Log.e(TAG, "Error while getting UserAccount: ", exception)
            null
        }
    }
}
