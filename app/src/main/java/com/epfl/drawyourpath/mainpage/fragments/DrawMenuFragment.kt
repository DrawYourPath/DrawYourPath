package com.epfl.drawyourpath.mainpage.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.map.MapFragment
import com.epfl.drawyourpath.pathDrawing.PathDrawingActivity
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class DrawMenuFragment : Fragment() {
    private val userCached: UserModelCached by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draw_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = MapFragment()
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.mapMenuFragmentContent, mapFragment)
        fragTransaction.commit()

        //display the activity to draw a path when we click on start drawing button
        val startButton: Button = view.findViewById(R.id.button_start_drawing)
        startButton.setOnClickListener {
            val intent = Intent(this.context, PathDrawingActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_USER_ID, userCached.getUserId())
            startActivity(intent)
        }
    }
}