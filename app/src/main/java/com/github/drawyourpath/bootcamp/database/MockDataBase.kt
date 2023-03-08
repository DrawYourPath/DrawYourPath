package com.github.drawyourpath.bootcamp.database

import android.graphics.Color
import android.widget.TextView

class MockDataBase : Database() {
    var usersDataBase: ArrayList<String> = ArrayList<String>()

    override fun isUserNameAvailable(userName: String, outputText: TextView): Boolean {
        //add an element in the user database to test the UI
        usersDataBase.add("albert")
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
        if (userName == "") {
            outputText.text = buildString { append("The username can't be empty !") }
            outputText.setTextColor(Color.RED)
            return false
        } else if (usersDataBase.contains(userName)) {
            outputText.text = unAvailableOutput
            outputText.setTextColor(Color.RED)
            return false
        } else {
            outputText.text = availableOutput
            outputText.setTextColor(Color.GREEN)
            return true
        }
    }

    override fun setUserName(userName: String, outputText: TextView) {
        if (isUserNameAvailable(userName, outputText)) {
            usersDataBase.add(userName)
        }
    }

}