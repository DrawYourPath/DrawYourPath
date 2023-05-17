package com.epfl.drawyourpath.challenge.milestone

import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.utils.Utils
import java.time.LocalDate

data class Milestone(
    val name: String,
    val description: String,
    val date: LocalDate,
    val drawable: Int,
) {

    constructor(enum: MilestoneEnum, date: LocalDate) : this (
        Utils.getStringFromALL_CAPS(enum.name),
        enum.description,
        date,
        enum.drawable,
    )

    constructor(entity: MilestoneEntity) : this (
        MilestoneEnum.valueOf(Utils.getALL_CAPSFromString(entity.milestone)),
        LocalDate.ofEpochDay(entity.date),
    )

    companion object {
        /**
         * get all the reached milestones with today date
         * @param dailyGoals the list of daily goals
         * @return the list of reached milestones
         */
        fun getAllReachedMilestones(dailyGoals: List<DailyGoal>): List<Milestone> {
            return MilestoneEnum.values().filter { it.isMilestoneReached(dailyGoals) }.map { Milestone(it, LocalDate.now()) }
        }
    }

}
