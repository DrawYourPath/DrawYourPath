package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.Tournament
import com.epfl.drawyourpath.community.CommunityTournamentsViewAdapter
import com.epfl.drawyourpath.community.TournamentPost
import com.github.drawyourpath.bootcamp.path.Path
import com.github.drawyourpath.bootcamp.path.Run
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

class CommunityFragment : Fragment(R.layout.fragment_community) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tournamentsView = view.findViewById<RecyclerView>(R.id.display_community_tournaments_view)

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = CommunityTournamentsViewAdapter(sampleDate())

    }

    private fun sampleDate(): List<Pair<Tournament, TournamentPost>> {

        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(point1, point2)
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10
        val run1 = Run(path, startTime, endTime)

        val tournament1 = Tournament("test", "test", LocalDateTime.now().plusDays(3L), LocalDateTime.now().plusDays(4L))


        return mutableListOf(
            Pair(tournament1, TournamentPost("user", run1, 512)),
            Pair(tournament1, TournamentPost("Michelle", run1, 0)),
            Pair(tournament1, TournamentPost("Hello", run1, -10)),
            Pair(tournament1, TournamentPost("ME", run1, 36541)),
            Pair(tournament1, TournamentPost("not me", run1, 1000)),
        )
    }

}