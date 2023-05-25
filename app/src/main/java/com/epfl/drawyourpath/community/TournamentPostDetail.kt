package com.epfl.drawyourpath.community

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.path.RunInfoStatsFragment

class TournamentPostDetail(private val run: Run) : Fragment(R.layout.fragment_tournament_post_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.post_detail_back_button).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        val fragTransaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.post_detail_fragment_container, RunInfoStatsFragment(run)).commit()
    }

}