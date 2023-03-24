package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.CommunityPostFragment
import com.epfl.drawyourpath.community.CommunityTournamentFragment

class CommunityFragment : Fragment(R.layout.fragment_community) {

    private lateinit var home: Button
    private lateinit var list: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home = view.findViewById(R.id.community_home_button)
        list = view.findViewById(R.id.community_list_button)

        var currentView = CommunityView.HOME
        updateButtonColor(view, currentView)
        replaceFragment<CommunityPostFragment>()

        home.setOnClickListener {
            if (currentView != CommunityView.HOME) {
                currentView = CommunityView.HOME
                updateButtonColor(view, currentView)
                replaceFragment<CommunityPostFragment>()
            }
        }

        list.setOnClickListener {
            if (currentView != CommunityView.LIST) {
                currentView = CommunityView.LIST
                updateButtonColor(view, currentView)
                replaceFragment<CommunityTournamentFragment>()
            }
        }

    }

    private inline fun <reified F : Fragment> replaceFragment() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragment_community_container_view)
        }
    }

    private enum class CommunityView {
        HOME, LIST
    }

    private fun updateButtonColor(view: View, current: CommunityView) {
        changeButtonColor(view, home, R.color.grey)
        changeButtonColor(view, list, R.color.grey)
        when (current) {
            CommunityView.HOME -> changeButtonColor(view, home, R.color.purple_200)
            CommunityView.LIST -> changeButtonColor(view, list, R.color.purple_200)
        }
    }

    private fun changeButtonColor(view: View, button: Button, color: Int) {
        val wrappedDrawable = DrawableCompat.wrap(button.background)
        DrawableCompat.setTint(wrappedDrawable, view.resources.getColor(color, null))
    }

}