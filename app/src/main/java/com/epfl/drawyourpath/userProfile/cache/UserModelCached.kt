package com.epfl.drawyourpath.userProfile.cache

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.userProfile.UserModel
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class UserModelCached(application: Application) : AndroidViewModel(application) {
    //database where the user is store online
    private var database: Database = FireDatabase()

    //userModel
    private lateinit var userModel: UserModel

    //cached room database for user
    private val db = Room
        .databaseBuilder(application, UserDatabase::class.java, UserDatabase.NAME)
        .fallbackToDestructiveMigration()
        .build()
        .userDao()

    // user
    private var user: LiveData<UserData> = MutableLiveData()

    //TODO add friendList

    /**
     * This function will create a new user based on the user model of the app
     * @param userAuth user authenticate give by the login
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @param profilePhoto the photo of the user (default is null)
     * @throws error if the inputs are incorrect
     */
    fun setNewUser(
        userAuth: User,
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate,
        distanceGoal: Double,
        activityTimeGoal: Double,
        nbOfPathsGoal: Int,
        profilePhoto: Bitmap? = null
    ) {
        setNewUser(
            userAuth.getUid(),
            userAuth.getEmail(),
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            activityTimeGoal,
            nbOfPathsGoal,
            profilePhoto
        )
    }

    /**
     * This function will create a new user based on the user model of the app
     * @param userId of the user
     * @param emailAddress of the user
     * @param username is chosen during the profile create and each user has  unique username and it can be change later(if available in the database = not taken by another user)(the username is consider to be correct, since tested in the profile creation)
     * @param firstname must respect the name convention of the app (name or name-name)
     * @param surname must respect the name convention of the app (name or name-name)
     * @param dateOfBirth the user be aged between 10 and 100 years old
     * @param distanceGoal init at the user profile creation and can be modify after(daily goal)
     * @param activityTimeGoal init at the user profile creation and can be modify after(daily goal)
     * @param nbOfPathsGoal init at the user profile creation and can be modify after(daily goal)
     * @param profilePhoto the photo of the user (default is null)
     * @throws error if the inputs are incorrect
     */
    fun setNewUser(
        userId: String,
        emailAddress: String,
        username: String,
        firstname: String,
        surname: String,
        dateOfBirth: LocalDate,
        distanceGoal: Double,
        activityTimeGoal: Double,
        nbOfPathsGoal: Int,
        profilePhoto: Bitmap? = null
    ) {

        //check the format of the firstname
        checkNameFormat(firstname, "firstname")

        //check the format of the surname
        checkNameFormat(surname, "surname")

        //check that the birth date respect the age condition of the app(10<=age<=100)
        checkDateOfBirth(dateOfBirth)

        //test the goals, the goals can't be equal or less than 0
        checkDistanceGoal(distanceGoal)
        checkActivityTimeGoal(activityTimeGoal)
        checkNbOfPathsGoal(nbOfPathsGoal)

        CompletableFuture.supplyAsync {
            db.insert(
                UserData(
                    userId,
                    username,
                    emailAddress,
                    firstname,
                    surname,
                    dateOfBirth.toEpochDay(),
                    distanceGoal,
                    activityTimeGoal,
                    nbOfPathsGoal
                )
            )
        }.thenAccept {
            setAndGetCurrentUser(userId)
        }

        userModel = UserModel(
            userId,
            emailAddress,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            activityTimeGoal,
            nbOfPathsGoal,
            profilePhoto,
            database
        )

    }

    /**
     * set the user to an existing user inside the cache (should be called once only inside activity)
     * @param userId the userId of the new current user
     * @return the current user
     */
    fun setAndGetCurrentUser(userId: String): CompletableFuture<LiveData<UserData>> {
        return CompletableFuture.supplyAsync {
            user = db.getUserById(userId)
        }.exceptionally {
            throw Error("user id does not exist\n${it.printStackTrace()}")
        }.thenApply {
            getUser()
        }
    }

    /**
     * get the current user (read-only) (should be called only inside fragment
     * and [setAndGetCurrentUser] or [setNewUser] should have been called inside the parent activity )
     * @return the [LiveData] of [UserData]
     */
    fun getUser(): LiveData<UserData> {
        return user
    }

    /**
     * get userModel
     * @return the userModel
     */
    fun getUserModel(): UserModel {
        return userModel
    }

    /**
     * set the database used for online storing (mainly used for tests)
     * @param database the database
     */
    fun setDatabase(database: Database) {
        this.database = database
    }

    /**
     * Use this function to modify the username(the username will be modify only if it is available on the database)
     * @param username that we want to set
     */
    fun setUsername(username: String): CompletableFuture<Boolean> {
        return userModel.setUsername(username).thenApply {
            if (it) {
                if (user.value == null) {
                    throw Error("user should not be null")
                }
                db.updateUsername(user.value!!.userId, username)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        checkDistanceGoal(distanceGoal)
        return userModel.setDistanceGoal(distanceGoal).thenApply {
            if (it) {
                if (user.value == null) {
                    throw Error("user should not be null")
                }
                db.updateDistanceGoal(user.value!!.userId, distanceGoal)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param activityTimeGoal new daily activity time goal
     */
    fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        checkActivityTimeGoal(activityTimeGoal)
        return userModel.setActivityTimeGoal(activityTimeGoal).thenApply {
            if (it) {
                if (user.value == null) {
                    throw Error("user should not be null")
                }
                db.updateTimeGoal(user.value!!.userId, activityTimeGoal)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPathsGoal new daily number of paths goal
     */
    fun setNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        checkNbOfPathsGoal(nbOfPathsGoal)
        return userModel.setNumberOfPathsGoal(nbOfPathsGoal).thenApply {
            if (it) {
                if (user.value == null) {
                    throw Error("user should not be null")
                }
                db.updatePathsGoal(user.value!!.userId, nbOfPathsGoal)
            }
            it
        }
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    /*fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        return userModel.setProfilePhoto(photo).thenApply {
            if (it) {
                if (user.value == null) {
                    throw Error("user should not be null")
                }
                db.updatePhoto(user.value!!.userId, photo)
            }
            it
        }
    }*/
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return UserModelCached(application) as T
            }
        }
    }

}

/**
 * Helper function to check if the name format of a given variableName is correct and throw directly an error if it is incorrect
 * @param name to be check
 * @param variableName to be checked
 * @throw an error if the format is not correct
 */
private fun checkNameFormat(name: String, variableName: String) {
    if (name.find { !it.isLetter() && it != '-' } != null || name.isEmpty()) {
        throw java.lang.Error("Incorrect $variableName")
    }
}

/**
 * Helper function to check if the email address is correct
 * @param email to be checked
 * @return true is the email is in the correct format, and false otherwise
 */
private fun checkEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * Helper function to check if the date of birth of the user respect the age condition of the app
 * @param date of the user birth
 * @throw an error if the age of the user give by the birth date don't respect the ge condition of the app
 */
private fun checkDateOfBirth(date: LocalDate) {
    if (!(date < LocalDate.now().plusYears(-10) && date > LocalDate.now().plusYears(-100))) {
        throw java.lang.Error("Incorrect date of birth !")
    }
}

/**
 * Helper function to check if the distance goal is greater or equal than zero
 * @param distanceGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkDistanceGoal(distanceGoal: Double) {
    if (distanceGoal <= 0.0) {
        throw java.lang.Error("The distance goal can't be equal or less than 0.")
    }
}

/**
 * Helper function to check if the activity time goal is greater or equal than zero
 * @param activityTimeGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkActivityTimeGoal(activityTimeGoal: Double) {
    if (activityTimeGoal <= 0.0) {
        throw java.lang.Error("The activity time goal can't be equal or less than 0.")
    }
}

/**
 * Helper function to check if the number of paths goal is greater or equal than zero
 * @param nbOfPathsGoal to be checked
 * @throw an error if the goal is incorrect
 */
private fun checkNbOfPathsGoal(nbOfPathsGoal: Int) {
    if (nbOfPathsGoal <= 0) {
        throw java.lang.Error("The number of paths goal can't be equal or less than 0.")
    }
}