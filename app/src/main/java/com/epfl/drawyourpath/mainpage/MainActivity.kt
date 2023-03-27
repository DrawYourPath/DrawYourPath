package com.epfl.drawyourpath.mainpage

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R

import com.epfl.drawyourpath.challenge.TemporaryUser
import com.epfl.drawyourpath.mainpage.fragments.*
import com.epfl.drawyourpath.notifications.NotificationsHelper
import com.epfl.drawyourpath.preferences.PreferencesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

/**
 * Main activity of the application, should be launched after the login activity.
 */
class MainActivity : AppCompatActivity() {
    // The navigation bar at the bottom of the screen
    private lateinit var bottomNavigationView: BottomNavigationView

    // The drawer menu displayed when clicking the "head" icon
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setup the components of the screen
        setupTopBar()
        setupProfileButton()
        setupDrawerNavigationView()
        setupBottomNavigationView()

        //Display the main fragment when no saved state
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.draw_menu_item
            replaceFragment<DrawFragment>()
        }

        //Setup notifications
        NotificationsHelper(applicationContext).setupNotifications()
    }

    private fun setupTopBar() {
        val topAppBar: Toolbar = findViewById(R.id.topAppBar)
        //Make this toolbar behave like the main action bar
        setSupportActionBar(topAppBar)
    }

    private fun setupProfileButton() {
        val profileImageButton: ImageButton = findViewById(R.id.profile_button)
        drawerLayout = findViewById(R.id.drawerLayout)
        //Set a listener to open the drawer menu (we might want it on the right)
        profileImageButton.setOnClickListener {
            drawerLayout.open()
        }
    }

    private fun setupDrawerNavigationView() {
        val drawerNavigationView: NavigationView = findViewById(R.id.navigationView)
        //Handle the items in the drawer menu
        drawerNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                //Display profile fragment
                R.id.profile_menu_item -> replaceFragment<ProfileFragment>()

                //Display stats fragment
                R.id.stats_menu_item -> replaceFragment<StatsFragment>()


                //Display challenge fragment
                R.id.challenge_menu_item -> replaceFragment<ChallengeFragment>(TemporaryUser.SAMPLE_DATA)
            }
            drawerLayout.close()
            true
        }
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        // Listener that handles the items in the bottom menu
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Display community fragment
                R.id.community_menu_item -> replaceFragment<CommunityFragment>()

                // Display friends fragment
                R.id.friends_menu_item -> replaceFragment<FriendsFragment>()

                // Display drawing fragment
                R.id.draw_menu_item -> replaceFragment<DrawFragment>()

                // Display history fragment
                R.id.history_menu_item -> replaceFragment<HistoryFragment>()

                // Display settings fragment

                R.id.preferences_menu_item -> replaceFragment<PreferencesFragment>()
            }
            true
        }
    }

    private inline fun <reified F : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragmentContainerView)
        }
    }


    /**
     * use this to replace fragment and give a user
     * Needs to be changed from [TemporaryUser] to a real user class
     */
    private inline fun <reified F : Fragment> replaceFragment(user: TemporaryUser) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            replace<F>(R.id.fragmentContainerView, args = bundle)
        }
    }
}