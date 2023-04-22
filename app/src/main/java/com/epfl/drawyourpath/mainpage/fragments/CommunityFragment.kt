package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.Menu
import android.view.SubMenu
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.*
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime

/**
 * fragment used to display and vote for the tournament posts [TournamentPost]
 * TODO make it asynchronous when linked with the database
 */
class CommunityFragment : Fragment(R.layout.fragment_community) {

    private lateinit var drawer: DrawerLayout
    private lateinit var headlineHome: LinearLayout
    private lateinit var headlineDetailsLayout: LinearLayout
    private lateinit var detailsLayout: LinearLayout
    private lateinit var tournamentPostsView: RecyclerView
    private lateinit var scroll: NestedScrollView
    private val tournament = TournamentModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariable(view)

        getTournaments()

        createTournamentPostsView()

        createMenuButton(view)

        createNavigationMenu(view)

        createSortButton(view)

        createBackButton(view)
    }

    /**
     * initialize the different variable
     */
    private fun initVariable(view: View) {
        drawer = view.findViewById(R.id.fragment_community)
        headlineHome = view.findViewById(R.id.community_headline_home)
        headlineDetailsLayout = view.findViewById(R.id.community_headline_detail_layout)
        detailsLayout = view.findViewById(R.id.community_detail_layout)
        tournamentPostsView = view.findViewById(R.id.display_community_tournaments_view)
        scroll = view.findViewById(R.id.community_nested_scroll_view)

        tournament.setSample(TournamentModel.SampleTournamentModel(sampleWeekly(), sampleYourTournaments(), sampleDiscoveryTournaments()))
    }

    /**
     * get the tournaments to display
     * TODO change from sample to real tournaments
     */
    private fun getTournaments() {
        if (arguments?.getSerializable("tournaments") != null) {
            tournament.setSample(arguments?.getSerializable("tournaments") as TournamentModel.SampleTournamentModel)
        }
    }

    /**
     * create the tournamentPost view that displays the posts
     */
    private fun createTournamentPostsView() {
        tournamentPostsView.layoutManager = LinearLayoutManager(context)
        tournamentPostsView.adapter = CommunityTournamentPostViewAdapter(getAllPostsFromAll(), true)
    }

    /**
     * create the sort button which will sort the posts
     * TODO create the sort button
     */
    private fun createSortButton(view: View) {
        val sortButton = view.findViewById<ImageButton>(R.id.community_sort_button)
    }

    /**
     * create the menu button which will open the navigation menu
     */
    private fun createMenuButton(view: View) {
        val menuButton = view.findViewById<ImageButton>(R.id.community_menu_button)
        menuButton.setOnClickListener {
            drawer.open()
        }
    }

    /**
     * create the back button to go back from the detail tournament view
     */
    private fun createBackButton(view: View) {
        val backButton = view.findViewById<ImageButton>(R.id.community_back_button)
        backButton.setOnClickListener {
            headlineHome.visibility = View.VISIBLE
            headlineDetailsLayout.visibility = View.GONE
            detailsLayout.visibility = View.GONE
            tournamentPostsView.adapter = CommunityTournamentPostViewAdapter(getAllPostsFromAll(), true)
            tournamentPostsView.invalidate()
            scroll.scrollTo(0, 0)
        }
    }

    /**
     * create the navigation menu where the different tournaments are displayed
     */
    private fun createNavigationMenu(view: View) {
        val menu = view.findViewById<NavigationView>(R.id.community_navigation_view).menu
        createTournamentButton(menu)

        val weekly = menu.addSubMenu("Weekly tournament")
        if (tournament.getWeeklyTournament() != null) {
            createMenuItem(view, weekly, tournament.getWeeklyTournament()!!)
        }

        val your = menu.addSubMenu("Your tournament")
        for (t in tournament.getYourTournament("placeholder")) {
            createMenuItem(view, your, t)
        }
        val discover = menu.addSubMenu("Discover")
        for (t in tournament.getDiscoverTournament("placeholder")) {
            createMenuItem(view, discover, t)
        }
    }

    private fun createTournamentButton(menu: Menu) {
        menu.add(getString(R.string.create_new_tournament))
            .setIcon(R.drawable.ic_add)
            .setOnMenuItemClickListener {
                replaceFragment<TournamentCreationFragment>()
                true
            }
    }

    /**
     * create the tournament item of a subMenu
     */
    private fun createMenuItem(view: View, menu: SubMenu, tournament: Tournament) {
        menu.add(tournament.name)
            .setContentDescription("${tournament.name} details")
            .setOnMenuItemClickListener {
                menuItemListener(view, tournament)
            }
    }

    /**
     * the menu item click listener that will change the view to display the corresponding tournament
     */
    private fun menuItemListener(view: View, tournament: Tournament): Boolean {
        view.findViewById<TextView>(R.id.community_detail_name).text = tournament.name
        view.findViewById<TextView>(R.id.community_detail_description).text = tournament.description
        view.findViewById<TextView>(R.id.community_detail_date).text = tournament.getStartOrEndDate()
        tournamentPostsView.adapter = CommunityTournamentPostViewAdapter(getPostsFrom(tournament), false)
        tournamentPostsView.invalidate()
        scroll.scrollTo(0, 0)
        headlineHome.visibility = View.GONE
        headlineDetailsLayout.visibility = View.VISIBLE
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
        val list = mutableListOf<Tournament>()
        if (tournament.getWeeklyTournament() != null) {
            list.add(tournament.getWeeklyTournament()!!)
        }
        list.addAll(tournament.getYourTournament("placeholder"))
        list.addAll(tournament.getDiscoverTournament("placeholder"))
        return list
    }

    /**
     * replace this fragment by another one
     */
    private inline fun <reified F : Fragment> replaceFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragmentContainerView)
        }
    }

    // TODO replace by real tournaments
    // everything from here are samples

    /**
     * sample tournaments
     */
    private fun sampleWeekly(): Tournament {
        val posts = mutableListOf(
            TournamentPost("xxDarkxx", sampleRun(), -13),
            TournamentPost("Michel", sampleRun(), 158),
            TournamentPost("MrPrefect", sampleRun(), 666),
            TournamentPost("Me Myself and I", sampleRun(), 123456),
            TournamentPost("Invalid Username", sampleRun(), 0),
        )

        return Tournament(
            "Weekly tournament: Star Path",
            "draw a star path",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            posts,
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
            TournamentPost("Invalid Username", sampleRun(), 0),
        )
        val posts1 = mutableListOf(
            TournamentPost("xD c moi", sampleRun(), 35),
            TournamentPost("Jaqueline", sampleRun(), 356),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("me", sampleRun(), -563),
            TournamentPost("IDK", sampleRun(), 0),
        )
        return mutableListOf(
            Tournament(
                "best tournament ever",
                "draw whatever you want",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts1,
            ),
            Tournament(
                "time square",
                "draw a square",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts,
            ),
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
            TournamentPost("Invalid Username", sampleRun(), 0),
        )
        val posts1 = mutableListOf(
            TournamentPost("SpaceMan", sampleRun(), 35),
            TournamentPost("NASA", sampleRun(), 124),
            TournamentPost("Diabolos", sampleRun(), 666),
            TournamentPost("Alien", sampleRun(), -3),
            TournamentPost("IDK", sampleRun(), 0),
        )
        return mutableListOf(
            Tournament(
                "Discover the earth",
                "draw the earth",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts1,
            ),
            Tournament(
                "to the moon",
                "draw the moon",
                LocalDateTime.now().plusDays(3L),
                LocalDateTime.now().plusDays(4L),
                posts,
            ),
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
