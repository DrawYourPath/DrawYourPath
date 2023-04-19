package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import com.epfl.drawyourpath.userProfile.UserModel
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

    override fun updateUsername(username: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setUsername(username: String): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun initUserProfile(userModel: UserModel): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun getUserAccount(userId: String): CompletableFuture<UserModel> {
        return failedFuture()
    }

    override fun getLoggedUserAccount(): CompletableFuture<UserModel> {
        return failedFuture()
    }

    override fun setDistanceGoal(distanceGoal: Double): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setNbOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun setProfilePhoto(photo: Bitmap): CompletableFuture<Boolean> {
        return failedFuture()
    }

    override fun addUserToFriendsList(userId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    override fun removeUserFromFriendlist(userId: String): CompletableFuture<Unit> {
        return failedFuture()
    }

    private fun <T: Any> failedFuture(): CompletableFuture<T> {
        return CompletableFuture<T>().exceptionally { throw Error("No Internet Connection") }
    }

}