package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class PathDrawingActivity : AppCompatActivity() {
    private val userCached: UserModelCached by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // retrieve the userId from the main activity
        val userId = intent.getStringExtra(MainActivity.EXTRA_USER_ID)
        if (userId != null) {
            userCached.setCurrentUser(userId)
        } else {
            Toast.makeText(applicationContext, R.string.toast_test_error_message, Toast.LENGTH_LONG)
                .show()
        }
        setContentView(R.layout.activity_path_drawing_actvity)
        // lunch the countdown fragment
        val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.contentFragmentPathDrawing, PathDrawingCountDownFragment()).commit()
    }
}
