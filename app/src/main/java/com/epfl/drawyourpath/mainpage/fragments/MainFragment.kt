package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.preferences.PreferencesFragment
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainFragment : Fragment(R.layout.fragment_main) {

    // The navigation bar at the bottom of the screen
    private lateinit var bottomNavigationView: BottomNavigationView

    // The drawer menu displayed when clicking the "head" icon
    private lateinit var drawerLayout: DrawerLayout

    private val userCached: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the components of the screen
        setupTopBar(view)
        setupProfileButton(view)
        setupDrawerNavigationView(view)
        setupBottomNavigationView(view)

        // Create an instance of your database
        val database: Database = FirebaseDatabase()

        // Create an instance of FriendsFragmentFactory and set it as the fragment factory
        val friendsFragmentFactory = FriendsFragmentFactory(database)
        requireActivity().supportFragmentManager.fragmentFactory = friendsFragmentFactory

        // Display the main fragment when no saved state
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.draw_menu_item
            replaceFragment<DrawMenuFragment>()
        }
    }

    private fun setupTopBar(view: View) {
        val topAppBar: Toolbar = view.findViewById(R.id.topAppBar)
        // Make this toolbar behave like the main action bar
        (requireActivity() as AppCompatActivity).setSupportActionBar(topAppBar)
    }

    private fun setupProfileButton(view: View) {
        val profileImageButton: ImageButton = view.findViewById(R.id.profile_button)
        userCached.getUser().observe(viewLifecycleOwner) {
            profileImageButton.setImageBitmap(it.profilePhoto(resources))
        }
        drawerLayout = view.findViewById(R.id.drawerLayout)
        // Set a listener to open the drawer menu (we might want it on the right)
        profileImageButton.setOnClickListener {
            drawerLayout.open()
        }
    }

    private fun setupDrawerNavigationView(view: View) {
        val drawerNavigationView: NavigationView = view.findViewById(R.id.navigationView)
        val header = drawerNavigationView.getHeaderView(0)
        userCached.getUser().observe(viewLifecycleOwner) {
            header.findViewById<TextView>(R.id.header_username).text = it.username
            header.findViewById<TextView>(R.id.header_email).text = it.emailAddress
        }
        // Handle the items in the drawer menu
        drawerNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Display profile fragment
                R.id.profile_menu_item -> replaceFragment<ProfileFragment>(
                    bundleOf(
                        PROFILE_USER_ID_KEY to userCached.getUserId(),
                    ),
                )

                // Display stats fragment
                R.id.stats_menu_item -> replaceFragment<StatsFragment>()

                // Display challenge fragment
                R.id.challenge_menu_item -> replaceFragment<ChallengeFragment>()

                // Display settings fragment

                R.id.preferences_menu_item -> replaceFragment<PreferencesFragment>()
            }
            drawerLayout.close()
            true
        }
    }

    private fun setupBottomNavigationView(view: View) {
        bottomNavigationView = view.findViewById(R.id.bottom_navigation)
        // Listener that handles the items in the bottom menu
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Display community fragment
                R.id.community_menu_item -> replaceFragment<CommunityFragment>()

                // Display friends fragment
                R.id.friends_menu_item -> replaceFragment<FriendsFragment>()

                // Display drawing fragment
                R.id.draw_menu_item -> replaceFragment<DrawMenuFragment>()

                // Display history fragment
                R.id.history_menu_item -> replaceFragment<HistoryFragment>()

                // Display chat fragment
                R.id.chat_menu_item -> replaceFragment<ChatFragment>()
            }
            true
        }
    }

    private inline fun <reified F : Fragment> replaceFragment(args: Bundle? = null) {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView, F::class.java, args)
        }
    }
}
