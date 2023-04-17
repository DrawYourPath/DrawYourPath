package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
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

        // Initialize the ViewModel
        /* uncomment later
        val userModel: UserModel  // Get the UserModel instance from your app. TODO
        val factory = FriendsViewModelFactory(userModel)
        viewModel = ViewModelProvider(this, factory).get(FriendsViewModel::class.java)
        */

        view.findViewById<Button>(R.id.BT_ScanQR).setOnClickListener { onScanQRClicked() }

        viewModel = ViewModelProvider(this).get(FriendsViewModel::class.java)

        // Set up the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.friends_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        friendsListAdapter = FriendsListAdapter { friend ->
            viewModel.addFriend(friend)
        }
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