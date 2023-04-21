package com.epfl.drawyourpath.challenge

import com.epfl.drawyourpath.community.Tournament
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Temporary user which will need to be moved into user
 *
 * @param dailyGoals a doubly linked-List which should always have the most recent dailyGoal as first
 * @param tournaments a list of tournaments the user takes part in
 * @param trophies the list of trophies earned by the user (should not be initialized at creation)
 */
data class TemporaryUser(
    private val dailyGoals: LinkedList<DailyGoal> = LinkedList(),
    val tournaments: List<Tournament> = listOf(),
    private val trophies: EnumMap<Trophy, LocalDate> = EnumMap(Trophy::class.java),
) : java.io.Serializable {

    /**
     * get Today's Daily Goal or create a new one if there is no Daily Goals for today
     *
     * @return DailyGoal representing today's Daily Goal
     */
    fun getTodayDailyGoal(): DailyGoal {
        if (dailyGoals.isEmpty()) {
            dailyGoals.addFirst(DEFAULT_DAILY_GOAL)
            return dailyGoals.first
        }
        if (dailyGoals.first.date == LocalDate.now()) {
            return dailyGoals.first
        }
        val newDailyGoal = dailyGoals.first.createNewGoalFromThis()
        dailyGoals.addFirst(newDailyGoal)
        return newDailyGoal
    }

    /**
     * function to call when a trophy is earned
     * it will add the trophy along with the current date
     *
     * @param trophy the trophy earned
     */
    fun addTrophy(trophy: Trophy) {
        if (trophies[trophy] != null) {
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

    companion object {
        /**
         * The default daily goal that will be used if no daily goals are set
         */
        val DEFAULT_DAILY_GOAL = DailyGoal(10.0, 30.0, 1)

        /**
         * sample data used to test
         */
        val SAMPLE_DATA = TemporaryUser(
            LinkedList(mutableListOf(DailyGoal(10.0, 30.0, 1, 6.34, 12.0, 1))),
            listOf(
                Tournament(
                    "test",
                    "test",
                    LocalDateTime.now().plusDays(3L),
                    LocalDateTime.now().plusDays(4L),
                ),
                Tournament(
                    "2nd test",
                    "test",
                    LocalDateTime.now().minusDays(1L),
                    LocalDateTime.now().plusDays(1L),
                ),
                Tournament(
                    "test3",
                    "test3",
                    LocalDateTime.now().minusDays(3L),
                    LocalDateTime.now().minusDays(2L),
                ),
                Tournament(
                    "4th test",
                    "test",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(3L),
                ),
            ),
            createTrophies(),
        )

        private fun createTrophies(): EnumMap<Trophy, LocalDate> {
            val trophies: EnumMap<Trophy, LocalDate> = EnumMap(Trophy::class.java)
            trophies[Trophy.MARATHON] = LocalDate.now().minusDays(5L)
            trophies[Trophy.THEFIRSTPATH] = LocalDate.now().minusWeeks(10L)
            trophies[Trophy.THEFIRSTHOUR] = LocalDate.now().minusWeeks(5L)
            trophies[Trophy.THEFIRSTKM] = LocalDate.now().minusMonths(5L)
            trophies[Trophy.TENKM] = LocalDate.now().minusWeeks(3L)
            return trophies
        }
    }
}
