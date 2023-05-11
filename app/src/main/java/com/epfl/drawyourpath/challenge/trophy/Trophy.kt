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
) {

    companion object {
        val sample = listOf(
            Trophy("Star Shape", "draw a star with 5 branches", LocalDate.ofYearDay(2023, 92), 1),
            Trophy("Discover the earth ", "draw the earth", LocalDate.ofYearDay(2023, 23), 3),
            Trophy("ShapeSpear", "draw a spear on the map", LocalDate.ofYearDay(2023, 35), 2),
            Trophy(
                "Most  Longest  greatest  Tournament  ever  of  all  times ................ : )",
                "This  is  a  long  description,  really  really  long...... still not long enough.... maybe now it is long enough",
                LocalDate.ofYearDay(2023, 89),
                1,
            ),
        )
    }
}
