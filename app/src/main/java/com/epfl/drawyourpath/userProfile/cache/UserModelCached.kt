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
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.userProfile.UserModel
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
 *
 * @see <a href="https://github.com/DrawYourPath/DrawYourPath/pull/105">more information</a>
 */
class UserModelCached(application: Application) : AndroidViewModel(application) {

    // database where the user is store online
    private var database: Database = FireDatabase()

    // cached room database for user
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
     * @param userModel the userModel to create the new user
     */
    fun createNewUser(userModel: UserModel): CompletableFuture<Unit> {
        setUserId(userModel.getUserId())
        return CompletableFuture.supplyAsync {
            cache.insert(fromUserModelToUserData(userModel))
        }
    }

    /**
     * set the user to an existing user
     * @param userId the user id of the new current user
     */
    fun setCurrentUser(userId: String): CompletableFuture<Unit> {
        checkCurrentUser(false)
        setUserId(userId)
        return CompletableFuture.supplyAsync {
            // insert empty user in cache to avoid null user
            cache.insertIfEmpty(UserEntity(userId))
        }.thenComposeAsync {
            database.getUserAccount(userId)
        }.thenApplyAsync {
            cache.insert(fromUserModelToUserData(it))
        }
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
     * get the current user id
     * @return the current user id
     */
    fun getUserId(): String? {
        return currentUserID
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
    fun updateUsername(username: String): CompletableFuture<Unit> {
        checkCurrentUser()
        return database.updateUsername(username).thenApplyAsync {
            cache.updateUsername(currentUserID!!, username)
        }
    }

    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun updateDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkDistanceGoal(distanceGoal)
        return database.setCurrentDistanceGoal(distanceGoal).thenApplyAsync {
            cache.updateDistanceGoal(currentUserID!!, distanceGoal)
        }
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param activityTimeGoal new daily activity time goal
     */
    fun updateActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkActivityTimeGoal(activityTimeGoal)
        return database.setCurrentActivityTimeGoal(activityTimeGoal).thenApplyAsync {
            cache.updateTimeGoal(currentUserID!!, activityTimeGoal)
        }
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPathsGoal new daily number of paths goal
     */
    fun updateNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkNbOfPathsGoal(nbOfPathsGoal)
        return database.setCurrentNbOfPathsGoal(nbOfPathsGoal).thenApplyAsync {
            cache.updatePathsGoal(currentUserID!!, nbOfPathsGoal)
        }
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    fun updateProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        checkCurrentUser()
        return database.setProfilePhoto(photo).thenApplyAsync {
            cache.updatePhoto(currentUserID!!, UserEntity.fromBitmapToByteArray(photo))
        }
    }

    /**
     * This function will clear the room database
     */
    fun clearCache(): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            cache.clear()
        }
    }

    private fun checkCurrentUser(checkIfNull: Boolean = true) {
        if (database is MockDataBase || database is MockNonWorkingDatabase) {
            return // already a test
        }
        if ((checkIfNull && currentUserID == null) || currentUserID == MockAuth.MOCK_USER.getUid()) {
            // if current user null then it is a test
            setDatabase(MockDataBase())
            Toast.makeText(getApplication(), R.string.toast_test_error_message, Toast.LENGTH_LONG).show()
        }
    }
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
        userModel.getCurrentDistanceGoal(),
        userModel.getCurrentActivityTime(),
        userModel.getCurrentNumberOfPathsGoal(),
        UserEntity.fromBitmapToByteArray(userModel.getProfilePhoto()),
    )
}
