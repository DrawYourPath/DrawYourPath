package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.*
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.util.*

/**
 * Fragment used to display different challenge about the user :
 * [DailyGoal] the DailyGoal the user set and aims daily
 * [Tournament] tournament the user takes/took part in
 * [Trophy] the trophies earned by the user
 */
class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    private val userCached: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goalView = view.findViewById<RecyclerView>(R.id.goals_view)
        val tournamentsView = view.findViewById<RecyclerView>(R.id.tournaments_view)
        val trophiesView = view.findViewById<RecyclerView>(R.id.trophies_view)

        val tempUser = TemporaryUser(LinkedList(mutableListOf()), listOf())
        tempUser.addTrophy(Trophy.MARATHON)
        tempUser.addTrophy(Trophy.THEFIRSTPATH)
        tempUser.addTrophy(Trophy.TENKM)

        goalView.layoutManager = LinearLayoutManager(context)
        userCached.getUser().observe(viewLifecycleOwner) {
            goalView.adapter = DailyGoalViewAdapter(DailyGoal(it.distanceGoal, it.activityTimeGoal, it.nbOfPathsGoal, 5.9, 15.2, 1))
        }

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = TournamentViewAdapter(tempUser.tournaments)

        trophiesView.layoutManager = GridLayoutManager(context, 3)
        trophiesView.adapter = TrophyViewAdapter(tempUser.getTrophies())
    }

}