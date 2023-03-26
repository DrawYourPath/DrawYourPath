package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.CommunityTournamentPostViewAdapter
import com.epfl.drawyourpath.community.TournamentModel
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.github.drawyourpath.bootcamp.path.Run
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime

/**
 * fragment used to display and vote for the tournament posts [TournamentPost]
 * TODO make it asynchronous when linked with the database
 */
class CommunityFragment : Fragment(R.layout.fragment_community) {

    private lateinit var drawer: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var detailsLayout: LinearLayout
    private lateinit var tournamentsView: RecyclerView

    private lateinit var tournament: TournamentModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawer = view.findViewById(R.id.fragment_community)
        menuButton = view.findViewById(R.id.community_menu_button)
        detailsLayout = view.findViewById(R.id.community_detail_layout)
        tournamentsView = view.findViewById(R.id.display_community_tournaments_view)

        tournament = TournamentModel(sampleWeekly(), sampleYourTournaments(), sampleDiscoveryTournaments())
        if (arguments?.getSerializable("tournaments") != null) {
            tournament = arguments?.getSerializable("tournaments") as TournamentModel
        }

        createNavigationMenu(view)

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = CommunityTournamentPostViewAdapter(getAllPostsFromAll(), true)

        menuButton.setOnClickListener {
            drawer.open()
        }

        //TODO add logic to the sort button
        //val sortButton = view.findViewById<ImageButton>(R.id.community_sort_button)

        val backButton = view.findViewById<ImageButton>(R.id.community_back_button)
        backButton.setOnClickListener {
            menuButton.visibility = View.VISIBLE
            detailsLayout.visibility = View.GONE
            tournamentsView.adapter = CommunityTournamentPostViewAdapter(getAllPostsFromAll(), true)
            tournamentsView.invalidate()
        }

    }

    /**
     * create the navigation menu where the different tournaments are displayed
     */
    private fun createNavigationMenu(view: View) {
        val menu = view.findViewById<NavigationView>(R.id.community_navigation_view).menu

        val weekly = menu.addSubMenu("Weekly tournament")
        weekly.add(tournament.getWeeklyTournament().name)
            .setContentDescription(tournament.getWeeklyTournament().name + " details")
            .setOnMenuItemClickListener {
                menuItemListener(view, sampleWeekly())
            }

        val your = menu.addSubMenu("Your tournament")
        for (i in tournament.getYourTournament("placeholder")) {
            your.add(i.name)
                .setContentDescription(i.name + " details")
                .setOnMenuItemClickListener {
                    menuItemListener(view, i)
                }
        }
        val discover = menu.addSubMenu("Discover")
        for (i in tournament.getDiscoverTournament("placeholder")) {
            discover.add(i.name)
                .setContentDescription(i.name + " details")
                .setOnMenuItemClickListener {
                    menuItemListener(view, i)
                }
        }
    }

    /**
     * the menu item click listener that will change the view to display the corresponding tournament
     */
    private fun menuItemListener(view: View, tournament: Tournament): Boolean {
        view.findViewById<TextView>(R.id.community_detail_name).text = tournament.name
        view.findViewById<TextView>(R.id.community_detail_description).text = tournament.description
        view.findViewById<TextView>(R.id.community_detail_date).text = tournament.getStartOrEndDate()
        tournamentsView.adapter = CommunityTournamentPostViewAdapter(getPostsFrom(tournament), false)
        tournamentsView.invalidate()
        menuButton.visibility = View.GONE
        detailsLayout.visibility = View.VISIBLE
        drawer.close()
        return true
    }

    /**
     * get all the posts from the given tournament
     *
     * @param tournament the tournament
     * @return the list of pair of tournament and posts
     */
    private fun getPostsFrom(tournament: Tournament): List<Pair<Tournament, TournamentPost>> {
        return tournament.posts.map { p -> Pair(tournament, p) }
    }


    /**
     * get all the posts from all tournaments
     *
     * @return the list of all pairs of tournament and posts
     */
    private fun getAllPostsFromAll(): List<Pair<Tournament, TournamentPost>> {
        return getAllTournaments().flatMap { tournament -> tournament.posts.map { p -> Pair(tournament, p) } }
    }

    /**
     * get all the tournaments from weekly tournament, your tournament and discover tournament
     *
     * @return the list of all tournaments
     */
    private fun getAllTournaments(): List<Tournament> {
        val list = mutableListOf(tournament.getWeeklyTournament())
        list.addAll(tournament.getYourTournament("placeholder"))
        list.addAll(tournament.getDiscoverTournament("placeholder"))
        return list
    }


    //TODO replace by real tournaments
    //everything from here are samples

    /**
     * sample tournaments
     */
    private fun sampleWeekly(): Tournament {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0)
        )

        return Tournament(
            "Weekly tournament: Star Path",
            "draw a star path",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            posts
        )
    }

    /**
     * sample tournaments
     */
    private fun sampleYourTournaments(): List<Tournament> {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0)
        )
        val posts1 = mutableListOf(
            TournamentPost("xD c moi", sampleRun(), 35),
            TournamentPost("Jaqueline", sampleRun(), 356),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("me", sampleRun(), -563),
            TournamentPost("IDK", sampleRun(), 0)
        )
        return mutableListOf(
            Tournament(
                "best tournament ever",
                "draw whatever you want",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts1
            ),
            Tournament(
                "time square",
                "draw a square",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts
            )
        )
    }

    /**
     * sample tournaments
     */
    private fun sampleDiscoveryTournaments(): List<Tournament> {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0)
        )
        val posts1 = mutableListOf(
            TournamentPost("SpaceMan", sampleRun(), 35),
            TournamentPost("NASA", sampleRun(), 124),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("Alien", sampleRun(), -3),
            TournamentPost("IDK", sampleRun(), 0)
        )
        return mutableListOf(
            Tournament(
                "Discover the earth",
                "draw the earth",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts1
            ),
            Tournament(
                "to the moon",
                "draw the moon",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts
            )
        )
    }

    /**
     * sample run
     */
    private fun sampleRun(): Run {
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(point1, point2)
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10
        return Run(path, startTime, endTime)
    }
}