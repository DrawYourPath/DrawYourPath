package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.epfl.Utils.drawyourpath.Utils
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

class FirebaseKeys {
    companion object {
        // Database root entries
        const val USERS_ROOT = "users"
        const val USERNAMES_ROOT = "usernameToUid"

        // User keys top level
        const val PROFILE = "profile"
        const val GOALS = "goals"
        const val RUN_HISTORY = "runs"
        const val FRIENDS = "friends"
        const val DAILY_GOALS = "dailyGoals"

        // User profile keys sublevel
        const val USERNAME = "username"
        const val FIRSTNAME = "firstname"
        const val SURNAME = "surname"
        const val BIRTHDATE = "birth"
        const val EMAIL = "email"
        const val PICTURE = "picture"

        // User goal keys sublevel
        const val GOAL_PATH = "paths"
        const val GOAL_DISTANCE = "distance"

        // Goals history list sublevels
        const val GOAL_HISTORY_EXPECTED_DISTANCE = "expectedDistance"
        const val GOAL_HISTORY_EXPECTED_PATHS = "expectedPaths"
        const val GOAL_HISTORY_EXPECTED_TIME = "expectedTime"
        const val GOAL_HISTORY_DISTANCE = "distance"
        const val GOAL_HISTORY_PATHS = "paths"
        const val GOAL_HISTORY_TIME = "time"
    }
}

/**
 * The Firebase contains files:
 * -usernameToUserId: that link the username to a unique userId
 * -users: that contains users based on the UserModel defined by their userId
 */
class FirebaseDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference

    private fun userRoot(userId: String): DatabaseReference {
        return database.child(FirebaseKeys.USERS_ROOT).child(userId)
    }

    private fun userProfile(userId: String): DatabaseReference {
        return userRoot(userId).child(FirebaseKeys.PROFILE)
    }

    private fun nameMappingRoot(): DatabaseReference {
        return database.child(FirebaseKeys.USERNAMES_ROOT)
    }

    private fun nameMapping(username: String): DatabaseReference {
        return nameMappingRoot().child(username)
    }

    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        userProfile(userId).child(FirebaseKeys.USERNAME).get()
            .addOnSuccessListener {
                future.complete(it.value != null)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun getUsername(userId: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        userProfile(userId).child(FirebaseKeys.USERNAME).get()
            .addOnSuccessListener {
                if (it.value !is String) {
                    future.completeExceptionally(
                        NoSuchFieldException("There is no username corresponding to the userId $userId"),
                    )
                } else {
                    future.complete(it.value as String)
                }
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        nameMapping(username).get()
            .addOnSuccessListener {
                if (it.value !is String) {
                    future.completeExceptionally(NoSuchFieldException("There is no userId corresponding to the username $username"))
                } else {
                    future.complete(it.value as String)
                }
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }
        return future
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        nameMapping(userName).get().addOnSuccessListener {
            future.complete(it.value == null)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    override fun setUsername(userId: String, username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        ilog("Setting username $username for userid $userId")

        // Checks for availability.
        isUsernameAvailable(username).handle { isAvailable, exc ->
            if (!isAvailable || exc != null) {
                future.completeExceptionally(Error("Username already taken"))
                return@handle
            }

            // Wanted username is available, we get the old username.
            getUsername(userId).handle { pastUsername, exc ->

                // Create a new mapping to the new username.
                nameMapping(username).setValue(userId).addOnSuccessListener {
                    // If there is a past username.
                    if (exc == null) {
                        // And remove the old one.
                        nameMapping(pastUsername).removeValue { _, _ ->
                            future.complete(Unit)
                        }
                    } else {
                        future.complete(Unit)
                    }
                }.addOnFailureListener {
                    future.completeExceptionally(Error("Failed to write new username"))
                }
            }
        }

        return future
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val data = listOf<Pair<String, Any?>>(
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.USERNAME}" to userData.username,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.BIRTHDATE}" to userData.birthDate,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.PICTURE}" to userData.picture,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.SURNAME}" to userData.surname,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.FIRSTNAME}" to userData.firstname,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_DISTANCE}" to userData.goals?.distance,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_PATH}" to userData.goals?.paths,

        ).filter { entry -> entry.second != null }.associate { entry -> entry }

        userRoot(userId).updateChildren(data)
            .addOnSuccessListener {
                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val data = listOf<Pair<String, Any?>>(
            // We don't set the username here as updating username is more complicated.
            // see FirebaseDatabase.setUsername()
            // "${FirebaseKeys.PROFILE}/${FirebaseKeys.USERNAME}" to userData.username,

            "${FirebaseKeys.PROFILE}/${FirebaseKeys.BIRTHDATE}" to userData.birthDate,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.PICTURE}" to userData.picture,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.SURNAME}" to userData.surname,
            "${FirebaseKeys.PROFILE}/${FirebaseKeys.FIRSTNAME}" to userData.firstname,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_DISTANCE}" to userData.goals?.distance,
            "${FirebaseKeys.GOALS}/${FirebaseKeys.GOAL_PATH}" to userData.goals?.paths,

        ).filter { entry -> entry.second != null }.associate { entry -> entry }

        userRoot(userId).updateChildren(data)
            .addOnSuccessListener {
                future.complete(Unit)
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun getUserData(userId: String): CompletableFuture<UserData> {
        val future = CompletableFuture<UserData>()

        userRoot(userId).get().addOnSuccessListener { data ->
            future.complete(mapToUserData(data, userId))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit> {
        if (goals.distance != null && goals.distance <= 0.0) {
            return Utils.failedFuture(Exception("Distance must be greater than 0."))
        }

        if (goals.paths != null && goals.paths <= 0) {
            return Utils.failedFuture(Exception("Path must be greater than 0."))
        }

        return setUserData(userId, UserData(goals = goals))
    }

    override fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit> {
        // TODO: Use Firebase Storage.
        // convert the bitmap to a byte array
        val byteArray = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.WEBP, 70, byteArray)

        val imageEncoded: String = Base64.getEncoder().encodeToString(byteArray.toByteArray())
        return setUserData(userId, UserData(picture = imageEncoded))
    }

    override fun addFriend(
        userId: String,
        targetFriend: String,
    ): CompletableFuture<Unit> {
        return addUserIdToFriendList(userId, targetFriend).thenApply {
            // add the currentUser to the friend list of the user with userId
            addUserIdToFriendList(targetFriend, userId)
        }
    }

    override fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return removeUserIdToFriendList(userId, targetFriend).thenApply {
            // remove the current userId to the friendlist of the user with userId
            removeUserIdToFriendList(targetFriend, userId)
        }
    }

    override fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit> {
        // create the field for the new path, the key is the start time
        return setData(
            userId,
            hashMapOf(
                "${FirebaseKeys.RUN_HISTORY}/${run.getStartTime()}" to run,
            ),
        )
    }

    override fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        userRoot(userId).child(FirebaseKeys.RUN_HISTORY).child(run.getStartTime().toString())
            .removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // This is how the data is store in the database:
        // dailyGoals = {
        //    date = {
        //        expectedDistance = Value
        //        expectedTime = Value
        //        expectedNbOfPaths = Value
        //        obtainedDistance = Value
        //        obtainedActivityTime = Value
        //        obtainedNbOfPaths = Value
        //    }
        //    date = {.....}
        // }

        // transform daily goal to data(except the date)
        val dailyGoalData = transformDailyGoals(dailyGoal)

        // add the daily goal or update it if it already exist with the new data at this date
        userRoot(userId).child(FirebaseKeys.DAILY_GOALS)
            .child(dailyGoal.date.toEpochDay().toString())
            .updateChildren(dailyGoalData)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun updateUserAchievements(
        userId: String,
        distanceDrawing: Double,
        activityTimeDrawing: Double,
    ): CompletableFuture<Unit> {
        // TODO: Rewrite this function so it can scale as we add achievements.
        //       Current implementation is too restrictive.
        /*
        This is how the achievements are store in the firebase:
        Users{
            userId{
                username: Value
                ....
                achievements{
                    totalDistance: Value
                    totalActivityTime: Value
                    totalNbOfPaths: Value
                }
            }
        }
         */
        /*

        val future = CompletableFuture<Unit>()

        // obtain the current achievements
        getCurrentUserAchievements().thenApply { pastAchievements ->
            val newAchievement = HashMap<String, Any>()
            newAchievement.put(
                totalDistanceFile,
                pastAchievements.get(totalDistanceFile) as Double + distanceDrawing
            )
            newAchievement.put(
                totalActivityTimeFile,
                pastAchievements.get(totalActivityTimeFile) as Double + activityTimeDrawing
            )
            newAchievement.put(
                totalNbOfPathsFile,
                pastAchievements.get(totalNbOfPathsFile) as Int + 1
            )
            val userId = getUserId()
            if (userId == null) {
                future.completeExceptionally(Error("The userId can't be null !"))
            } else {
                userAccountFile(userId).child(achievementsFile).updateChildren(newAchievement)
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { future.completeExceptionally(it) }
            }
        }
        return future
        */
        return Utils.failedFuture(Error("Not implemented"))
    }

    /**
     * Helper function to update data of the current user account
     * @param data to be updated
     * @return a future to indicated if the data have been correctly updated
     */
    private fun setData(userId: String, data: Map<String, Any?>): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        userRoot(userId).updateChildren(data)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future
    }

    /**
     * Helper function to convert a data snapshot to a userModel
     * @param data data snapshot to convert
     * @param userId of the user
     * @return ta future that contains the user Model
     */
    private fun mapToUserData(data: DataSnapshot, userId: String): UserData {
        val profile = data.child(FirebaseKeys.PROFILE)
        val goals = data.child(FirebaseKeys.GOALS)

        return UserData(
            userId = userId,
            username = profile.child(FirebaseKeys.USERNAME).value as String?,
            firstname = profile.child(FirebaseKeys.FIRSTNAME).value as String?,
            surname = profile.child(FirebaseKeys.SURNAME).value as String?,
            birthDate = profile.child(FirebaseKeys.BIRTHDATE).value as Long?,
            email = profile.child(FirebaseKeys.EMAIL).value as String?,
            goals = UserGoals(
                paths = (goals.child(FirebaseKeys.GOAL_PATH).value as Number?)?.toLong(),
                distance = (goals.child(FirebaseKeys.GOAL_DISTANCE).value as Number?)?.toDouble(),
            ),
            picture = profile.child(FirebaseKeys.PICTURE).value as String?,
            runs = transformRunsHistory(profile.child(FirebaseKeys.RUN_HISTORY)),
            dailyGoals = transformDataToDailyGoalList(profile.child(FirebaseKeys.DAILY_GOALS)),
            friendList = transformFriendsList(profile.child(FirebaseKeys.FRIENDS)),
        )
    }

    /**
     * Helper function to decode the photo from string to bitmap format and return null if the dataSnapShot is null
     * @param photoStr photo encoded
     * @return the photo in bitmap format, and null if no photo is stored on the database
     */
    private fun decodePhoto(photoStr: Any?): Bitmap? {
        return if (photoStr == null) {
            null
        } else {
            val tabByte = Base64.getDecoder().decode(photoStr as String)
            BitmapFactory.decodeByteArray(tabByte, 0, tabByte.size)
        }
    }

    /**
     * Helper function to obtain the friends list from the database of the user
     * @param data the data snapshot containing the user List
     * @return a list containing the userIds of the friends
     */
    private fun transformFriendsList(data: DataSnapshot?): List<String> {
        if (data == null) {
            return emptyList()
        }
        val friendListUserIds = ArrayList<String>()

        for (friend in data.children) {
            friendListUserIds.add(friend.key as String)
        }
        return friendListUserIds
    }

    /**
     * Helper function to obtain the runs history from the database of the user
     * @param data the data snapshot containing the history
     * @return a list containing the history of the runs of the user
     */
    private fun transformRunsHistory(data: DataSnapshot?): List<Run> {
        if (data == null) {
            return emptyList()
        }
        val runsHistory = ArrayList<Run>()

        for (run in data.children) {
            val points = ArrayList<LatLng>()
            for (point in run.child("path").child("points").children) {
                val lat = point.child("latitude").getValue(Double::class.java)
                val lon = point.child("longitude").getValue(Double::class.java)
                if (lat != null && lon != null) {
                    points.add(LatLng(lat, lon))
                }
            }
            val startTime = run.child("startTime").value as? Long
            val endTime = run.child("endTime").value as? Long
            if (startTime != null && endTime != null) {
                runsHistory.add(Run(Path(points), startTime, endTime))
            } else {
                android.util.Log.w(
                    FirebaseDatabase::class.java.name,
                    "A point of a run has invalid coordinates => ignoring the point",
                )
            }
        }

        return runsHistory
    }

    /**
     * Helper function to add a userId "friendUserId" to the friendList of a a user with userId "currentUserId"
     * @param currentUserId userId that belong the friendlist
     * @param friendUserId userId that we want to add from the friendlist
     * @return a future that indicate if the userId has been correctly added to the friendlist
     */
    private fun addUserIdToFriendList(
        currentUserId: String,
        friendUserId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // create the field for the new friend
        val newFriend = hashMapOf<String, Any>(friendUserId to true)

        // updated the friendlist in the database
        userRoot(currentUserId).child(FirebaseKeys.FRIENDS)
            .updateChildren(newFriend)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    /**
     * Helper function to remove a userId "removeUserId" to the friendList of a a user with userId "currentUserId"
     * @param currentUserId userId that belong the friendlist
     * @param removeUserId userId that we want to remove from the friendlist
     * @return a future that indicate if the userId has been correctly added to the friendlist
     */
    private fun removeUserIdToFriendList(
        currentUserId: String,
        removeUserId: String,
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        // obtain the previous friendList
        userRoot(currentUserId).child(FirebaseKeys.FRIENDS).child(removeUserId).removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { err -> future.completeExceptionally(err) }

        return future
    }

    /**
     * Helper function to transform a DailyGoal of a certain date object to a data object
     * @param dailyGoal DailyGoal object that we would like to transform in data object
     */
    private fun transformDailyGoals(dailyGoal: DailyGoal): HashMap<String, Any> {
        return hashMapOf(
            FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE to dailyGoal.expectedDistance,
            FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME to dailyGoal.expectedTime,
            FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS to dailyGoal.expectedPaths,
            FirebaseKeys.GOAL_HISTORY_DISTANCE to dailyGoal.distance,
            FirebaseKeys.GOAL_HISTORY_TIME to dailyGoal.time,
            FirebaseKeys.GOAL_HISTORY_PATHS to dailyGoal.paths,
        )
    }

    /**
     * Helper function to obtain the daily goal list from the database of the user
     * @param data the data snapshot containing the daily goal list
     * @return a list containing the daily goal realized by the user
     */
    private fun transformDataToDailyGoalList(data: DataSnapshot?): List<DailyGoal> {
        if (data == null) {
            return emptyList()
        }

        val dailyGoalList = ArrayList<DailyGoal>()

        for (dailyGoal in data.children) {
            val date: LocalDate? =
                if (dailyGoal.key != null) LocalDate.ofEpochDay(dailyGoal.key!!.toLong()) else null
            val expectedDistance: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_DISTANCE)
                    .getValue(Double::class.java)
            val expectedTime: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_TIME)
                    .getValue(Double::class.java)
            val expectedPaths: Int? =
                (dailyGoal.child(FirebaseKeys.GOAL_HISTORY_EXPECTED_PATHS).value as Long?)?.toInt()
            val distance: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_DISTANCE).getValue(Double::class.java)
            val time: Double? =
                dailyGoal.child(FirebaseKeys.GOAL_HISTORY_TIME).getValue(Double::class.java)
            val paths: Int? =
                (dailyGoal.child(FirebaseKeys.GOAL_HISTORY_PATHS).value as Long?)?.toInt()

            if (date == null) {
                continue
            }

            dailyGoalList.add(
                DailyGoal(
                    expectedDistance = expectedDistance ?: 0.0,
                    expectedPaths = expectedPaths ?: 0,
                    expectedTime = expectedTime ?: 0.0,
                    distance = distance ?: 0.0,
                    time = time ?: 0.0,
                    paths = paths ?: 0,
                    date = date,
                ),
            )
        }
        return dailyGoalList
    }

    private fun ilog(text: String) {
        Log.i("Firebase Database", text)
    }
}
