package com.epfl.drawyourpath.challenge.milestone

import com.epfl.drawyourpath.R

enum class MilestoneEnum(
    val description: String,
    val drawable: Int = R.drawable.achievements,
) {
    // TODO add more milestones
    THE_FIRST_KILOMETER("run a total of 1 kilometer"),
    TEN_KILOMETERS("run a total of 10 kilometers"),
    MARATHON("run a marathon (42.195 kilometers)"),
    HUNDRED_KILOMETERS("run a total of 100 kilometers"),
    THE_FIRST_HOUR("run for a total of 1 hour"),
    TEN_HOUR("run for a total of 10 hours"),
    THE_FIRST_DAY("run for a day (24 hours)"),
    THE_FIRST_PATH("draw your first path"),
    TEN_PATH("draw ten paths"),
}
