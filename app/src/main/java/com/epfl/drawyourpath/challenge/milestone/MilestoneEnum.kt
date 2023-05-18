package com.epfl.drawyourpath.challenge.milestone

import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.Statistics
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal

enum class MilestoneEnum(
    val description: String,
    val isMilestoneReached: (dailyGoals: List<DailyGoal>) -> Boolean,
    val drawable: Int = R.drawable.achievements,
) {
    // TODO add more milestones
    THE_FIRST_KILOMETER("Run a total of 1 kilometer", { Statistics.getTotalDistance(it) >= 1.0 }),
    TEN_KILOMETERS("Run a total of 10 kilometers", { Statistics.getTotalDistance(it) >= 10.0 }),
    MARATHON("Run a marathon (42.195 kilometers)", { Statistics.getTotalDistance(it) >= 42.195 }),
    HUNDRED_KILOMETERS("Run a total of 100 kilometers", { Statistics.getTotalDistance(it) >= 100.0 }),
    THE_FIRST_HOUR("Run for a total of 1 hour", { Statistics.getTotalTime(it) >= 60.0 }),
    TEN_HOURS("Run for a total of 10 hours", { Statistics.getTotalTime(it) >= 600.0 }),
    THE_FIRST_DAY("Run for a day (24 hours)", { Statistics.getTotalTime(it) >= 24.0 * 60.0 }),
    THE_FIRST_PATH("Draw your first path", { Statistics.getShapeDrawnCount(it) >= 1 }),
    TEN_PATHS("Draw ten paths", { Statistics.getShapeDrawnCount(it) >= 10 }),
    WEEK_STREAK("Reach the daily goals 7 days in a row", { Statistics.getReachedGoalsCount(it) >= 7 }),
    MONTH_STREAK("Reach the daily goals 1 month in a row", { Statistics.getReachedGoalsCount(it) >= 30 }),
}
