package com.epfl.drawyourpath.mainpage.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.Utils.drawyourpath.Utils
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.TrophyDialog
import com.epfl.drawyourpath.database.*
import com.epfl.drawyourpath.qrcode.generateQR
import java.util.concurrent.CompletableFuture

const val PROFILE_USER_ID_KEY = "userId"
const val PROFILE_TEST_KEY = "test"
const val PROFILE_TEST_FAILING_KEY = "testFailing"

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    lateinit var database: Database

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImageIcon(R.id.IV_Trophy, R.drawable.award)
        setImageIcon(R.id.IV_Achievements, R.drawable.achievements)
        setImageIcon(R.id.IV_ProfilePicture, R.drawable.profile_placholderpng)

        view.findViewById<TextView>(R.id.TV_Trophy).setOnClickListener { onTrophyClicked() }

        database = createDatabase()

        arguments?.getString(PROFILE_USER_ID_KEY).let {
            when (it) {
                null -> updateUiForError("This user doesn't exist (Empty user).")
                else -> fetchUser(it)
            }
        }
    }

    private fun createDatabase(): Database {
        return when (arguments?.getBoolean(PROFILE_TEST_KEY) ?: false) {
            true -> when (arguments?.getBoolean(PROFILE_TEST_FAILING_KEY) ?: false) {
                true -> MockNonWorkingDatabase()
                false -> MockDatabase()
            }
            false -> FirebaseDatabase()
        }
    }

    private fun fetchUser(userId: String) {
        ilog("Fetching user $userId")

        updateUiForError("Loading...")
        database.getUserData(userId)
            .thenAccept {
                ilog("User $userId fetched.")

                updateUiForData(it)
            }.exceptionally {
                updateUiForError(it.localizedMessage ?: "Unknown error.")
                null
            }
    }

    private fun updateUiForData(userData: UserData) {
        val dailyGoals = (userData.dailyGoals ?: emptyList())

        setQRCodeUserID(userData.userId ?: "")
        setUsername(userData.username ?: "Anonymous")

        loadFriendsNames(userData.friendList ?: emptyList())

        if (userData.picture != null && userData.picture.isNotEmpty()) {
            setUserImage(Utils.decodePhoto(userData.picture))
        }

        setTotalKilometer(
            dailyGoals.fold(0.0) {
                    acc, dailyGoal ->
                acc + dailyGoal.distance
            }.toInt(),
        )
        setGoalsReached(
            dailyGoals.fold(0) {
                    acc, dailyGoal ->
                acc + if (dailyGoal.wasReached()) 1 else 0
            },
        )
        setAverageSpeed(
            dailyGoals.fold(Pair(0.0, 0.0)) {
                    acc, dailyGoal ->
                Pair(acc.first + dailyGoal.distance, acc.second + dailyGoal.time)
            }.let { if (it.second == 0.0) 0.0 else it.first / it.second }.toInt(),
        )
        setShapesDrawn(
            dailyGoals.fold(0) {
                    acc, dailyGoal ->
                acc + dailyGoal.paths
            },
        )

        // TODO: Add these stats when we implemented them.
        setStreak(0)
        setTrophyCount(0)
        setAchievementsCount(0)

        setErrorVisibility(false)
    }

    private fun loadFriendsNames(friendIds: List<String>) {
        val futures = friendIds.map { database.getUsername(it) }
        CompletableFuture.allOf(*futures.toTypedArray())
            .thenApply {
                futures.mapNotNull {
                    if (it.isDone && !it.isCompletedExceptionally) it.get() else null
                }
            }
            .thenAccept {
                populateFriendList(it)
            }
    }

    private fun updateUiForError(error: String) {
        view?.findViewById<TextView>(R.id.TV_Error)?.text = error
        setErrorVisibility(true)
    }

    private fun setErrorVisibility(visible: Boolean) {
        view?.findViewById<TextView>(R.id.TV_Error)?.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    private fun onTrophyClicked() {
        openTrophyFragment()
    }

    private fun openTrophyFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        TrophyDialog().show(fragmentManager, "dialog")
    }

    private fun populateFriendList(friends: List<String>) {
        val friendsList = view?.findViewById<ListView>(R.id.LV_Friends)

        friendsList?.adapter = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_list_item_1,
            friends,
        )
    }

    private fun setStreak(streak: Int) {
        setTextViewText(R.id.TV_DaysStreak, String.format("%d Days", streak))
    }

    private fun setTotalKilometer(kilometers: Int) {
        setTextViewText(R.id.TV_TotalKilometers, String.format("%d KM", kilometers))
    }

    private fun setShapesDrawn(shapesDrawn: Int) {
        setTextViewText(R.id.TV_ShapesDrawn, String.format("%d Shapes", shapesDrawn))
    }

    private fun setAverageSpeed(avgSpeed: Int) {
        setTextViewText(R.id.TV_AvgSpeed, String.format("%d KM/H", avgSpeed))
    }

    private fun setAchievementsCount(achievementsCount: Int) {
        setTextViewText(R.id.TV_Achievements, achievementsCount.toString())
    }

    private fun setTrophyCount(trophyCount: Int) {
        setTextViewText(R.id.TV_Trophy, trophyCount.toString())
    }

    private fun setGoalsReached(goalsReached: Int) {
        setTextViewText(R.id.TV_GoalsReached, String.format("%d Goals", goalsReached))
    }

    private fun setTextViewText(id: Int, text: String) {
        view?.findViewById<TextView>(id)?.text = text
    }

    private fun setImageIcon(id: Int, resourceId: Int) {
        view?.findViewById<ImageView>(id)?.setImageResource(resourceId)
    }

    private fun setQRCodeUserID(uid: String) {
        view?.findViewById<ImageView>(R.id.IV_QRCode)?.setImageBitmap(generateQR(uid, 300))
    }

    private fun setUsername(username: String) {
        view?.findViewById<TextView>(R.id.TV_username)?.text = username
    }

    private fun setUserImage(image: Bitmap) {
        view?.findViewById<ImageView>(R.id.IV_ProfilePicture)?.setImageBitmap(image)
    }

    private fun ilog(text: String) {
        Log.i("ProfileFragment", text)
    }
}
