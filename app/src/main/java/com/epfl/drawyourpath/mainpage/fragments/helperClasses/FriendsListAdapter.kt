package com.epfl.drawyourpath.mainpage.fragments.helperClasses
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R

data class Friend(
    val id: String,
    val name: String,
    val profileImage: Int, // Use a drawable resource ID for simplicity. TODO probably change to a bitmap later
    val isFriend: Boolean
)


// This adapter is responsible for displaying the list of friends in a RecyclerView.
class FriendsListAdapter(private val onAddOrRemoveFriendClicked: (Friend, Boolean) -> Unit) :
    RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder>() {

    // This is the list of friends that the adapter will display.
    private var friendsList: List<Friend> = listOf()

    // The ViewHolder class holds references to the views in the item layout.
    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        private val addFriendButton: Button = itemView.findViewById(R.id.add_friend_button)

        /**
         * The bind() function updates the views with the data from the Friend object.
         */
        fun bind(friend: Friend) {
            name.text = friend.name
            profileImage.setImageResource(friend.profileImage)

            if (friend.isFriend) {
                addFriendButton.text = "Unfriend"
                addFriendButton.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.unfriend_button_color)
                addFriendButton.setOnClickListener {
                    onAddOrRemoveFriendClicked(friend, true)
                }
            } else {
                addFriendButton.text = "Add Friend"
                addFriendButton.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.add_friend_button_color)
                addFriendButton.setOnClickListener {
                    onAddOrRemoveFriendClicked(friend, false)
                }
            }
        }
    }

    // onCreateViewHolder is called when a new ViewHolder is needed.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        // Inflate the item_friend.xml layout and create a new ViewHolder with it.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    // onBindViewHolder is called to update a ViewHolder with data from the list.
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friendsList[position])
    }

    // getItemCount returns the number of items in the list.
    override fun getItemCount(): Int = friendsList.size

    /**
     * updateFriendsList is a helper function to update the list of friends and refresh the RecyclerView.
     */
    fun updateFriendsList(newFriendsList: List<Friend>) {
        friendsList = newFriendsList
        notifyDataSetChanged()
    }
}