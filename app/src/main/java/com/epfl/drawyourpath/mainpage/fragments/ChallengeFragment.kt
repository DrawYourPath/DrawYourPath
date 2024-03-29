package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoalViewAdapter
import com.epfl.drawyourpath.challenge.milestone.Milestone
import com.epfl.drawyourpath.challenge.milestone.MilestoneViewAdapter
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.challenge.trophy.TrophyViewAdapter
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.time.LocalDateTime

/**
 * Fragment used to display different challenge about the user :
 * [DailyGoal] the DailyGoal the user set and aims daily
 * [Trophy] tournament the user takes/took part in
 * [Milestone] the trophies earned by the user
 */
class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    private val user: UserModelCached by activityViewModels()

    // live data of list of trophies
    private val trophiesList = MutableLiveData(listOf<Trophy>())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDailyGoalView(view.findViewById(R.id.goals_view))

        setTrophiesView(view.findViewById(R.id.trophies_view))

        setMilestonesView(view.findViewById(R.id.milestones_view))
    }

    /**
     * set the recycler view of the DailyGoal
     * @param goalView the RecyclerView to set
     */
    private fun setDailyGoalView(goalView: RecyclerView) {
        val dailyGoalAdapter =
            DailyGoalViewAdapter(
                { user.updateGoals(UserGoals(distance = it)) },
                { user.updateGoals(UserGoals(activityTime = it)) },
                { user.updateGoals(UserGoals(paths = it.toLong())) },
            )
        user.getTodayDailyGoal().observe(viewLifecycleOwner) {
            dailyGoalAdapter.updateDailyGoal(it)
        }
        goalView.layoutManager = LinearLayoutManager(context)
        goalView.adapter = dailyGoalAdapter
    }

    /**
     * set the recycler view of the trophies
     * @param trophiesView the RecyclerView to set
     */
    private fun setTrophiesView(trophiesView: RecyclerView) {
        getTrophiesList()
        val trophiesAdapter = TrophyViewAdapter()
        trophiesList.observe(viewLifecycleOwner) {
            trophiesAdapter.update(it)
        }
        trophiesView.layoutManager = LinearLayoutManager(context)
        trophiesView.adapter = trophiesAdapter
    }

    /**
     * Helper function to get the actual list of trophies from the database and control if it is complete.
     */
    private fun getTrophiesList() {
        val database = user.getDatabase()
        // get the list of tournaments where the user is present
        database.getUserData(user.getUserId()!!).thenApplyAsync { userData ->
            val listTournamentId = userData.tournaments ?: emptyList()
            // get the list of trophies of the user
            val actualListTrophies = userData.trophies ?: emptyList()
            trophiesList.postValue(actualListTrophies)
            // obtain the list of tournament associated to a trophy
            val listTournamentWithTrophy: List<String> = actualListTrophies.map { trophy ->
                trophy.tournamentId
            }
            // check that the list of trophy is potentially complete(all the tournaments where the user is present is associated to a trophy)
            if (!listTournamentWithTrophy.containsAll(listTournamentId)) {
                // if it is not the case then complete it by checking all the tournaments not associated to a trophy.
                val remainingTournament = listTournamentId.filter { !listTournamentWithTrophy.contains(it) }
                checkTournaments(listOfTournamentsId = remainingTournament, userId = user.getUserId()!!)
            }
        }
    }

    /**
     *  Check that the given list of tournament is not associated to a trophy win by the uer with the given userId
     *  and add them if it is the case to the database and to the view
     *  @param listOfTournamentsId list of tournaments id to check
     *  @param userId associated to a trophy
     *  @return the list of trophy win by the user in this tournaments
     */
    private fun checkTournaments(listOfTournamentsId: List<String>, userId: String) {
        listOfTournamentsId.forEach { tournamentId ->
            user.getDatabase().getTournament(tournamentId).observe(viewLifecycleOwner) { tournament ->
                if (tournament.endDate < LocalDateTime.now()) {
                    val ranking = tournament.posts.sortedByDescending { it.getVotes() }.take(3)
                    ranking.forEachIndexed { index, elem ->
                        if (elem.userId == userId) {
                            val trophy = Trophy(
                                tournamentId = tournamentId,
                                tournamentName = tournament.name,
                                tournamentDescription = tournament.description,
                                date = tournament.endDate.toLocalDate(),
                                ranking = index + 1,
                            )
                            trophiesList.postValue(
                                trophiesList.value?.plus(trophy) ?: listOf(trophy),
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * set the recycler view of the milestones
     * @param milestoneView the RecyclerView to set
     */
    private fun setMilestonesView(milestoneView: RecyclerView) {
        val milestoneAdapter = MilestoneViewAdapter()
        user.getMilestones().observe(viewLifecycleOwner) {
            milestoneAdapter.update(it)
        }
        milestoneView.layoutManager = GridLayoutManager(context, 2)
        milestoneView.adapter = milestoneAdapter
    }
}
