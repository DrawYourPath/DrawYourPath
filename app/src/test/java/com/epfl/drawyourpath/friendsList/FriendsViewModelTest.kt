package com.epfl.drawyourpath.friendsList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.Friend
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.FriendsViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class FriendsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FriendsViewModel
    private lateinit var friendsListObserver: Observer<List<Friend>>

    @Before
    fun setUp() {
        viewModel = FriendsViewModel()
        friendsListObserver = mock(Observer::class.java) as Observer<List<Friend>>
        viewModel.friendsList.observeForever(friendsListObserver)
    }

    @Test
    fun searchFiltersFriendsCorrectly() {
        viewModel.search("john")

        val filteredList = listOf(
            Friend(1, "John Doe", R.drawable.ic_profile_placeholder, false)
        )

        assertEquals(filteredList, viewModel.friendsList.value)
        verify(friendsListObserver).onChanged(filteredList)
    }

    @Test
    fun addFriendUpdatesIsFriendPropertyCorrectly() {
        val friend = Friend(1, "John Doe", R.drawable.ic_profile_placeholder, false)
        viewModel.addFriend(friend)

        val updatedFriend = Friend(1, "John Doe", R.drawable.ic_profile_placeholder, true)
        val updatedFriendsList = viewModel.friendsList.value
        assertEquals(updatedFriend, updatedFriendsList?.first { it.id == friend.id })
    }
}