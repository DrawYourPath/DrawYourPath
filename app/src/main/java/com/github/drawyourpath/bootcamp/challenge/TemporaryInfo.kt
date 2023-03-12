package com.github.drawyourpath.bootcamp.challenge

import java.time.LocalDate
import java.time.LocalDateTime

data class TemporaryInfo(
    val dailyGoal: DailyGoal,
    val tournaments: List<Tournament>,
    val trophies: List<Trophy>
) {

    companion object {
        val SAMPLE_DATA = TemporaryInfo(
            DailyGoal(10.0, 30.0, 1, 6.34, 12.0, 1),
            listOf(
                Tournament("test", "test", LocalDateTime.now().plusDays(3L), LocalDateTime.now().plusDays(4L)),
                Tournament("2nd test", "test",  LocalDateTime.now().minusDays(1L),  LocalDateTime.now().plusDays(1L)),
                Tournament("test3", "test3",  LocalDateTime.now().minusDays(3L),  LocalDateTime.now().minusDays(2L)),
                Tournament("4th test", "test",  LocalDateTime.now(),  LocalDateTime.now().plusDays(3L))
            ),
            listOf(
                Trophy("trophy1", "trophy1"), Trophy("trophy2", "trophy2"),
                Trophy("trophy3", "trophy3"), Trophy("trophy4", "trophy4"),
                Trophy("trophy5", "trophy5"), Trophy("trophy6", "trophy6"),
                Trophy("trophy7", "trophy7"), Trophy("trophy8", "trophy8"),
                Trophy("trophy9", "trophy9"), Trophy("trophy10", "trophy10")
            )
        )
    }
}