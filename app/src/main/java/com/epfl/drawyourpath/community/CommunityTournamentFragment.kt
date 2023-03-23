package com.epfl.drawyourpath.community

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.Tournament
import com.epfl.drawyourpath.challenge.TournamentViewAdapter
import java.time.LocalDateTime

class CommunityTournamentFragment : Fragment(R.layout.fragment_community_tournament) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekly = view.findViewById<RecyclerView>(R.id.display_weekly_tournaments_view)
        val your = view.findViewById<RecyclerView>(R.id.display_your_tournaments_view)
        val discover = view.findViewById<RecyclerView>(R.id.display_discover_tournaments_view)

        weekly.layoutManager = LinearLayoutManager(context)
        weekly.adapter = TournamentViewAdapter(sampleWeekly())

        your.layoutManager = LinearLayoutManager(context)
        your.adapter = TournamentViewAdapter(mutableListOf())

        discover.layoutManager = LinearLayoutManager(context)
        discover.adapter = TournamentViewAdapter(sampleDiscover())

    }

    private fun sampleWeekly(): List<Tournament> {
        return mutableListOf(
            Tournament(
                "weekly star", "draw a star path", LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(1L)
            )
        )
    }

    private fun sampleDiscover(): List<Tournament> {
        return mutableListOf(
            Tournament(
                "test",
                "test",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L)
            ),
            Tournament(
                "2nd test",
                "test",
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(1L)
            ),
            Tournament(
                "test3",
                "test3",
                LocalDateTime.now().minusDays(3L),
                LocalDateTime.now().minusDays(2L)
            ),
            Tournament(
                "4th test",
                "test",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3L)
            )
        )

    }


}