package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.Menu
import android.view.SubMenu
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.*
import com.epfl.drawyourpath.database.FirebaseDatabase
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

/**
 * fragment used to display and vote for the tournament posts [TournamentPost]
 */
class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val tournamentModel: TournamentModel by activityViewModels()

    private lateinit var postViewAdapter: CommunityTournamentPostViewAdapter
    private lateinit var drawer: DrawerLayout
    private lateinit var headlineHome: LinearLayout
    private lateinit var headlineDetailsLayout: LinearLayout
    private lateinit var detailsLayout: LinearLayout
    private lateinit var tournamentPostsView: RecyclerView
    private lateinit var scroll: NestedScrollView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariable(view)

        createTournamentPostsView()

        createMenuButton(view)

        createNavigationMenu(view)

        createSortButton(view)

        createBackButton(view)

        createAddPostButton(view)
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
        postViewAdapter = CommunityTournamentPostViewAdapter { vote, postId, tournamentId -> tournamentModel.addVote(vote, postId, tournamentId) }
    }

    /**
     * create the tournamentPost view that displays the posts
     */
    private fun createTournamentPostsView() {
        tournamentPostsView.layoutManager = LinearLayoutManager(context)
        tournamentPostsView.adapter = postViewAdapter
        tournamentModel.posts.observe(viewLifecycleOwner) { posts ->
            postViewAdapter.update(posts, headlineHome.isVisible, tournamentModel.getCurrentUser())
        }
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
            tournamentModel.showPostOf(null)
            scroll.scrollTo(0, 0)
        }
    }

    /**
     * create the add post button to go to the tournament post creation fragment
     * @param view the view
     */
    private fun createAddPostButton(view: View) {
        val addButton = view.findViewById<FloatingActionButton>(R.id.community_add_post_button)
        addButton.setOnClickListener {
            replaceFragment<TournamentPostCreationFragment>()
        }
    }

    /**
     * create the navigation menu where the different tournaments are displayed
     */
    private fun createNavigationMenu(view: View) {
        val menu = view.findViewById<NavigationView>(R.id.community_navigation_view).menu
        createTournamentButton(menu)

        val your = menu.addSubMenu("Your tournament")
        tournamentModel.yourTournament.observe(viewLifecycleOwner) { tournaments ->
            your.clear()
            tournaments.forEach { createMenuItem(view, your, it, true) }
        }

        val soon = menu.addSubMenu("Starting soon")
        tournamentModel.startingSoonTournament.observe(viewLifecycleOwner) { tournaments ->
            soon.clear()
            tournaments.forEach { createMenuItem(view, soon, it.first, it.second) }
        }
        val discover = menu.addSubMenu("Discover")
        tournamentModel.discoverTournament.observe(viewLifecycleOwner) { tournaments ->
            discover.clear()
            tournaments.forEach { createMenuItem(view, discover, it, false) }
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
    private fun createMenuItem(view: View, menu: SubMenu, tournament: Tournament, registered: Boolean) {
        menu.add(tournament.name)
            .setContentDescription("${tournament.name} details")
            .setActionView(R.layout.item_tournament)
            .setOnMenuItemClickListener {
                menuItemListener(view, tournament, registered)
            }
            .actionView!!.findViewById<ToggleButton>(R.id.item_tournament_toggle).also {
                it.isChecked = registered
            }.setOnCheckedChangeListener { _, isChecked ->
                tournamentModel.register(tournament.id, isChecked)
            }
    }

    /**
     * the menu item click listener that will change the view to display the corresponding tournament
     */
    private fun menuItemListener(view: View, tournament: Tournament, registered: Boolean): Boolean {
        view.findViewById<TextView>(R.id.community_detail_name).text = tournament.name
        view.findViewById<TextView>(R.id.community_detail_description).text = tournament.description
        view.findViewById<TextView>(R.id.community_detail_date).text =
            tournament.getStartOrEndDate()
        view.findViewById<ToggleButton>(R.id.community_detail_toggle).also {
            it.isChecked = registered
        }.setOnCheckedChangeListener { _, isChecked ->
            tournamentModel.register(tournament.id, isChecked)
        }
        tournamentModel.showPostOf(tournament.id)
        scroll.scrollTo(0, 0)
        headlineHome.visibility = View.GONE
        headlineDetailsLayout.visibility = View.VISIBLE
        detailsLayout.visibility = View.VISIBLE
        drawer.close()
        return true
    }

    /**
     * replace this fragment by another one
     */
    private inline fun <reified F : Fragment> replaceFragment() {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<F>(R.id.fragmentContainerView)
            addToBackStack("creation")
        }
    }
}
