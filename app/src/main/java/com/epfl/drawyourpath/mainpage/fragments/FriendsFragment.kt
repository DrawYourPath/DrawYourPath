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
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.Friend
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsListAdapter
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsViewModel
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsViewModelFactory
import com.epfl.drawyourpath.userProfile.UserModel

class FriendsFragment(private val database: Database) : Fragment(R.layout.fragment_friends) {
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

        val database: Database = this.database

        // Set up the RecyclerView with an empty adapter initially
        val recyclerView: RecyclerView = view.findViewById(R.id.friends_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        friendsListAdapter = FriendsListAdapter { friend, isFriend ->
            viewModel.addOrRemoveFriend(friend, isFriend)
        }
        recyclerView.adapter = friendsListAdapter

        // Initialize the ViewModel
        val userAccountFuture = database.getLoggedUserAccount()

        userAccountFuture.thenApply { userModel ->

            // Initialize the ViewModel with the userModel
            val factory = FriendsViewModelFactory(userModel, this.database)
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
                    if (query != null && query.isNotBlank()) {
                        database.isUsernameAvailable(query).thenApply { isAvailable ->

                            if (isAvailable == true) {
                                Toast.makeText(
                                    requireContext(),
                                    "Username not found.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                database.getUserIdFromUsername(query).thenApply { userId ->
                                    database.getLoggedUserAccount().thenApply { loggedUserModel ->
                                        if (userId != loggedUserModel.getUserId()) {
                                            database.getUserAccount(userId).thenApply { userModel ->
                                                val newFriend = Friend(
                                                    userModel.getUserId(),
                                                    userModel.getUsername(),
                                                    userModel.getProfilePhoto(),
                                                    false
                                                )


                                                // Add the new friend to the list and update the UI
                                                viewModel.addPotentialFriend(newFriend)

                                            }

                                        }else{
                                            Toast.makeText(
                                                requireContext(),
                                                "You can't add yourself as a friend.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
            // Handle any exceptions that occurred during the CompletableFuture execution
            Log.e(TAG, "Error while getting UserAccount: ", exception)
            null
        }
    }

    private fun onScanQRClicked() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.scanQRCode()
            .thenApply {
                // TODO: Add friend from ID "it"
                if (it == null) {
                    Toast.makeText(mainActivity, "Scan cancelled", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(mainActivity, "Scanned $it", Toast.LENGTH_LONG).show()
                }
            }
            .exceptionally {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}


class FriendsFragmentFactory(private val database: Database) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            FriendsFragment::class.java.name -> FriendsFragment(database)
            else -> super.instantiate(classLoader, className)
        }
    }
}