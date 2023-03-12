package com.github.drawyourpath.bootcamp.database

import android.graphics.Color
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withTimeout
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.collections.HashMap

private val TIMEOUT_SERVER_REQUEST: Long = 10
class FireDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference

    override fun isUserNameAvailable(userName: String, outputText: TextView): Boolean {
        val future = CompletableFuture<Boolean>()

        if (userName == "") {
            outputText.text = buildString { append("The username can't be empty !") }
            outputText.setTextColor(Color.RED)
            return false
        }

        database.child("users").child(userName).get().addOnSuccessListener {
            if (it.value == null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        val unAvailableOutput: String = buildString {
            append("*The username ")
            append(userName)
            append(" is NOT available !")
        }
        val availableOutput: String = buildString {
            append("*The username ")
            append(userName)
            append(" is available !")
        }
        val serverUnreachable: String = "The server is unreachable, please try again later !"

        //since the orTimeout require an API level 33(we are in min API level 28), we can't use it
        val durationFuture = future.thenAccept {
            if (it) {
                outputText.text = availableOutput
                outputText.setTextColor(Color.GREEN)
            } else {
                outputText.text = unAvailableOutput
                outputText.setTextColor(Color.RED)
            }
        }
        if(outputText.text.toString() == availableOutput){
            return true
        }
        /*
        outputText.text = serverUnreachable
        outputText.setTextColor(Color.RED)
        */
        if (outputText.text.toString() == availableOutput) {
            return true
        }
        return false
    }

    override fun setUserName(userName: String, outputText: TextView): Boolean {
        if (isUserNameAvailable(userName, outputText)) {
            val userAdd = HashMap<String, String>()
            userAdd.put(userName, "empty")
            database.child("users").updateChildren(userAdd as Map<String, Any>)
            return true
        }
        return false
    }

    override fun setPersonalInfo(username: String, firstname: String, surname: String, dateOfBirth: LocalDate) {
        val userAdd = HashMap<String, String>()
        userAdd.put("firstname", firstname)
        userAdd.put("surname", surname)
        val dateOfBirthStr: String = dateOfBirth.dayOfMonth.toString() + " / " + dateOfBirth.monthValue + " / " + dateOfBirth.year
        userAdd.put("dateOfBirth", dateOfBirthStr)
        database.child("users").child(username).updateChildren(userAdd as Map<String, Any>)
    }

    override fun setUserGoals(username: String, distanceGoal: Int, timeGoal: Int, nbOfPathsGoal: Int) {
        val userAdd = HashMap<String, String>()
        userAdd.put("distanceGoal", distanceGoal.toString())
        userAdd.put("activityTimeGoal", timeGoal.toString())
        userAdd.put("numberOfPathsGoal", nbOfPathsGoal.toString())
        database.child("users").child(username).updateChildren(userAdd as Map<String, Any>)
    }
}

