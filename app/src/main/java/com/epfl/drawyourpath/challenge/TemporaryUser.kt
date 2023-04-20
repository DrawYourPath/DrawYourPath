package com.epfl.drawyourpath.challenge

import com.epfl.drawyourpath.community.Tournament
import java.time.LocalDate
import java.util.*

/**
 * Temporary user which will need to be moved into user
 * //TODO delete this file
 * @param tournaments a list of tournaments the user takes part in
 * @param trophies the list of trophies earned by the user (should not be initialized at creation)
 */
data class TemporaryUser(
    val tournaments: List<Tournament> = listOf(),
    private val trophies: EnumMap<Trophy, LocalDate> = EnumMap(Trophy::class.java)
) : java.io.Serializable {

    /**
     * function to call when a trophy is earned
     * it will add the trophy along with the current date
     *
     * @param trophy the trophy earned
     */
    fun addTrophy(trophy: Trophy) {
        if (trophies[trophy] == null) {
            trophies[trophy] = LocalDate.now()
        }
    }

    /**
     * return a list containing all trophies earned along with the date
     *
     * @return List<Pair<Trophy, LocalDate>>
     */
    fun getTrophies(): List<Pair<Trophy, LocalDate>> {
        return trophies.toList().filter { it.second != null }
    }

}