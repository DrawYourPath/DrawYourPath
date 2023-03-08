package com.github.drawyourpath.bootcamp.challengeActivity

data class Goal(val distanceInKilometer: Double,
                val timeInMinutes: Double,
                val nbOfShapes: Int) {


    /**
     * return the string corresponding to the position
     */
    fun getToString(pos: Int): String {
        return when(pos) {
            0 -> "$distanceInKilometer kilometers"
            1 -> "$timeInMinutes minutes"
            2 -> "$nbOfShapes shapes"
            else -> "pos should be : -1 < pos < ${count()}"
        }
    }

    fun getToDouble(pos: Int): Double {
        return when(pos) {
            0 -> distanceInKilometer
            1 -> timeInMinutes
            2 -> nbOfShapes.toDouble()
            else -> 0.0
        }
    }

    /**
     * return the number of attributes
     */
    fun count(): Int {
        return 3;
    }

}
