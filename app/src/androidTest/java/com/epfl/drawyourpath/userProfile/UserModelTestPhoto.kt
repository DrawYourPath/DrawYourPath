package com.epfl.drawyourpath.userProfile

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.MockDataBase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class UserModelTestPhoto {
    private val userId = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    private val username = "albert"
    private val email = "mockuser@mockdomain.org"
    private val firstname = "Hugo"
    private val surname = "Hof"
    private val dateOfBirth = LocalDate.of(2000, 2, 20)
    private val distanceGoal = 10.0
    private val timeGoal = 60.0
    private val nbOfPaths = 5
    private val database = MockDataBase()
    private val auth = MockAuth.MOCK_USER

    private val photoProfile: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)

    /**
     * Test if a userModel with input correct data is correctly created with null profilePhoto
     */
    @Test
    fun createCorrectUserWithNullPhoto() {
        val user: UserModel = UserModel(
            userId,
            email,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            null,
            emptyList(),
            emptyList(),
            database
        )
        Assert.assertEquals(user.getUserId(), userId)
        Assert.assertEquals(user.getUsername(), username)
        Assert.assertEquals(user.getEmailAddress(), email)
        Assert.assertEquals(user.getFirstname(), firstname)
        Assert.assertEquals(user.getSurname(), surname)
        Assert.assertEquals(user.getDateOfBirth(), dateOfBirth)
        Assert.assertEquals(user.getDistanceGoal(), distanceGoal, 0.00001)
        Assert.assertEquals(user.getActivityTime(), timeGoal, 0.00001)
        Assert.assertEquals(user.getNumberOfPathsGoal(), nbOfPaths)
        Assert.assertEquals(user.getProfilePhoto(), null)
    }

    /**
     * Test if a userModel with input correct data is correctly created with a profilePhoto
     */
    @Test
    fun createCorrectUserWithPhoto() {
        val user: UserModel = UserModel(
            userId,
            email,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            photoProfile,
            emptyList(),
            emptyList(),
            database
        )
        Assert.assertEquals(user.getUserId(), userId)
        Assert.assertEquals(user.getUsername(), username)
        Assert.assertEquals(user.getEmailAddress(), email)
        Assert.assertEquals(user.getFirstname(), firstname)
        Assert.assertEquals(user.getSurname(), surname)
        Assert.assertEquals(user.getDateOfBirth(), dateOfBirth)
        Assert.assertEquals(user.getDistanceGoal(), distanceGoal, 0.00001)
        Assert.assertEquals(user.getActivityTime(), timeGoal, 0.00001)
        Assert.assertEquals(user.getNumberOfPathsGoal(), nbOfPaths)
        Assert.assertEquals(user.getProfilePhoto(), photoProfile)
    }

    /**
     * Test if set a photo will correct modify the userModel
     */
    @Test
    fun setValidProfilePhoto() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        user.setProfilePhoto(photoProfile)
        Assert.assertEquals(user.getProfilePhoto(), photoProfile)
        //check the database
        Assert.assertEquals(
            database.userIdToUserAccount.get(userId)?.getProfilePhoto() ?: 0,
            photoProfile
        )
    }
}