package com.epfl.drawyourpath.challenge.trophy

import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.utils.Utils
import java.time.LocalDate

data class Trophy(
    val tournamentName: String,
    val tournamentDescription: String,
    val date: LocalDate,
    val ranking: Int,
    val drawable: Int = R.drawable.award,
    val dateAsString: String = Utils.getDateAsString(date),
)
