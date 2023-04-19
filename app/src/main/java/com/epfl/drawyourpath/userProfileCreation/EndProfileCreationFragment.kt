package com.epfl.drawyourpath.userProfileCreation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class EndProfileCreationFragment : Fragment(R.layout.fragment_end_profile_creation) {
    private var username = ""

    private val usernameFile = "username"

    private val userCached: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //retrieve the isRunTestValue and userName from the PersonalInfoFragment
        val argsFromLastFrag: Bundle? = arguments
        if (argsFromLastFrag != null) {
            username = argsFromLastFrag.getString(usernameFile).toString()
        }
        val welcomeMessage: String =
            "We are happy to welcome you, " + username + " in the DrawYourPath app." +
                    " Please click on the BEGIN NOW button to discover the app !"
        val welcomeOutputTex: TextView =
            view.findViewById(R.id.endProfileCreation_text_userProfileCreation)
        welcomeOutputTex.text = welcomeMessage


        //go to the app main activity when click on the BEGIN NOW button
        val beginButton: Button = view.findViewById(R.id.goToMainActivity)
        beginButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_USER_ID, userCached.getUserId())
            this.startActivity(intent)
        }
    }
}