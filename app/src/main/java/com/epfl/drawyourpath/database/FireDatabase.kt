package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
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

/**
 * The Firebase contains files:
 * -usernameToUserId: that link the username to a unique userId
 * -users: that contains users based on the UserModel defined by their userId
 */
class FireDatabase : Database() {
    val database: DatabaseReference = Firebase.database.reference
    private val userAuth: User? = FirebaseAuth.getUser()
    private val usernameToUserIdFileName: String = "usernameToUserId"
    private val usersProfileFileName: String = "users"

    override fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        accessUserAccountFile(userId).get().addOnSuccessListener {
            if (it.value != null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        accessUserAccountFile(userId).child(usernameFile).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException("There is no username corresponding to the userId $userId"))
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        accessUsernameToUserIdFile().child(username).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException("There is no userId corresponding to the username $username"))
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        accessUsernameToUserIdFile().child(userName).get().addOnSuccessListener {
            if (it.value == null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun updateUsername(username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            //obtain the past username form the userId
            getUsernameFromUserId(userId).thenAccept { pastUsername ->
                if (pastUsername == null) {
                    future.completeExceptionally(java.lang.Error("Impossible to find the past username !"))
                } else {
                    //update the link username to userId and the username on the userAccount
                    setUsername(username).thenAccept { isSetUsername ->
                        removeUsernameToUidMapping(pastUsername, future)
                    }
                }
            }
        }
        return future
    }

    override fun setUsername(username: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        //TODO:Add security rules to database
        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            //check if the username is available
            isUsernameAvailable(username).thenAccept { isAvailable ->
                if (isAvailable) {
                    //add the link between the username and the userId
                    val usernameToUserId = HashMap<String, String>()
                    usernameToUserId.put(username, userId)
                    accessUsernameToUserIdFile().updateChildren(usernameToUserId as Map<String, Any>)
                        .addOnSuccessListener {
                            //add the users account to the database and the username to the user account
                            val userAccount = HashMap<String, String>()
                            userAccount.put(usernameFile, username)
                            database.child(usersProfileFileName).child(userId)
                                .updateChildren(userAccount as Map<String, Any>)
                                .addOnSuccessListener { future.complete(Unit) }
                                .addOnFailureListener {
                                    future.completeExceptionally(Exception("Impossible to create the user account."))
                                }
                        }
                        .addOnFailureListener {
                            future.completeExceptionally(java.lang.Error("Impossible to find the link between the username and the userId to the Database."))
                        }
                } else {
                    future.completeExceptionally(java.lang.Error("The username is not available !"))
                }
            }
        }
        return future
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val userData = HashMap<String, Any>()
        userData.put(emailFile, userModel.getEmailAddress())
        userData.put(firstnameFile, userModel.getFirstname())
        userData.put(surnameFile, userModel.getSurname())
        userData.put(dateOfBirthFile, userModel.getDateOfBirth().toEpochDay())
        userData.put(currentDistanceGoalFile, userModel.getCurrentDistanceGoal())
        userData.put(currentActivityTimeGoalFile, userModel.getCurrentActivityTime())
        userData.put(currentNOfPathsGoalFile, userModel.getCurrentNumberOfPathsGoal())

        updateUserData(userData).thenApply {
            initUserAchievement().thenAccept { isInit ->
                if(isInit){
                    future.complete(Unit)
                }
                future.completeExceptionally(Exception("The user profile was not correctly initialized in the database !"))
            }
        }
        return future
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        val future = CompletableFuture<UserModel>()

        accessUserAccountFile(userId).get().addOnSuccessListener { userData ->
            future.complete(dataToUserModel(userData, userId))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        Log.d("Debug", "getLoggedUserAccount called!!!!!!!!!!!!!!!!!!")
        val userId = getUserId()
        return if (userId == null) {
            val future = CompletableFuture<UserModel>()
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
            future
        } else {
            getUserAccount(userId)
        }
    }

    override fun setCurrentDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        if (distanceGoal <= 0.0) {
            val future = CompletableFuture<Unit>()
            future.completeExceptionally(java.lang.Error("The distance goal can't be less or equal than 0."))
            return future
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(currentDistanceGoalFile, distanceGoal)
        return updateUserData(dataUpdated)
    }

    override fun setCurrentActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        if (activityTimeGoal <= 0.0) {
            val future = CompletableFuture<Unit>()
            future.completeExceptionally(java.lang.Error("The activity time goal can't be less or equal than 0."))
            return future
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(currentActivityTimeGoalFile, activityTimeGoal)
        return updateUserData(dataUpdated)
    }

    override fun setCurrentNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        if (nbOfPathsGoal <= 0) {
            val future = CompletableFuture<Unit>()
            future.completeExceptionally(java.lang.Error("The number of paths goal can't be less or equal than 0."))
            return future
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(currentNOfPathsGoalFile, nbOfPathsGoal)
        return updateUserData(dataUpdated)
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        val dataUpdated = HashMap<String, Any>()

        //convert the bitmap to a byte array
        val byteArray = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.WEBP, 70, byteArray)
        val imageEncoded: String = Base64.getEncoder().encodeToString(byteArray.toByteArray())
        dataUpdated.put(profilePhotoFile, imageEncoded)
        return updateUserData(dataUpdated)
    }

    override fun addUserToFriendsList(userId: String): CompletableFuture<Unit> {
        return isUserStoredInDatabase(userId).thenApply {
            if (!it) {
                throw Exception("The user with $userId is not present on the database.")
            } else {
                //add the user to the the friendList of the current user
                val currentUserId = getUserId()
                if (currentUserId == null) {
                    throw Exception("Any user is logged !")
                } else {
                    addUserIdToFriendList(currentUserId, userId).thenApply {
                        //add the currentUser to the friend list of the user with userId
                        addUserIdToFriendList(userId, currentUserId)
                    }
                }
            }
        }
    }

    override fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit> {
        val currentUserId = getUserId()
        if (currentUserId == null) {
            val future = CompletableFuture<Unit>()
            future.completeExceptionally(Exception("Any user is logged !"))
            return future
        } else {
            //remove the userId from the friendlist of the current user
            return removeUserIdToFriendList(currentUserId, userId).thenApply {
                //remove the current userId to the friendlist of the user with userId
                removeUserIdToFriendList(userId, currentUserId)
            }
        }
    }

    override fun addRunToHistory(run: Run): CompletableFuture<Unit> {
        //check if user is correctly logged
        val currentUserId = getUserId()
        val future = CompletableFuture<Unit>()
        if (currentUserId == null) {
            future.completeExceptionally(Exception("Any user is logged !"))
            return future
        }

        //create the field for the new path, the key is the start time
        val newRun = HashMap<String, Any>()
        newRun.put(run.getStartTime().toString(), run)
        //update the runs history on the database
        accessUserAccountFile(currentUserId).child(runsHistoryFile)
            .updateChildren(newRun as Map<String, Any>)
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }
        return future

    }

    override fun removeRunFromHistory(run: Run): CompletableFuture<Unit> {
        //check if user is correctly logged
        val currentUserId = getUserId()
        val future = CompletableFuture<Unit>()
        if (currentUserId == null) {
            future.completeExceptionally(Exception("Any user is logged !"))
            return future
        }

        //remove the run
        accessUserAccountFile(currentUserId).child(runsHistoryFile)
            .child(run.getStartTime().toString()).removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener { future.completeExceptionally(it) }

        return future
    }

    override fun addDailyGoal(dailyGoal: DailyGoal): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val currentUserId = getUserId()
        if(currentUserId==null){
            future.completeExceptionally(Exception("Any user is logged !"))
        }else{
            /*
            This is how the data are store in the database:
            dailyGoals = {
                date = {
                    expectedDistance = Value
                    expectedTime = Value
                    expectedNbOfPaths = Value
                    obtainedDistance = Value
                    obtainedActivityTime = Value
                    obtainedNbOfPaths = Value
                }
                date = {.....}
             }
             */
            //transform daily goal to data(except the date)
            val dailyGoalData = transformDailyGoalToData(dailyGoal)
            //add the daily goal or update it if it already exist with the new data at this date
            accessUserAccountFile(currentUserId).child(dailyGoalsFile)
                .child(dailyGoal.date.toEpochDay().toString())
                .updateChildren(dailyGoalData)
                .addOnSuccessListener { future.complete(Unit) }
                .addOnFailureListener { future.completeExceptionally(it) }
        }
        return future
    }

    override fun updateUserAchievements(
        distanceDrawing: Double,
        activityTimeDrawing: Double
    ): CompletableFuture<Unit> {
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
        val future = CompletableFuture<Unit>()

        //obtain the current achievements
        getCurrentUserAchievements().thenApply { pastAchievements ->
            val newAchievement = HashMap<String, Any>()
            newAchievement.put(totalDistanceFile, pastAchievements.get(totalDistanceFile) as Double + distanceDrawing)
            newAchievement.put(totalActivityTimeFile, pastAchievements.get(totalActivityTimeFile) as Double + activityTimeDrawing)
            newAchievement.put(totalNbOfPathsFile, pastAchievements.get(totalNbOfPathsFile) as Int + 1)
            val userId = getUserId()
            if (userId == null) {
                future.completeExceptionally(java.lang.Error("The userId can't be null !"))
            } else {
                accessUserAccountFile(userId).child(achievementsFile).updateChildren(newAchievement)
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener{ future.completeExceptionally(it) }
            }
        }
        return future
    }

    /**
     * Helper function to access the userAccount database file of a user
     * @param userId od the user
     * @return the database reference to this file
     */
    private fun accessUserAccountFile(userId: String): DatabaseReference {
        return database.child(usersProfileFileName).child(userId)
    }

    /**
     * Helper function to access the usernameToUserId database file
     */
    private fun accessUsernameToUserIdFile(): DatabaseReference {
        return database.child(usernameToUserIdFileName)
    }

    /**
     * Helper function to update data of the current user account
     * @param data to be updated
     * @return a future to indicated if the data have been correctly updated
     */
    private fun updateUserData(data: Map<String, Any>): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            accessUserAccountFile(userId).updateChildren(data)
                .addOnSuccessListener { future.complete(Unit) }
                .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to update the data in the database !")) }
        }
        return future
    }

    /**
     * Helper function to get the userId from the authentication and check if a user is log
     * @return the userId of the user log on the app
     * @throw an error if any user is log on the app
     */
    private fun getUserId(): String? {
        return userAuth?.getUid()
    }

    /**
     * Helper function to remove the past link from username->userId
     * @param username that will be removed form the mapping
     * @param future future state before the execution of this function
     */
    private fun removeUsernameToUidMapping(username: String, future: CompletableFuture<Unit>) {
        //remove the past username from the link username/userId
        accessUsernameToUserIdFile().child(username).removeValue()
            .addOnSuccessListener { future.complete(Unit) }
            .addOnFailureListener {
                future.completeExceptionally(
                    java.lang.Error(
                        "Impossible to remove the past username link !"
                    )
                )
            }
    }

    /**
     * Helper function to convert a data snapshot to a userModel
     * @param data data snapshot to convert
     * @param userId of the user
     * @return ta future that contains the user Model
     */
    private fun dataToUserModel(data: DataSnapshot?, userId: String): UserModel {
        if (data == null) {
            throw java.lang.Error("There is no user account corresponding to this userId.")
        } else {
            val email = data.child(emailFile).value
            val username = data.child(usernameFile).value
            val firstname = data.child(firstnameFile).value
            val surname = data.child(surnameFile).value
            val dateOfBirth = data.child(dateOfBirthFile).value
            val distanceGoal = data.child(currentDistanceGoalFile).value
            val activityTimeGoal = data.child(currentActivityTimeGoalFile).value
            val nbOfPathsGoal = data.child(currentNOfPathsGoalFile).value
            val friendsListData = data.child(friendsListFile)
            val runsHistoryData = data.child(runsHistoryFile)
            val dailyGoalData = data.child(dailyGoalsFile)
            val totalDistance = data.child(achievementsFile).child(totalDistanceFile).getValue(Double::class.java)
            val totalActivityTime = data.child(achievementsFile).child(totalActivityTimeFile).getValue(Double::class.java)
            val totalNbOfPaths = data.child(achievementsFile).child(totalNbOfPathsFile).value

            if (firstname == null || surname == null || dateOfBirth == null || distanceGoal == null || activityTimeGoal == null ||
                nbOfPathsGoal == null ||totalDistance == null || totalActivityTime == null || totalNbOfPaths == null) {
                throw java.lang.Error("The user account present on the database is incomplete.")
            } else {
                //test if the photoProfile is null to know if we need to decode it
                val profilePhotoEncoded = data.child(profilePhotoFile).value
                val profilePhoto = decodePhoto(profilePhotoEncoded)

                //obtain the friendsList
                val friendsList = transformFriendsList(friendsListData)

                //obtain the history
                val runsHistory = transformRunsHistory(runsHistoryData)

                //obtain the daily goal list
                val dailyGoalList = transformDataToDailyGoalList(dailyGoalData)

                //create the userModel
                return UserModel(
                    userId,
                    email as String,
                    username as String,
                    firstname as String,
                    surname as String,
                    LocalDate.ofEpochDay(dateOfBirth as Long),
                    (distanceGoal as Long).toDouble(),
                    (activityTimeGoal as Long).toDouble(),
                    (nbOfPathsGoal as Long).toInt(),
                    profilePhoto,
                    friendsList,
                    runsHistory,
                    this,
                    dailyGoalList,
                    totalDistance,
                    totalActivityTime,
                    (totalNbOfPaths as Long).toInt()
                )
            }
        }
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
                android.util.Log.w(FireDatabase::class.java.name, "A point of a run has invalid coordinates => ignoring the point")
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
        friendUserId: String
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        //create the field for the new friend
        val newFriend = HashMap<String, Boolean>()
        newFriend.put(friendUserId, true)
        //updated the friendlist in the database
        accessUserAccountFile(currentUserId).child(friendsListFile)
            .updateChildren(newFriend as Map<String, Any>)
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
        removeUserId: String
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        //obtain the previous friendList
        accessUserAccountFile(currentUserId).child(friendsListFile).get()
            .addOnSuccessListener { previousFriendList ->
                val newFriendList = HashMap<String, Any>()
                for(friend in previousFriendList.children) {
                    val friendStr = friend.key as String
                    if(friendStr != removeUserId){
                        newFriendList.put(friendStr, true)
                    }
                }
                accessUserAccountFile(currentUserId).child(friendsListFile).setValue(newFriendList)
                    .addOnSuccessListener { future.complete(Unit) }
                    .addOnFailureListener { err -> future.completeExceptionally(err) }
            }
            .addOnFailureListener { err -> future.completeExceptionally(err) }
        return future
    }

    /**
     * Helper function to transform a DailyGoal of a certain date object to a data object
     * @param dailyGoal DailyGoal object that we would like to transform in data object
     */
    private fun transformDailyGoalToData(dailyGoal: DailyGoal): HashMap<String, Any>{
        val dailyGoalData = HashMap<String, Any>()
        dailyGoalData.put(expectedDistanceFile, dailyGoal.distanceInKilometerGoal)
        dailyGoalData.put(expectedActivityTimeFile, dailyGoal.activityTimeInMinutesGoal)
        dailyGoalData.put(expectedNbOfPathsFile, dailyGoal.nbOfPathsGoal)
        dailyGoalData.put(obtainedDistanceFile, dailyGoal.distanceInKilometerProgress)
        dailyGoalData.put(obtainedActivityTimeFile, dailyGoal.activityTimeInMinutesProgress)
        dailyGoalData.put(obtainedNbOfPathsFile, dailyGoal.nbOfPathsProgress)

        return dailyGoalData
    }

    /**
     * Helper function to obtain the daily goal list from the database of the user
     * @param data the data snapshot containing the daily goal list
     * @return a list containing the daily goal realized by the user
     */
    private fun transformDataToDailyGoalList(data: DataSnapshot?): List<DailyGoal>{
        if(data == null){
            return emptyList()
        }
        val dailyGoalList = ArrayList<DailyGoal>()

        for(dailyGoal in data.children){
            val date: LocalDate = LocalDate.ofEpochDay(dailyGoal.key!!.toLong())
            val expectedDistance: Double = (dailyGoal.child(expectedDistanceFile).getValue(Double::class.java)!!)
            val expectedActivityTime: Double = (dailyGoal.child(expectedActivityTimeFile).getValue(Double::class.java)!!)
            val expectedNbOfPaths: Int = (dailyGoal.child(expectedNbOfPathsFile).value as Long).toInt()
            val obtainedDistance: Double = (dailyGoal.child(obtainedDistanceFile).getValue(Double::class.java)!!)
            val obtainedActivityTime: Double = (dailyGoal.child(obtainedActivityTimeFile).getValue(Double::class.java)!!)
            val obtainedNbOfPaths: Int = (dailyGoal.child(obtainedNbOfPathsFile).value as Long).toInt()

            dailyGoalList.add(DailyGoal(expectedDistance, expectedActivityTime, expectedNbOfPaths, obtainedDistance,
                obtainedActivityTime, obtainedNbOfPaths, date)
            )
        }
        return dailyGoalList
    }

    /**
     * Helper function to get the current user achievements from the database
     * @return a future that contains a map that contains the current achievements data(total distance, total activity time and total number of paths draw by the user)
     */
    private fun getCurrentUserAchievements(): CompletableFuture<HashMap<String, Any>>{
        val future = CompletableFuture<HashMap<String, Any>>()
        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            accessUserAccountFile(userId).child(achievementsFile).get()
                .addOnSuccessListener { dataAchievements->
                    val achievementsMap = HashMap<String, Any>()
                    achievementsMap.put(totalDistanceFile, (dataAchievements.child(totalDistanceFile).getValue(Double::class.java)!!))
                    achievementsMap.put(totalActivityTimeFile, (dataAchievements.child(totalActivityTimeFile).getValue(Double::class.java)!!))
                    achievementsMap.put(totalNbOfPathsFile, (dataAchievements.child(totalNbOfPathsFile).value as Long).toInt())
                    future.complete(achievementsMap)
                }
                .addOnFailureListener { future.completeExceptionally(it) }
        }
        return future
    }

    /**
     * Helper function to init the user achievement
     */
    private fun initUserAchievement(): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()

        val initAchievementData = HashMap<String, Any>()
        initAchievementData.put(totalDistanceFile, 0.0)
        initAchievementData.put(totalActivityTimeFile, 0.0)
        initAchievementData.put(totalNbOfPathsFile, 0)

        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            accessUserAccountFile(userId).child(achievementsFile).updateChildren(initAchievementData)
                .addOnSuccessListener { future.complete(true) }
                .addOnFailureListener { future.completeExceptionally(it) }
        }
        return future
    }

}





