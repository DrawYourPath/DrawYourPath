package com.epfl.drawyourpath.chat


import android.graphics.Bitmap
import com.epfl.drawyourpath.path.Run

/**
 * A sealed class representing the content of a chat message.
 * There are three types of message content: Text, Picture, and RunPath.
 */
sealed class MessageContent {
    /**
     * Represents a text message.
     *
     * @property text The text content of the message.
     */
    data class Text(var text: String) : MessageContent() {
        /**
         * Update the content of a text message
         *
         * @param newText The new text content for the message
         */
        fun updateTextContent(newText: String) {
            text = newText
        }
    }

    /**
     * Represents an image message.
     *
     * @property image The Bitmap image of the message.
     */
    data class Picture(val image: Bitmap) : MessageContent()

    /**
     * Represents a run path message.
     *
     * @property run The Run object containing the run path data.
     */
    data class RunPath(val run: Run) : MessageContent()
}

/**
 * A class representing a chat message.
 *
 * @property id The unique identifier for the message.
 * @property senderId The unique identifier for the sender of the message.
 * @property content The MessageContent object representing the content of the message.
 * @property timestamp The timestamp of the message (in milliseconds since epoch).
 */
data class Message(
    val id: Long,
    val senderId: String,
    val content: MessageContent,
    val timestamp: Long
) {
    companion object {
        /**
         * Factory method for creating a text message.
         *
         * @param senderId The unique identifier for the sender of the message.
         * @param text The text content of the message.
         * @param timestamp The timestamp of the message and also used as the id of the message (in milliseconds since epoch).
         * @return A Message object with the specified parameters and MessageType.Text.
         */
        fun createTextMessage(senderId: String, text: String, timestamp: Long): Message {
            return Message(timestamp, senderId, MessageContent.Text(text), timestamp)
        }



        /**
         * Factory method for creating an image message.
         *
         * @param senderId The unique identifier for the sender of the message.
         * @param image The Bitmap image of the message.
         * @param timestamp The timestamp of the message and also used as the id of the message (in milliseconds since epoch).
         * @return A Message object with the specified parameters and MessageType.Picture.
         */
        fun createPictureMessage(senderId: String, image: Bitmap, timestamp: Long): Message {
            return Message(timestamp, senderId, MessageContent.Picture(image), timestamp)
        }

        /**
         * Factory method for creating a run path message.
         *
         * @param senderId The unique identifier for the sender of the message.
         * @param run The Run object containing the run path data.
         * @param timestamp The timestamp of the message and also used as the id of the message (in milliseconds since epoch).
         * @return A Message object with the specified parameters and MessageType.RunPath.
         */
        fun createRunPathMessage(senderId: String, run: Run, timestamp: Long): Message {
            return Message(timestamp, senderId, MessageContent.RunPath(run), timestamp)
        }
    }
}

/**
 * A class representing a chat conversation.
 */
class Chat {
    /**
     * A mutable list of Message objects representing the messages in the chat.
     */
    private val messages: MutableList<Message> = mutableListOf()

    /**
     * Adds a message to the chat.
     *
     * @param message The Message object to be added to the chat.
     */
    fun addMessage(message: Message) {
        messages.add(message)
    }

    /**
     * Returns a list of all messages in the chat.
     *
     * @return A List of Message objects representing all messages in the chat.
     */
    fun getMessages(): List<Message> {
        return messages
    }

    /**
     * Retrieves a message by its unique identifier.
     *
     * @param id The unique identifier of the message to retrieve.
     * @return The Message object with the specified ID, or null if not found.
     */
    fun getMessageById(id: Long): Message? {
        return messages.find { it.id == id }
    }

    /**
     * Removes a message by its unique identifier.
     *
     * @param id The unique identifier of the message to remove.
     * @return A Boolean indicating whether the message was successfully removed.
     */
    fun removeMessageById(id: Long): Boolean {
        return messages.removeIf { it.id == id }
    }
}