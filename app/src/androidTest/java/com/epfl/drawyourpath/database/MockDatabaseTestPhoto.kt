package com.epfl.drawyourpath.database

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.utils.Utils
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MockDatabaseTestPhoto {
    private val photoProfile: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)
    private val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"

    @get:Rule
    val executorRule = CountingTaskExecutorRule()

    /**
     * wait for all thread to be done or throw a timeout error
     */
    private fun waitUntilAllThreadAreDone() {
        executorRule.drainTasks(2, TimeUnit.SECONDS)
        Thread.sleep(50)
    }

    /**
     * Test if the setting of the photo is correctly made in the database
     */
    @Test
    fun setProfilePhotoCorrectly() {
        val database = MockDatabase()
        database.setProfilePhoto(userIdTest, photoProfile).get()
        Assert.assertEquals(database.users[userIdTest]?.picture!!, Utils.encodePhotoToString(photoProfile))
        Assert.assertEquals(
            database.users[userIdTest]?.picture!!,
            Utils.encodePhotoToString(
                Utils.decodePhoto(database.users[userIdTest]?.picture!!)!!,
            ),
        )
    }

    /**
     * Test that a picture message is correctly added to a conversation
     */
    @Test
    fun addPictureMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = getCurrentDateTimeInEpochSeconds()
        val messageSent = Message.createPictureMessage(senderId, photoProfile, date)
        database.addChatMessage(conversationId, messageSent)
        val expected = listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].copy().chat?.value ?: emptyList())
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the chat messages list
        Assert.assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the chat preview
        Assert.assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }
}
