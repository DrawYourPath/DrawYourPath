package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
        val trophiesAdapter = TrophyViewAdapter()
        user.getTrophies().observe(viewLifecycleOwner) {
            trophiesAdapter.update(it)
        }
        trophiesView.layoutManager = LinearLayoutManager(context)
        trophiesView.adapter = trophiesAdapter
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
