package com.github.drawyourpath.bootcamp.webapi

data class BoredActivity(
    // Description of the activity
    val activity: String,
    // Type of the activity
    // ["education", "recreational", "social", "diy", "charity", "cooking", "relaxation", "music", "busywork"]
    val type: String,
    // The number of people that this activity could involve
    // [0, n]
    val participants: Int,
    // A factor describing the cost of the event with zero being free
    // [0, 1]
    val price: Float,
    // Internet link of the activity
    val link: String,
    // A unique numeric id
    // [1000000, 9999999]
    val key: Int,
    // A factor describing how possible an event is to do with zero being the most accessible
    // [0.0, 1.0]
    val accessibility: Float
)