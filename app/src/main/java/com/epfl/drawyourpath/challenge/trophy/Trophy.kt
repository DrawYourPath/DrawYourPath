package com.epfl.drawyourpath.challenge.trophy

import java.time.LocalDate

data class Trophy(
    val tournamentName: String,
    val tournamentDescription: String,
    val date: LocalDate,
    val ranking: Int,
)
