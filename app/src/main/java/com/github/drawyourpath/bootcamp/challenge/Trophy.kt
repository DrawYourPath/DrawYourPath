package com.github.drawyourpath.bootcamp.challenge

enum class Trophy(val trophyName: String, val description: String, val imagePath: String = "trophy/trophy.png") : java.io.Serializable {

    THEFIRSTKM("The First Kilometer", "run a total of 1 kilometer"),
    TENKM("10 Kilometers", "run a total of 10 kilometers"),
    MARATHON("MARATHON", "run a total of 42.195 kilometer"),
    THEFIRSTHOUR("The first Hour", "run for a total of 1 hour"),
    THEFIRSTPATH("The first Path", "draw your fisrt path")

}

