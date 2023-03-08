package com.github.drawyourpath.bootcamp.challengeActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drawyourpath.bootcamp.R

class ChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)

        var goalView = findViewById<RecyclerView>(R.id.goals_view)
        var tournamentsView = findViewById<RecyclerView>(R.id.tournaments_view)
        var trophiesView = findViewById<RecyclerView>(R.id.trophies_view)

        var temp = TemporaryInfo.SAMPLE_DATA

        goalView.layoutManager = LinearLayoutManager(this)
        goalView.adapter = GoalViewAdapter(temp.goal, Goal(2.0, 25.0, 1))

        tournamentsView.layoutManager = LinearLayoutManager(this)
        tournamentsView.adapter = TournamentViewAdapter(temp.tournaments)

        trophiesView.layoutManager = GridLayoutManager(this, 3)
        trophiesView.adapter = TrophyViewAdapter(temp.trophies)

    }
}