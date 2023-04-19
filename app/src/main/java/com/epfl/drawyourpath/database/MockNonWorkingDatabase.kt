package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.concurrent.CompletableFuture

/**
 * this class is used for testing and will always return failed futures
 */
class MockNonWorkingDatabase : Database() {
    override fun isUserStoredInDatabase(userId: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun getUsernameFromUserId(userId: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun updateUsername(username: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setUsername(username: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        return failedFuture()
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        return failedFuture()
    }

    override fun setCurrentDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setCurrentActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setCurrentNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addUserToFriendsList(userId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addDailyGoal(dailyGoal: DailyGoal): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun updateUserAchievements(distanceDrawing: Double, activityTimeDrawing: Double): CompletableFuture<Unit> {
        return failedFuture()
    }

    private fun <T: Any> failedFuture(): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { throw Error(ERROR_NAME) }
    }

    companion object {
        const val ERROR_NAME = "MockError: No Internet Connection"
    }

}