package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoalViewAdapter
import com.epfl.drawyourpath.challenge.milestone.Milestone
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

/**
 * Fragment used to display different challenge about the user :
 * [DailyGoal] the DailyGoal the user set and aims daily
 * [Trophy] tournament the user takes/took part in
 * [Milestone] the trophies earned by the user
 */
class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    private val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val tournamentsView = view.findViewById<RecyclerView>(R.id.tournaments_view)
        val trophiesView = view.findViewById<RecyclerView>(R.id.trophies_view)*/

        checkTest()

        setDailyGoalView(view.findViewById(R.id.goals_view))

        // TODO: change this when in userModel.
        /*tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = TournamentViewAdapter(tempUser.tournaments)

        trophiesView.layoutManager = GridLayoutManager(context, 3)
        trophiesView.adapter = TrophyViewAdapter(tempUser.getTrophies())*/
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
     * check if there is a test. If so then add a today daily goal
     */
    private fun checkTest() {
        if (arguments?.getBoolean(TEST_EXTRA) == true) {
            val mock = MockDatabase()
            mock.addDailyGoal(MockAuth.MOCK_USER.getUid(), DailyGoal.TEST_SAMPLE).thenApply {
                user.setDatabase(mock)
            }
        }
    }

    companion object {
        const val TEST_EXTRA = "test_challenge_fragment"
    }
}
