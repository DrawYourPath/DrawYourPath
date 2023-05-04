package com.epfl.drawyourpath.mainpage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.mainpage.fragments.*
import com.epfl.drawyourpath.notifications.NotificationsHelper
import com.epfl.drawyourpath.preferences.PreferencesFragment
import com.epfl.drawyourpath.qrcode.SCANNER_ACTIVITY_RESULT_CODE
import com.epfl.drawyourpath.qrcode.SCANNER_ACTIVITY_RESULT_KEY
import com.epfl.drawyourpath.qrcode.launchFriendQRScanner
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.CompletableFuture

const val USE_MOCK_CHALLENGE_REMINDER = "useMockChallengeReminder"
const val SCAN_QR_REQ_CODE = 8233

/**
 * Main activity of the application, should be launched after the login activity.
 */
class MainActivity : AppCompatActivity() {
    // The navigation bar at the bottom of the screen
    private lateinit var bottomNavigationView: BottomNavigationView

    // The drawer menu displayed when clicking the "head" icon
    private lateinit var drawerLayout: DrawerLayout

    private var qrScanResult: CompletableFuture<String>? = null

    private val userCached: UserModelCached by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the user id given by login or register to use it inside this activity and its child fragment
        setupUser()

        // Setup the components of the screen
        setupTopBar()
        setupProfileButton()
        setupDrawerNavigationView()
        setupBottomNavigationView()

        // Create an instance of your database
        val database: Database = FirebaseDatabase()

        // Create an instance of FriendsFragmentFactory and set it as the fragment factory
        val friendsFragmentFactory = FriendsFragmentFactory(database)
        supportFragmentManager.fragmentFactory = friendsFragmentFactory

        // Display the main fragment when no saved state
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.draw_menu_item
            replaceFragment<DrawMenuFragment>()
        }

        setupNotifications()
    }

    /**
     * Launches the QR scanner.
     * @return a future completed when the user scanned something.
     */
    fun scanQRCode(): CompletableFuture<String> {
        val result = CompletableFuture<String>()
        if (qrScanResult != null) {
            result.completeExceptionally(Exception("QR scan is still pending."))
        } else {
            qrScanResult = result

            // Asks for camera permission if we don't have it.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    SCAN_QR_REQ_CODE,
                )
            } else {
                launchFriendQRScanner(this, SCAN_QR_REQ_CODE)
            }
        }
        return result
    }

    private fun setupUser() {
        val userId = intent.getStringExtra(EXTRA_USER_ID)
        if (userId != null) {
            userCached.setCurrentUser(userId)
        } else {
            Toast.makeText(applicationContext, R.string.toast_test_error_message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setupTopBar() {
        val topAppBar: Toolbar = findViewById(R.id.topAppBar)
        // Make this toolbar behave like the main action bar
        setSupportActionBar(topAppBar)
    }

    private fun setupProfileButton() {
        val profileImageButton: ImageButton = findViewById(R.id.profile_button)
        userCached.getUser().observe(this) {
            profileImageButton.setImageBitmap(it.profilePhoto(resources))
        }
        drawerLayout = findViewById(R.id.drawerLayout)
        // Set a listener to open the drawer menu (we might want it on the right)
        profileImageButton.setOnClickListener {
            drawerLayout.open()
        }
    }

    private fun setupDrawerNavigationView() {
        val drawerNavigationView: NavigationView = findViewById(R.id.navigationView)
        val header = drawerNavigationView.getHeaderView(0)
        userCached.getUser().observe(this) {
            header.findViewById<TextView>(R.id.header_username).text = it.username
            header.findViewById<TextView>(R.id.header_email).text = it.emailAddress
        }
        // Handle the items in the drawer menu
        drawerNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Display profile fragment
                R.id.profile_menu_item -> replaceFragment<ProfileFragment>()

                // Display stats fragment
                R.id.stats_menu_item -> replaceFragment<StatsFragment>()

                // Display challenge fragment
                R.id.challenge_menu_item -> replaceFragment<ChallengeFragment>()
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
                R.id.draw_menu_item -> replaceFragment<DrawMenuFragment>()

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

    private fun setupNotifications() {
        val useMockReminder = intent.getBooleanExtra(USE_MOCK_CHALLENGE_REMINDER, false)
        NotificationsHelper(applicationContext).setupNotifications(useMockReminder)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCAN_QR_REQ_CODE && resultCode == SCANNER_ACTIVITY_RESULT_CODE) {
            val scannedData = data?.getStringExtra(SCANNER_ACTIVITY_RESULT_KEY)
            if (qrScanResult != null) {
                qrScanResult!!.complete(scannedData)
                qrScanResult = null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SCAN_QR_REQ_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                launchFriendQRScanner(this, SCAN_QR_REQ_CODE)
            } else if (qrScanResult != null) {
                qrScanResult!!.completeExceptionally(Exception("No camera access"))
                qrScanResult = null
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}
