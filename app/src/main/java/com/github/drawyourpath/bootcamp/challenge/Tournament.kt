package com.github.drawyourpath.bootcamp.challenge

import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

/**
 * class representing tournaments that the user can take part in
 */
data class Tournament(
    val name: String,
    val description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
    //val result: List<User>?
) : java.io.Serializable {

    /**
     * return a string representing either when it starts, when it ends or when it has ended
     *
     * @return the resulting string
     */
    fun getStartOrEndDate(): String {
        val now = LocalDateTime.now(Clock.systemDefaultZone())
        if (now < startDate) {
            return "Start in ${getDurationToString(Duration.between(now, startDate))}"
        }
        if (now < endDate) {
            return "End in ${getDurationToString(Duration.between(now, endDate))}"
        }
        return "Ended the ${getDateToString(endDate)}"
    }

    /**
     * helper function to get the date
     */
    private fun getDateToString(date: LocalDateTime): String {
        val res = StringBuilder()
        res.append(date.dayOfMonth)
        res.append(" of ")
        res.append(date.month.toString())
        res.append(" ")
        res.append(date.year)
        return res.toString()
    }

    /**
     * helper function to get the duration between to dates
     */
    private fun getDurationToString(duration: Duration): String {
        val res = StringBuilder()
        if (duration.toDays() >= 1L) {
            res.append(duration.toDays().toInt())
            res.append(" days ")
        }
        if (duration.toHours() >= 0L) {
            res.append(duration.toHours().toInt() % 24)
            res.append(" hours ")
        }
        if (duration.toMinutes() >= 0L) {
            res.append(duration.toMinutes().toInt() % 60)
            res.append(" minutes")
        }
        return res.toString()
    }


}
