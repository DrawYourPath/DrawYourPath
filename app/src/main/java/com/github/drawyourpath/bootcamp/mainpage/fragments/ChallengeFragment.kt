package com.github.drawyourpath.bootcamp.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.challenge.DailyGoalViewAdapter
import com.github.drawyourpath.bootcamp.challenge.TemporaryInfo
import com.github.drawyourpath.bootcamp.challenge.TournamentViewAdapter
import com.github.drawyourpath.bootcamp.challenge.TrophyViewAdapter

class ChallengeFragment : Fragment(R.layout.fragment_challenge) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goalView = view.findViewById<RecyclerView>(R.id.goals_view)
        val tournamentsView = view.findViewById<RecyclerView>(R.id.tournaments_view)
        val trophiesView = view.findViewById<RecyclerView>(R.id.trophies_view)

        val temp = TemporaryInfo.SAMPLE_DATA

        goalView.layoutManager = LinearLayoutManager(context)
        goalView.adapter = DailyGoalViewAdapter(temp.dailyGoal)

        tournamentsView.layoutManager = LinearLayoutManager(context)
        tournamentsView.adapter = TournamentViewAdapter(temp.tournaments)

        trophiesView.layoutManager = GridLayoutManager(context, 3)
        trophiesView.adapter = TrophyViewAdapter(temp.trophies)
    }

}