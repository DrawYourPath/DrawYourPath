package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.*
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.*

/**
 * Fragment used to display different challenge about the user :
 * [DailyGoal] the DailyGoal the user set and aims daily
 * [Tournament] tournament the user takes/took part in
 * [Trophy] the trophies earned by the user
 */
class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    private val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goalView = view.findViewById<RecyclerView>(R.id.goals_view)
        val tournamentsView = view.findViewById<RecyclerView>(R.id.tournaments_view)
        val trophiesView = view.findViewById<RecyclerView>(R.id.trophies_view)

        var tempUser = TemporaryUser(LinkedList(mutableListOf()), listOf())
        tempUser.addTrophy(Trophy.MARATHON)
        tempUser.addTrophy(Trophy.THEFIRSTPATH)
        tempUser.addTrophy(Trophy.TENKM)

        if (arguments?.getSerializable("user") != null) {
            //getSerializable(key: String?) deprecated but alternative requires API level 33
            tempUser = arguments?.getSerializable("user") as TemporaryUser
        }

        val dailyGoalAdapter =
            DailyGoalViewAdapter({ user.updateDistanceGoal(it) }, { user.updateActivityTimeGoal(it) }, { user.updateNumberOfPathsGoal(it) })
        user.getTodayDailyGoal().observe(viewLifecycleOwner) {
            dailyGoalAdapter.updateDailyGoal(it)
        }
        goalView.layoutManager = LinearLayoutManager(context)
        goalView.adapter = dailyGoalAdapter

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = TournamentViewAdapter(tempUser.tournaments)

        trophiesView.layoutManager = GridLayoutManager(context, 3)
        trophiesView.adapter = TrophyViewAdapter(tempUser.getTrophies())
    }

}