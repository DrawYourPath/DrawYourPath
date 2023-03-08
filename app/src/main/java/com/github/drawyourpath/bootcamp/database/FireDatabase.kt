package com.github.drawyourpath.bootcamp.database

import android.graphics.Color
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Objects
import java.util.concurrent.CompletableFuture

class FireDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference

    override fun isUserNameAvailable(userName: String, outputText: TextView): Boolean {
        val future = CompletableFuture<Boolean>()

        if (userName == "") {
            outputText.text = buildString { append("The user name can't be empty !") }
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
            append("*The userID ")
            append(userName)
            append(" is NOT available !")
        }
        val availableOutput: String = buildString {
            append("*The userID ")
            append(userName)
            append(" is available !")
        }

        future.thenAccept {
            if (it) {
                outputText.text = availableOutput
                outputText.setTextColor(Color.GREEN)
            } else {
                outputText.text = unAvailableOutput
                outputText.setTextColor(Color.RED)
            }
        }

        if (outputText.text.toString() == availableOutput) {
            return true;
        }
        return false;
    }

    override fun setUserName(userName: String, outputText: TextView) {
        if (isUserNameAvailable(userName, outputText)) {
            val userAdd= HashMap<String, String>()
            userAdd.put(userName, "empty")
            database.child("users").updateChildren(userAdd as Map<String, Any>)
        }
    }
}

