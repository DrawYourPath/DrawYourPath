package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import java.util.concurrent.CompletableFuture

/**
 * this class is used for testing and will always return failed futures
 */
class MockNonWorkingDatabase : Database() {
    override fun isUserInDatabase(userId: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun getUsername(userId: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun getUserIdFromUsername(username: String): CompletableFuture<String> {
        return failedFuture()
    }

    override fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setUsername(userId: String, username: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun createUser(userId: String, userData: UserData): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setUserData(userId: String, userData: UserData): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun getUserData(userId: String): CompletableFuture<UserData> {
        return failedFuture()
    }

    override fun setGoals(userId: String, goals: UserGoals): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun setProfilePhoto(userId: String, photo: Bitmap): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeFriend(userId: String, targetFriend: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addRunToHistory(userId: String, run: Run): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeRunFromHistory(userId: String, run: Run): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun addDailyGoal(userId: String, dailyGoal: DailyGoal): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun updateUserAchievements(
        userId: String,
        distanceDrawing: Double,
        activityTimeDrawing: Double
    ): CompletableFuture<Unit> {
        return failedFuture()
    }

    private fun <T : Any> failedFuture(): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { throw Error(ERROR_NAME) }
    }

    companion object {
        const val ERROR_NAME = "MockError: No Internet Connection"
    }
}
