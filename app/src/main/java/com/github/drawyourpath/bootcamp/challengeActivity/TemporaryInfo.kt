package com.github.drawyourpath.bootcamp.challengeActivity

import java.util.Date

data class TemporaryInfo(
    val goal: Goal,
    val tournaments: List<Tournament>,
    val trophies: List<Trophy>) {

    companion object {
        val SAMPLE_DATA = TemporaryInfo(Goal(10.0, 30.0, 1),
            listOf(Tournament("test", "test"), Tournament("2nd test", "test"),
                Tournament("test3", "test3"), Tournament("4th test", "test")),
                listOf(Trophy("trophy1", "trophy1"),Trophy("trophy2", "trophy2"),
                    Trophy("trophy3", "trophy3"), Trophy("trophy4", "trophy4"),
                    Trophy("trophy5", "trophy5"), Trophy("trophy6", "trophy6"),
                    Trophy("trophy7", "trophy7"), Trophy("trophy8", "trophy8"),
                    Trophy("trophy9", "trophy9"), Trophy("trophy10", "trophy10")))
    }
}