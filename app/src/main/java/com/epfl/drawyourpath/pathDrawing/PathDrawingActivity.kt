package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

/**
 * This class is used to show the different fragments to draw a path(countdown, draw a path, see user performance, end view to return back to the menu)
 */
class PathDrawingActivity : AppCompatActivity() {
    // the user currently cached in the app
    private val userCached: UserModelCached by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // retrieve the userId from the main activity
        val userId = intent.getStringExtra(MainActivity.EXTRA_USER_ID)
        val countdownDuration = intent.getLongExtra(EXTRA_COUNTDOWN_DURATION, 4)
        if (userId != null) {
            userCached.setCurrentUser(userId)
        } else {
            Toast.makeText(applicationContext, R.string.toast_test_error_message, Toast.LENGTH_LONG)
                .show()
        }
        setContentView(R.layout.fragment_path_drawing)
        // lunch and display the countdown fragment
        val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.path_drawing_activity_content, PathDrawingCountDownFragment(countdownDuration = countdownDuration)).commit()
    }
    companion object {
        const val EXTRA_COUNTDOWN_DURATION = "countdown_duration"
    }
}
