package com.github.drawyourpath.bootcamp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

/**
 * Activity with a Drawer menu that displays some Fragments
 */
class FragmentActivity : AppCompatActivity() {
    //-----------
    //Components:
    //-----------
    //The bar at the top of the screen
    private lateinit var topAppBar: MaterialToolbar

    //The drawer menu which contains items to select
    lateinit var drawerLayout: DrawerLayout

    //The view in which the elements are displayed
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        //Setup all components of the activity
        setupAppBar()
        setupDrawerLayout()
        setupNavigationView()

        //Display the main fragment when no saved state
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragmentContainerView)
            }
        }
    }

    private fun setupAppBar() {
        topAppBar = findViewById(R.id.topAppBar)
        //Make this tool bar behave like the main action bar
        setSupportActionBar(topAppBar)
    }

    private fun setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawerLayout)
        //Set a listener to open the drawer menu
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }
    }

    private fun setupNavigationView() {
        navigationView = findViewById(R.id.navigationView)
        //Handle the items in the drawer menu
        //There may be a more efficient way
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                //Display main fragment
                R.id.activity_main_drawer_main -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<MainFragment>(R.id.fragmentContainerView)
                }

                //Display profile fragment
                R.id.activity_main_drawer_profile -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ProfileFragment>(R.id.fragmentContainerView)
                }

                //Display settings fragment
                R.id.activity_main_drawer_settings -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<SettingsFragment>(R.id.fragmentContainerView)
                }
            }
            drawerLayout.close()
            true
        }
    }

}