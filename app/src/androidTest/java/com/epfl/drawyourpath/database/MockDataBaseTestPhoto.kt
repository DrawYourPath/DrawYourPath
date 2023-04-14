package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockDataBaseTestPhoto {
    private val photoProfile : Bitmap = Bitmap.createBitmap(14,14, Bitmap.Config.RGB_565)
    private val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    /**
     * Test if the setting of the photo is correctly made in the database
     */
    @Test
    fun setProfilePhotoCorrectly(){
        val database = MockDataBase()
        val isSet = database.setProfilePhoto(photoProfile).get()
        Assert.assertEquals(isSet, true)
        Assert.assertEquals(database.userIdToUserAccount.get(userIdTest)?.getProfilePhoto(), photoProfile)
    }
}