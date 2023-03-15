package com.github.drawyourpath.bootcamp.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.challenge.*
import java.util.*

class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goalView = view.findViewById<RecyclerView>(R.id.goals_view)
        val tournamentsView = view.findViewById<RecyclerView>(R.id.tournaments_view)
        val trophiesView = view.findViewById<RecyclerView>(R.id.trophies_view)

        var tempUser = TemporaryUser(LinkedList(mutableListOf(DailyGoal(5.0, 15.0, 1))), listOf())
        tempUser.addTrophy(Trophy.MARATHON)
        tempUser.addTrophy(Trophy.THEFIRSTPATH)
        tempUser.addTrophy(Trophy.TENKM)

        if (arguments?.getSerializable("user") != null) {
            tempUser = arguments?.getSerializable("user") as TemporaryUser
        }

        goalView.layoutManager = LinearLayoutManager(context)
        goalView.adapter = DailyGoalViewAdapter(tempUser.getTodayDailyGoal())

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = TournamentViewAdapter(tempUser.tournaments)

        trophiesView.layoutManager = GridLayoutManager(context, 3)
        trophiesView.adapter = TrophyViewAdapter(tempUser.getTrophies())
    }

}