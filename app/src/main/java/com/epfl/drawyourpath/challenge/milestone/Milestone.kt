package com.epfl.drawyourpath.challenge.milestone

import com.epfl.drawyourpath.utils.Utils
import java.time.LocalDate

data class Milestone(
    val name: String,
    val description: String,
    val date: LocalDate,
    val drawable: Int,
    val dateAsString: String = Utils.getDateAsString(date),
) {

    constructor(enum: MilestoneEnum, date: LocalDate) : this (
        Utils.getStringFromALL_CAPS(enum.name),
        enum.description,
        date,
        enum.drawable,
    )

    companion object {
        val sample = MilestoneEnum.values().toList().map { Milestone(it, LocalDate.ofYearDay(2023, 105)) }
    }
}
