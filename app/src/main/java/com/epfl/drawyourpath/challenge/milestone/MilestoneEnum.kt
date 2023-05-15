package com.epfl.drawyourpath.challenge.milestone

import com.epfl.drawyourpath.R

enum class MilestoneEnum(
    val description: String,
    val drawable: Int = R.drawable.achievements,
) {
    // TODO add more milestones
    THE_FIRST_KILOMETER("Run a total of 1 kilometer"),
    TEN_KILOMETERS("Run a total of 10 kilometers"),
    MARATHON("Run a marathon (42.195 kilometers)"),
    HUNDRED_KILOMETERS("Run a total of 100 kilometers"),
    THE_FIRST_HOUR("Run for a total of 1 hour"),
    TEN_HOUR("Run for a total of 10 hours"),
    THE_FIRST_DAY("Run for a day (24 hours)"),
    THE_FIRST_PATH("Draw your first path"),
    TEN_PATH("Draw ten paths"),
    WEEK_STREAK("Reach the daily goals 7 days in a row"),
    MONTH_STREAK("Reach the daily goals 1 month in a row"),
}
