package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng

// This is a class for the RecyclerView adapter for messages
class MessagesAdapter(private val messages: List<Message>, private val userId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Using companion object to define constants for different types of views
    companion object {
        private const val VIEW_TYPE_TEXT_INCOMING = 1
        private const val VIEW_TYPE_TEXT_OUTGOING = 2
        private const val VIEW_TYPE_PICTURE = 3
        private const val VIEW_TYPE_RUN_PATH = 4
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        // Return the type of view depending on the type of message content
        return when (message.content) {
            is MessageContent.Text -> if (message.senderId == userId) VIEW_TYPE_TEXT_OUTGOING else VIEW_TYPE_TEXT_INCOMING
            is MessageContent.Picture -> VIEW_TYPE_PICTURE
            is MessageContent.RunPath -> VIEW_TYPE_RUN_PATH
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create view holder depending on the type of view
        return when (viewType) {
            VIEW_TYPE_TEXT_INCOMING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_text_incoming, parent, false)
                TextMessageViewHolder(view)
            }
            VIEW_TYPE_TEXT_OUTGOING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_text_outgoing, parent, false)
                TextMessageViewHolder(view)
            }
            VIEW_TYPE_PICTURE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_picture, parent, false)
                PictureMessageViewHolder(view)
            }
            VIEW_TYPE_RUN_PATH -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.past_run, parent, false)
                RunPathMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        // Fill the holder view with appropriate data based on the type of view
        when (holder.itemViewType) {
            VIEW_TYPE_TEXT_INCOMING, VIEW_TYPE_TEXT_OUTGOING -> {
                val textHolder = holder as TextMessageViewHolder
                val content = message.content as MessageContent.Text
                textHolder.textMessage.text = content.text
            }
            VIEW_TYPE_PICTURE -> {
                val pictureHolder = holder as PictureMessageViewHolder
                val content = message.content as MessageContent.Picture
                pictureHolder.image.setImageBitmap(content.image)
            }
            VIEW_TYPE_RUN_PATH -> {
                val runPathHolder = holder as RunPathMessageViewHolder
                val content = message.content as MessageContent.RunPath

                val run = content.run

                val runCoordinates: List<LatLng> = run.getPath().getPoints().flatten()

                Utils.loadMapImage(holder.itemView.context, runCoordinates, runPathHolder.mapImageView)

                runPathHolder.dateTextView.text = run.getDate()
                runPathHolder.distanceTextView.text = holder.itemView.context.getString(R.string.display_distance).format(Utils.getStringDistance(run.getDistance()))
                runPathHolder.timeTakenTextView.text = holder.itemView.context.getString(R.string.display_duration).format(Utils.getStringDuration(run.getDuration()))
                runPathHolder.shapeRecognizedTextView.text = holder.itemView.context.getString(R.string.display_shape).format(run.predictedShape)
                runPathHolder.shapeScoreTextView.text = holder.itemView.context.getString(R.string.display_score).format(run.similarityScore)
            }
        }
    }

    override fun getItemCount() = messages.size // Returns the total count of items

    // Define different types of ViewHolder classes

    // This ViewHolder represents a View for an incoming or outgoing text message.
    class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
    }

    class PictureMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageMessage)
    }

    class RunPathMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mapImageView: ImageView = itemView.findViewById(R.id.mapImageView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        val timeTakenTextView: TextView = itemView.findViewById(R.id.timeTakenTextView)
        val shapeRecognizedTextView: TextView = itemView.findViewById(R.id.shapeTextView)
        val shapeScoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
    }
}
