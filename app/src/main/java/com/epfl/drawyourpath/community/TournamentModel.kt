package com.epfl.drawyourpath.community

/**
 *
 */
class TournamentModel : java.io.Serializable {

    private var weekly: Tournament? = null
    private var your: List<Tournament> = mutableListOf()
    private var discover: List<Tournament> = mutableListOf()

    /**
     * get the tournament of this week
     * TODO link with the database to get the correct one
     *
     * @return the weekly tournament
     */
    fun getWeeklyTournament(): Tournament? {
        return weekly
    }

    /**
     * get the tournament of the user
     * TODO link with the database to get the tournament of the user
     *
     * @param userId the id of the user
     * @return the list of tournaments of the user
     */
    fun getYourTournament(userId: String): List<Tournament> {
        return your.toList()
    }

    /**
     * get the tournament which the user might like
     * TODO link with the database to get the tournaments
     *
     * @param userId the id of the user
     * @return the list of tournaments the user might like
     */
    fun getDiscoverTournament(userId: String): List<Tournament> {
        return discover.toList()
    }

    fun setSample(sample: SampleTournamentModel) {
        weekly = sample.weekly
        your = sample.your
        discover = sample.discover
    }

    data class SampleTournamentModel(val weekly: Tournament, val your: List<Tournament>, val discover: List<Tournament>) : java.io.Serializable
}
