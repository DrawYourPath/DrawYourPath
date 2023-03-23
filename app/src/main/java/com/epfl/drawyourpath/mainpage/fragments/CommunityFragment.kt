package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.CommunityPostFragment
import com.epfl.drawyourpath.community.CommunityTournamentFragment

class CommunityFragment : Fragment(R.layout.fragment_community) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val home = view.findViewById<Button>(R.id.community_home_button)
        val list = view.findViewById<Button>(R.id.community_list_button)

        home.setOnClickListener {
            replaceFragment<CommunityPostFragment>()
        }

        list.setOnClickListener {
            replaceFragment<CommunityTournamentFragment>()
        }

        replaceFragment<CommunityPostFragment>()

    }

    private inline fun <reified F : Fragment> replaceFragment() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragment_community_container_view)
        }
    }

}