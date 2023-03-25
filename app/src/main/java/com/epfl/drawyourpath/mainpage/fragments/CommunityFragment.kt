package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.Tournament
import com.epfl.drawyourpath.community.CommunityTournamentPostViewAdapter
import com.epfl.drawyourpath.community.TournamentPost
import com.github.drawyourpath.bootcamp.path.Path
import com.github.drawyourpath.bootcamp.path.Run
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime

class CommunityFragment : Fragment(R.layout.fragment_community) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = view.findViewById<ImageButton>(R.id.community_menu_button)
        val drawer = view.findViewById<DrawerLayout>(R.id.fragment_community)
        createNavigationMenu(view, drawer)

        val tournamentsView = view.findViewById<RecyclerView>(R.id.display_community_tournaments_view)

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = CommunityTournamentPostViewAdapter(sampleData())

        menu.setOnClickListener {
            drawer.open()
        }

    }

    private fun createNavigationMenu(view: View, drawer: DrawerLayout) {
        val menu = view.findViewById<NavigationView>(R.id.community_navigation_view).menu

        val weekly = menu.addSubMenu("weekly tournament")
        weekly.add("the weekly tournament")
            .setOnMenuItemClickListener {
                drawer.close()
                true
            }.isCheckable = true

        val my = menu.addSubMenu("my tournament")
        for (i in 0..10) {
            my.add("test $i")
        }
        val discover = menu.addSubMenu("discover")
        for (i in 0..20) {
            discover.add("discover tournaments $i")
        }
    }

    private fun sampleData(): List<Pair<Tournament, TournamentPost>> {

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