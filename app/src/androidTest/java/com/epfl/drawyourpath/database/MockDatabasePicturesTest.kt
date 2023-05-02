package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.chat.Message
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class MockDatabasePicturesTest {
    private val photoProfile: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)
    private val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"

    /**
     * Test if the setting of the photo is correctly made in the database
     */
    @Test
    fun setProfilePhotoCorrectly() {
        val database = MockDatabase()
        database.setProfilePhoto(userIdTest, photoProfile).get()
        // Assert.assertEquals(database.users[userIdTest].picture, photoProfile)
    }

    /**
     * Test that a picture message is correctly added to a conversation
     */
    @Test
    fun addPictureMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val messageSent = Message.createPictureMessage(senderId, photoProfile, date)
        database.addChatMessage(conversationId, messageSent)
        // check the chat messages list
        Assert.assertEquals(
            listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].chat ?: emptyList()),
            database.chatMessages[conversationId]!!.chat,
        )
        // check the chat preview
        Assert.assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }
}
