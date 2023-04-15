package com.epfl.drawyourpath.userProfile.cache

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.room.Room
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.userProfile.UserModel
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * This class is persistent between fragment but not between activity.
 *
 * To use it inside fragment: `private val user: UserModelCached by activityViewModels()`
 *
 * To use it inside activity: `private val user: UserModelCached by viewModels()`
 *
 * Inside the activity, either create a new user with [createNewUser] or set to an existing user [setCurrentUser].
 *
 * For testing: either never use [createNewUser] or [setCurrentUser] or use [setDatabase] with [MockDataBase] as argument
 * both options will set the current user to a mock user with the [MockDataBase]
 */
class UserModelCached(application: Application) : AndroidViewModel(application) {
    //database where the user is store online
    private var database: Database = FireDatabase()

    //userModel
    private lateinit var userModel: UserModel

    //cached room database for user
    private val cache = Room
        .databaseBuilder(application, UserDatabase::class.java, UserDatabase.NAME)
        .fallbackToDestructiveMigration()
        .build()
        .userDao()

    // current user id
    private var currentUserID: String? = null

    // current user id
    private val _currentUserID = MutableLiveData<String>()

    // user
    private val user: LiveData<UserEntity> = _currentUserID.switchMap { cache.getUserById(it) }

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
    fun createNewUser(
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
        createNewUser(
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
    fun createNewUser(
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

        val userEntity = UserEntity(
            userId,
            username,
            emailAddress,
            firstname,
            surname,
            UserEntity.fromLocalDateToLong(dateOfBirth),
            distanceGoal,
            activityTimeGoal,
            nbOfPathsGoal,
            UserEntity.fromBitmapToByteArray(profilePhoto)
        )

        userModel = fromUserDataToUserModel(userEntity, database)

        CompletableFuture.supplyAsync {
            cache.insert(userEntity)
        }.thenAccept {
            setUserId(userId)
        }
    }

    /**
     * set the user to an existing user
     * @param userId the user id of the new current user
     */
    fun setCurrentUser(userId: String) {
        checkCurrentUser(false)
        CompletableFuture.supplyAsync {
            // insert empty user in cache to avoid null user
            cache.insertIfEmpty(UserEntity(userId))
        }.thenComposeAsync {
            database.getUserAccount(userId)
        }.thenApplyAsync {
            cache.insert(fromUserModelToUserData(it))
        }
        setUserId(userId)
    }

    /**
     * helper function for setting the userId
     * @param userId the user id
     */
    private fun setUserId(userId: String) {
        currentUserID = userId
        _currentUserID.value = userId
    }

    /**
     * get the current user (read-only).
     *
     * Its usage is as follow : `getUser().observe(viewLifecycleOwner) { viewText.text = it.username }`
     * @return the [LiveData] of [UserEntity]
     */
    fun getUser(): LiveData<UserEntity> {
        checkCurrentUser()
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
        // if database is mock then set user to a test user
        if (database is MockDataBase) {
            setCurrentUser(database.userIdTest)
        }
    }

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return database.isUsernameAvailable(userName)
    }

    /**
     * Use this function to modify the username(the username will be modify only if it is available on the database)
     * @param username that we want to set
     */
    fun setUsername(username: String): CompletableFuture<Boolean> {
        checkCurrentUser()
        return CompletableFuture.supplyAsync {
            database.setUsername(username).join()
        }.thenApplyAsync {
            if (it) {
                cache.updateUsername(currentUserID!!, username)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        checkCurrentUser()
        checkDistanceGoal(distanceGoal)
        return CompletableFuture.supplyAsync {
            database.setDistanceGoal(distanceGoal).join()
        }.thenApplyAsync {
            if (it) {
                cache.updateDistanceGoal(currentUserID!!, distanceGoal)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param activityTimeGoal new daily activity time goal
     */
    fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        checkCurrentUser()
        checkActivityTimeGoal(activityTimeGoal)
        return CompletableFuture.supplyAsync {
            database.setActivityTimeGoal(activityTimeGoal).join()
        }.thenApplyAsync {
            if (it) {
                cache.updateTimeGoal(currentUserID!!, activityTimeGoal)
            }
            it
        }
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPathsGoal new daily number of paths goal
     */
    fun setNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        checkCurrentUser()
        checkNbOfPathsGoal(nbOfPathsGoal)
        return CompletableFuture.supplyAsync {
            database.setNbOfPathsGoal(nbOfPathsGoal).join()
        }.thenApplyAsync {
            if (it) {
                cache.updatePathsGoal(currentUserID!!, nbOfPathsGoal)
            }
            it
        }
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        checkCurrentUser()
        return CompletableFuture.supplyAsync {
            database.setProfilePhoto(photo).join()
        }.thenApplyAsync {
            if (it) {
                cache.updatePhoto(currentUserID!!, UserEntity.fromBitmapToByteArray(photo))
            }
            it
        }
    }

    private fun checkCurrentUser(checkIfNull: Boolean = true) {
        if (database is MockDataBase) {
            return //already a test
        }
        if ((checkIfNull && currentUserID == null) || currentUserID == MockAuth.MOCK_USER.getUid()) {
            // if current user null then it is a test
            setDatabase(MockDataBase())
            Toast.makeText(getApplication(), R.string.toast_test_error_message, Toast.LENGTH_LONG).show()
        }
    }

}

/**
 * helper function to create a UserModel from a UserData
 * @param data the userData
 * @param database the database
 * @return the resulting UserModel
 */
private fun fromUserDataToUserModel(data: UserEntity, database: Database): UserModel {
    return UserModel(
        data.userId,
        data.emailAddress,
        data.username,
        data.firstname,
        data.surname,
        data.getDateOfBirthAsLocalDate(),
        data.distanceGoal,
        data.activityTimeGoal,
        data.nbOfPathsGoal,
        data.getProfilePhotoAsBitmap(),
        database
    )
}

/**
 * helper function to create a UserData from a UserModel
 * @param userModel the userModel
 * @return the resulting UserData
 */
private fun fromUserModelToUserData(userModel: UserModel): UserEntity {
    return UserEntity(
        userModel.getUserId(),
        userModel.getUsername(),
        userModel.getEmailAddress(),
        userModel.getFirstname(),
        userModel.getSurname(),
        UserEntity.fromLocalDateToLong(userModel.getDateOfBirth()),
        userModel.getDistanceGoal(),
        userModel.getActivityTime(),
        userModel.getNumberOfPathsGoal(),
        UserEntity.fromBitmapToByteArray(userModel.getProfilePhoto())
    )
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