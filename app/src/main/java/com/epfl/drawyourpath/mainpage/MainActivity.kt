package com.epfl.drawyourpath.mainpage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.*
import com.epfl.drawyourpath.notifications.NotificationsHelper
import com.epfl.drawyourpath.pathDrawing.PathDrawingContainerFragment
import com.epfl.drawyourpath.qrcode.SCANNER_ACTIVITY_RESULT_CODE
import com.epfl.drawyourpath.qrcode.SCANNER_ACTIVITY_RESULT_KEY
import com.epfl.drawyourpath.qrcode.launchFriendQRScanner
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.util.concurrent.CompletableFuture

const val USE_MOCK_CHALLENGE_REMINDER = "useMockChallengeReminder"
const val SCAN_QR_REQ_CODE = 8233

/**
 * Main activity of the application, should be launched after the login activity.
 */
class MainActivity : AppCompatActivity() {

    private var qrScanResult: CompletableFuture<String>? = null

    private val userCached: UserModelCached by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the user id given by login or register to use it inside this activity and its child fragment
        setupUser()

        setupNotifications()

        setupFragment()
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

    private inline fun <reified F : Fragment> replaceFragment(args: Bundle? = null) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container_view, F::class.java, args)
        }
    }

    /**
     * setup the Main fragment or the path drawing fragment if the intent for it was sent
     */
    private fun setupFragment() {
        val runTest = intent.getBooleanExtra(EXTRA_DRAW_TEST, false)
        if (runTest) {
            replaceFragment<PathDrawingContainerFragment>(Bundle().also {
                it.putLong(PathDrawingContainerFragment.EXTRA_COUNTDOWN_DURATION, 0L)
            })
        } else {
            replaceFragment<MainFragment>()
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
        // value to pass in intent to set the current user
        const val EXTRA_USER_ID = "extra_user_id"

        // value pass in intent to set the path drawing fragment (used in test)
        const val EXTRA_DRAW_TEST = "extra_draw_test"
    }
}
