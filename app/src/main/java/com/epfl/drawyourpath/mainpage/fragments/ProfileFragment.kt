package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImageIcon(R.id.IV_Trophy, R.drawable.award)
        setImageIcon(R.id.IV_Achievements, R.drawable.achievements)
        setImageIcon(R.id.IV_ProfilePicture, R.drawable.profile_placholderpng)

        view.findViewById<TextView>(R.id.TV_Trophy).setOnClickListener { onTrophyClicked() }

        // TODO: Pull data from user model.
        populateFriendList(listOf(
            "Miguel", "Jean Radiateur", "XxDenisXX", "CrazyRunnerDu18", "Alphonso9", "SCRUM Masseur"))
        setStreak(44)
        setTotalKilometer(120)
        setShapesDrawn(15)
        setAverageSpeed(7)
        setGoalsReached(33)
        setTrophyCount(459)
        setAchievementsCount(14)
    }

    private fun onTrophyClicked() {
        // TODO: Redirect to trophy activity.
    }

    private fun populateFriendList(friends: List<String>) {
        val friendsList = view?.findViewById<ListView>(R.id.LV_Friends)

        friendsList?.adapter = ArrayAdapter(requireActivity().applicationContext,
            android.R.layout.simple_list_item_1, friends)
    }

    private fun setStreak(streak: Int) {
        setTextViewText(R.id.TV_DaysStreak, String.format("%d Days", streak))
    }

    private fun setTotalKilometer(kilometers: Int) {
        setTextViewText(R.id.TV_TotalKilometers, String.format("%d KM", kilometers))
    }

    private fun setShapesDrawn(shapesDrawn: Int) {
        setTextViewText(R.id.TV_ShapesDrawn, String.format("%d Shapes", shapesDrawn))
    }

    private fun setAverageSpeed(avgSpeed: Int) {
        setTextViewText(R.id.TV_AvgSpeed, String.format("%d KM/H", avgSpeed))
    }

    private fun setAchievementsCount(achievementsCount: Int) {
        setTextViewText(R.id.TV_Achievements, achievementsCount.toString())
    }

    private fun setTrophyCount(trophyCount: Int) {
        setTextViewText(R.id.IV_Trophy, trophyCount.toString())
    }

    private fun setGoalsReached(goalsReached: Int) {
        setTextViewText(R.id.TV_GoalsReached, String.format("%d Goals", goalsReached))
    }

    private fun setTextViewText(id: Int, text: String) {
        view?.findViewById<TextView>(id)?.text = text
    }

    private fun setImageIcon(id: Int, resourceId: Int) {
        view?.findViewById<ImageView>(id)?.setImageResource(resourceId)
    }
}