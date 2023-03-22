package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.userProfile.UserModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

private val TIMEOUT_SERVER_REQUEST: Long = 10


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

        database.child(usersProfileFileName).child(userId).get().addOnSuccessListener {
            if (it.value == null) future.complete(true)
            else future.complete(false)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        database.child(usersProfileFileName).child(userId).child("username").get().addOnSuccessListener {
            if(it.value == null) future.completeExceptionally(NoSuchFieldException("There is no username corresponding to the userId $userId"))
            else future.complete(it.value as String)
        }.addOnFailureListener{
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        database.child(usernameToUserIdFileName).child(username).get().addOnSuccessListener {
            if(it.value == null) future.completeExceptionally(NoSuchFieldException("There is no userId corresponding to the username $username"))
            else future.complete(it.value as String)
        }.addOnFailureListener{
            future.completeExceptionally(it)
        }
        return future
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        database.child(usernameToUserIdFileName).child(userName).get().addOnSuccessListener {
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
        //obtain the past username form the userId
        getUsernameFromUserId(userId).thenAccept{ pastUsername ->
            if(pastUsername == null){
                future.completeExceptionally(java.lang.Error("Impossible to find the past username !"))
            }else{
                //update the link username to userId and the username on the userAccount
                setUsername(username).thenAccept{ isSetUsername ->
                    if(isSetUsername){
                        //remove the past username from the link username/userId
                        database.child(usernameToUserIdFileName).child(pastUsername).removeValue()
                            .addOnSuccessListener { future.complete(true) }
                            .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to remove the past username link !"))}
                    }else{
                        future.completeExceptionally(java.lang.Error("Impossible to set this username !"))
                    }
                }
            }
        }
        return future
    }

    override fun setUsername(username: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        val userId = getUserId()
        //check if the username is available
        isUsernameAvailable(username).thenAccept{isAvailable ->
            if(isAvailable){
                //add the link between the username and the userId
                val usernameToUserId = HashMap<String, String>()
                usernameToUserId.put(username, userId)
                database.child(usernameToUserIdFileName).updateChildren(usernameToUserId as Map<String, Any>)
                    .addOnSuccessListener {
                        //add the users account to the database and the username to the user account
                        val userAccount = HashMap<String, String>()
                        userAccount.put("username", username)
                        database.child("users").child(userId).updateChildren(userAccount as Map<String, Any>)
                            .addOnSuccessListener { future.complete(true) }
                            .addOnFailureListener {
                                future.completeExceptionally(Exception("Impossible to create the user account."))
                            }
                    }
                    .addOnFailureListener {
                        future.completeExceptionally(java.lang.Error("Impossible to find the link between the username and the userId to the Database.") )
                    }
            }else{
                future.completeExceptionally(java.lang.Error("The username is not available !"))
            }
        }
        return future
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean> {
        val userData = HashMap<String, Any>()
        userData.put("email", userModel.getEmailAddress())
        userData.put("firstname", userModel.getFirstname())
        userData.put("surname", userModel.getSurname())
        userData.put("dateOfBirth", userModel.getDateOfBirth().toEpochDay())
        userData.put("distanceGoal", userModel.getDistanceGoal())
        userData.put("activityTimeGoal", userModel.getActivityTime())
        userData.put("nbOfPathsGoal", userModel.getNumberOfPathsGoal())

        val future = CompletableFuture<Boolean>()
        val userId = getUserId()

        database.child(usersProfileFileName).child(userId).updateChildren(userData)
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to init the user data on the database.")) }
        return future
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        val future = CompletableFuture<UserModel>()

        database.child(usersProfileFileName).child(userId).get().addOnSuccessListener { userData ->
            if(userData == null) {
                future.completeExceptionally(java.lang.Error("There is no user account corresponding to this userId."))
            }else{
                if(userAuth==null){
                    future.completeExceptionally(java.lang.Error("There is no user logged on the app."))
                }else{
                    val firstname = userData.child("firstname").value
                    val surname = userData.child("surname").value
                    val dateOfBirth = userData.child("dateOfBirth").value
                    val distanceGoal = userData.child("distanceGoal").value
                    val activityTimeGoal = userData.child("activityTimeGoal").value
                    val nbOfPathsGoal = userData.child("nbOfPathsGoal").value
                    if(firstname==null||surname==null||dateOfBirth==null||distanceGoal==null||activityTimeGoal==null||nbOfPathsGoal==null){
                        future.completeExceptionally(java.lang.Error("The user account present on the database is incomplete."))
                    }else{
                        future.complete(
                            UserModel(userAuth, firstname as String, surname as String, LocalDate.ofEpochDay(dateOfBirth as Long),
                                    distanceGoal as Double, activityTimeGoal as Double, nbOfPathsGoal as Int, FireDatabase()))
                    }
                }
            }
        }.addOnFailureListener{
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        val userId = getUserId()
        return getUserAccount(userId)
    }

    override fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put("distanceGoal", distanceGoal)

        if(distanceGoal <= 0.0){
            throw java.lang.Error("The distance goal can't be less or equal than 0.")
        }

        database.child(usersProfileFileName).child(getUserId()).updateChildren(dataUpdated)
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to set the distance goal to the database"))}
        return future
    }

    override fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put("activityTimeGoal", activityTimeGoal)

        if(activityTimeGoal <= 0.0){
            throw java.lang.Error("The activity time goal can't be less or equal than 0.")
        }

        database.child(usersProfileFileName).child(getUserId()).updateChildren(dataUpdated)
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to set the activity time goal to the database"))}
        return future
    }

    override fun setNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val dataUpdated = HashMap<String, Any>()
        dataUpdated.put("nbOfPathsGoal", nbOfPathsGoal)

        if(nbOfPathsGoal <= 0.0){
            throw java.lang.Error("The number of paths goal can't be less or equal than 0.")
        }

        database.child(usersProfileFileName).child(getUserId()).updateChildren(dataUpdated)
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(java.lang.Error("Impossible to set the number of paths goal to the database"))}
        return future
    }

    /**
     * Helper function to get the userId from the authentication and check if a user is log
     * @return the userId of the user log on the app
     * @throw an error if any user is log on the app
     */
    private fun getUserId(): String{
        if(userAuth == null){
            throw java.lang.Error("Any user is log on the app")
        }
        return userAuth.getUid()
    }
}




