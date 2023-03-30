package com.epfl.drawyourpath.mainpage.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
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
        friendsListAdapter = FriendsListAdapter { friend ->
            viewModel.addFriend(friend)
        }
        recyclerView.adapter = friendsListAdapter

        // Initialize the ViewModel
        val userAccountFuture = database.getLoggedUserAccount()
        Log.w("Debug", "View is created!!!!!!!!!!!!!!")

        userAccountFuture.thenApply { userModel ->
            Log.d("Debug", "thenApply called")
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
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.search(query ?: "")
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
