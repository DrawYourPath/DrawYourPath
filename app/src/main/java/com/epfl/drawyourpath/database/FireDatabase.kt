package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.userProfile.UserModel
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
            if (it.value == null) future.complete(true)
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

    override fun updateUsername(username: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

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
                        if (isSetUsername) {
                            future.thenApply { removeUsernameToUidMapping(username) }
                        } else {
                            future.completeExceptionally(java.lang.Error("Impossible to set this username !"))
                        }
                    }
                }
            }
        }
        return future
    }

    override fun setUsername(username: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
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
                                .addOnSuccessListener { future.complete(true) }
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

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean> {
        val userData = HashMap<String, Any>()
        userData.put(emailFile, userModel.getEmailAddress())
        userData.put(firstnameFile, userModel.getFirstname())
        userData.put(surnameFile, userModel.getSurname())
        userData.put(dateOfBirthFile, userModel.getDateOfBirth().toEpochDay())
        userData.put(distanceGoalFile, userModel.getDistanceGoal())
        userData.put(activityTimeGoalFile, userModel.getActivityTime())
        userData.put(nbOfPathsGoalFile, userModel.getNumberOfPathsGoal())

        return updateUserData(userData)
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        val future = CompletableFuture<UserModel>()

        accessUserAccountFile(userId).get().addOnSuccessListener { userData ->
            future.thenApply { dataToUserModel(userData, userId) }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        val userId = getUserId()
        val future = CompletableFuture<UserModel>()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        }else {
            future.thenApply{getUserAccount(userId)}
        }
        return future
    }

    override fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        if (distanceGoal <= 0.0) {
            val future = CompletableFuture<Boolean>()
            future.completeExceptionally(java.lang.Error("The distance goal can't be less or equal than 0."))
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(distanceGoalFile, distanceGoal)
        return updateUserData(dataUpdated)
    }

    override fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        if (activityTimeGoal <= 0.0) {
            val future = CompletableFuture<Boolean>()
            future.completeExceptionally(java.lang.Error("The activity time goal can't be less or equal than 0."))
            return future
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(activityTimeGoalFile, activityTimeGoal)
        return updateUserData(dataUpdated)
    }

    override fun setNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        if (nbOfPathsGoal <= 0) {
            val future = CompletableFuture<Boolean>()
            future.completeExceptionally(java.lang.Error("The number of paths goal can't be less or equal than 0."))
            return future
        }
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put(nbOfPathsGoalFile, nbOfPathsGoal)
        return updateUserData(dataUpdated)
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val dataUpdated = HashMap<String, Any>()

        //convert the bitmap to a byte array
        val byteArray = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
        val imageEncoded: String = Base64.getEncoder().encodeToString(byteArray.toByteArray())
        dataUpdated.put(profilePhotoFile, imageEncoded)
        return updateUserData(dataUpdated)
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
     * Helper function to upadte data of the current user account
     * @param data to be updated
     * @return a future to indicated if the data have been correctly updated
     */
    private fun updateUserData(data: Map<String, Any>): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val userId = getUserId()
        if (userId == null) {
            future.completeExceptionally(java.lang.Error("The userId can't be null !"))
        } else {
            accessUserAccountFile(userId).updateChildren(data)
                .addOnSuccessListener { future.complete(true) }
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
     * @return a future that indicate if the username was correctly removed
     */
    private fun removeUsernameToUidMapping(username: String) {
        val future = CompletableFuture<Boolean>()
        //remove the past username from the link username/userId
        accessUsernameToUserIdFile().child(username).removeValue()
            .addOnSuccessListener { future.complete(true) }
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
    private fun dataToUserModel(data: DataSnapshot?, userId: String): CompletableFuture<UserModel> {
        val future = CompletableFuture<UserModel>()

        if (data == null) {
            future.completeExceptionally(java.lang.Error("There is no user account corresponding to this userId."))
        } else {
            val email = data.child(emailFile).value
            val username = data.child(usernameFile).value
            val firstname = data.child(firstnameFile).value
            val surname = data.child(surnameFile).value
            val dateOfBirth = data.child(dateOfBirthFile).value
            val distanceGoal = data.child(distanceGoalFile).value
            val activityTimeGoal = data.child(activityTimeGoalFile).value
            val nbOfPathsGoal = data.child(nbOfPathsGoalFile).value

            //decode the photo
            val profilePhotoEncoded = data.child(profilePhotoFile).value
            val tabByte = Base64.getDecoder().decode(profilePhotoEncoded as String)
            val profilePhoto = BitmapFactory.decodeByteArray(tabByte, 0, tabByte.size)

            if (firstname == null || surname == null || dateOfBirth == null || distanceGoal == null || activityTimeGoal == null || nbOfPathsGoal == null) {
                future.completeExceptionally(java.lang.Error("The user account present on the database is incomplete."))
            } else {
                future.complete(
                    UserModel(
                        userId,
                        email as String,
                        username as String,
                        firstname as String,
                        surname as String,
                        LocalDate.ofEpochDay(dateOfBirth as Long),
                        distanceGoal as Double,
                        activityTimeGoal as Double,
                        nbOfPathsGoal as Int,
                        profilePhoto,
                        FireDatabase()
                    )
                )
            }

        }
        return future
    }
}





