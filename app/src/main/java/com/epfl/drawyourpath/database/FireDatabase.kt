package com.epfl.drawyourpath.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

private val TIMEOUT_SERVER_REQUEST: Long = 10

class FireDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference
    override fun isUserStoreOnDatabase(userId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        database.child("users").child(userId).get().addOnSuccessListener {
            if (it.value == null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun isUserNameAvailable(userName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        database.child("users").child(userName).get().addOnSuccessListener {
            if (it.value == null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun updateUsername(username: String, userId: String): CompletableFuture<Boolean> {
        TODO("This will be implmented during next task when I will clean the database organisation")
    }

    override fun setUserName(userName: String) {
        val userAdd = HashMap<String, String>()
        userAdd.put(userName, "empty")
        database.child("users").updateChildren(userAdd as Map<String, Any>)
            .addOnFailureListener { throw Exception("Impossible to add the username on the database") }
    }

    override fun setPersonalInfo(
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate
    ) {
        val userAdd = HashMap<String, String>()
        userAdd.put("firstname", firstname)
        userAdd.put("surname", surname)
        updateUserData(userAdd, username)

        //for the date
        val unixDate: Long = dateOfBirth.toEpochDay()
        val userDateMap = HashMap<String, Long>()
        userDateMap.put("dateOfBirth", unixDate)
        updateUserData(userDateMap, username)
    }

    override fun setUserGoals(
        username: String,
        distanceGoal: Int,
        timeGoal: Int,
        nbOfPathsGoal: Int
    ) {
        val userAdd = HashMap<String, String>()
        userAdd.put("distanceGoal", distanceGoal.toString())
        userAdd.put("activityTimeGoal", timeGoal.toString())
        userAdd.put("numberOfPathsGoal", nbOfPathsGoal.toString())
        updateUserData(userAdd, username)
    }

    /**
     * Helper functions to add some data to a user profile withe his username to the database
     * @param data date to add to the database
     * @param username corresponding to the user profile
     */
    private fun updateUserData(data: Map<String, Any>, username: String) {
        database.child("users").child(username).updateChildren(data)
            .addOnFailureListener {
                throw Exception(buildString {
                    append("Impossible to add the data on the user: ")
                    append(username)
                    append(" on the database")
                })
            }
    }
}

