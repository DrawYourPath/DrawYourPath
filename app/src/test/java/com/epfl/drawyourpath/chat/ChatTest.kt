package com.epfl.drawyourpath.chat

import android.graphics.Bitmap
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

class ChatTest {

    // Test that messages can be added to the chat and retrieved correctly
    @Test
    fun testAddAndRetrieveMessages() {
        val chat = Chat()

        // Create mock messages for testing
        val mockBitmap = Mockito.mock(Bitmap::class.java)
        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        val pictureMessage = Message.createPictureMessage("sender1", mockBitmap, 2000L)
        val runMessage = Message.createRunPathMessage("sender1", createMockRun(), 3000L)

        // Add messages to the chat
        chat.addMessage(textMessage)
        chat.addMessage(pictureMessage)
        chat.addMessage(runMessage)

        // Retrieve messages from the chat and verify their contents
        val messages = chat.getMessages()
        assertEquals(3, messages.size)
        assertEquals(textMessage, messages[0])
        assertEquals(pictureMessage, messages[1])
        assertEquals(runMessage, messages[2])
    }

    // Test that a message can be removed from the chat using its ID
    @Test
    fun testRemoveMessageById() {
        val chat = Chat()

        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        chat.addMessage(textMessage)

        // Attempt to remove the message from the chat
        val removed = chat.removeMessageById(1000L)

        // Verify that the message was removed successfully
        assertTrue(removed)
        assertEquals(0, chat.getMessages().size)
    }

    // Test that attempting to remove a non-existent message by ID does not affect the chat
    @Test
    fun testRemoveMessageById_notFound() {
        val chat = Chat()

        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        chat.addMessage(textMessage)

        // Attempt to remove a message that doesn't exist in the chat
        val removed = chat.removeMessageById(2000L)

        // Verify that the attempt was unsuccessful and the chat remains unchanged
        assertFalse(removed)
        assertEquals(1, chat.getMessages().size)
    }

    // Test that a message can be retrieved from the chat using its ID
    @Test
    fun testGetMessageById() {
        val chat = Chat()

        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        chat.addMessage(textMessage)

        val retrievedMessage = chat.getMessageById(1000L)
        assertNotNull(retrievedMessage)
        assertEquals(textMessage, retrievedMessage)
    }

    // Test that attempting to retrieve a non-existent message by ID returns null
    @Test
    fun testGetMessageById_notFound() {
        val chat = Chat()

        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        chat.addMessage(textMessage)

        val retrievedMessage = chat.getMessageById(2000L)
        assertNull(retrievedMessage)
    }

    // Test that a text message's content can be updated
    // Test that a text message's content can be updated
    @Test
    fun testUpdateMessageText() {
        val chat = Chat()

        val textMessage = Message.createTextMessage("sender1", "Hello, world!", 1000L)
        chat.addMessage(textMessage)

        val newText = "Hello, updated world!"
        (textMessage.content as MessageContent.Text).updateTextContent(newText)

        val updatedMessage = chat.getMessageById(1000L)
        assertNotNull(updatedMessage)
        assertEquals(newText, (updatedMessage?.content as MessageContent.Text).text)
    }

    // Helper function to create a mock Run instance for testing purposes
    private fun createMockRun(): Run {
        val mockPath = Mockito.mock(Path::class.java)
        Mockito.`when`(mockPath.getDistance()).thenReturn(1000.0)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 1000L * 60 * 10
        return Run(path = mockPath, startTime = startTime, endTime = endTime, duration = endTime - startTime)
    }
}
