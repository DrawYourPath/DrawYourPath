package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.DrawFragment
import com.epfl.drawyourpath.userProfileCreation.PersonalInfoFragment


class PathDrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_drawing_actvity)
        //lunch the countdown fragment
        val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.contentFragmentPathDrawing, PathDrawingCountDownFragment())
        fragTransaction.commit()
    }
}