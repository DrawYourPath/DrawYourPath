package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import org.junit.Assert
import org.junit.Test

class MockDataBaseTestPhoto {
    private val photoProfile : Bitmap = Bitmap.createBitmap(14,14, Bitmap.Config.RGB_565)
    private val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    /**
     * Test if the number of paths goal is correctly set
     */
    @Test
    fun setNbOfPathsGoalValid(){
        val database = MockDataBase()
        val isSet = database.setProfilePhoto(photoProfile).get()
        Assert.assertEquals(isSet, true)
        Assert.assertEquals(database.userIdToUserAccount.get(userIdTest)?.getProfilePhoto(), photoProfile)
    }
}