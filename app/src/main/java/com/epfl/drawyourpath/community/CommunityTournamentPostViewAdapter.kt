package com.epfl.drawyourpath.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils
import java.util.concurrent.CompletableFuture

/**
 * used in a recycler view to display the [TournamentPost]
 */
class CommunityTournamentPostViewAdapter(
    private val vote: (vote: Int, postId: String, tournamentId: String) -> CompletableFuture<Unit>,
    private val getUsername: (id: String) -> CompletableFuture<String>,
    private val showRunDetail: (run: Run) -> Unit,
) : RecyclerView.Adapter<CommunityTournamentPostViewAdapter.ViewHolder>() {

    private var posts: List<TournamentPost> = mutableListOf()
    private var showName: Boolean = false
    private var userId: String = MockDatabase.mockUser.userId!!
    private val userIdToUsername: MutableMap<String, String> = mutableMapOf()

    /**
     * Custom view holder using a custom layout for tournaments
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tournamentName: TextView
        val userName: TextView
        val imagePath: ImageView
        val upvote: ImageButton
        val downvote: ImageButton
        val voteCount: TextView
        val context: Context
        val cardView: CardView

        init {
            // Define click listener for the ViewHolder's View
            tournamentName = view.findViewById(R.id.tournament_name_display_text)
            userName = view.findViewById(R.id.tournament_user_display_text)
            imagePath = view.findViewById(R.id.tournament_path_display_image)
            upvote = view.findViewById(R.id.tournament_upvote_button)
            downvote = view.findViewById(R.id.tournament_downvote_button)
            voteCount = view.findViewById(R.id.tournament_vote_count_text)
            context = view.context
            cardView = view.findViewById(R.id.tournament_post_card_view)
        }
    }

    // Create new views (invoked by the layout manager )
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_tournaments_posts, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // add an invisible post at the end to be able to scroll past the add post button
        if (viewHolder.adapterPosition >= posts.count()) {
            viewHolder.cardView.visibility = INVISIBLE
            return
        } else {
            viewHolder.cardView.visibility = VISIBLE
        }

        val post = posts[viewHolder.adapterPosition]

        // if it displays posts from only one tournament the name is not displayed
        if (showName) {
            viewHolder.tournamentName.text = post.tournamentName
            viewHolder.tournamentName.visibility = VISIBLE
        } else {
            viewHolder.tournamentName.visibility = GONE
        }

        // show the image
        viewHolder.imagePath.setImageBitmap(Utils.coordinatesToBitmap(post.run.getPath().getPoints()))

        // go to run details if the image is clicked
        viewHolder.imagePath.setOnClickListener {
            showRunDetail(post.run)
        }

        // set the username of the user who posted
        viewHolder.userName.text = userIdToUsername[post.userId] ?: post.userId
        if (!userIdToUsername.containsKey(post.userId)) {
            getUsername(post.userId).whenComplete { it, error ->
                if (error == null && it != null) {
                    userIdToUsername[post.userId] = it
                    viewHolder.userName.text = it
                }
            }
        }

        // display the vote count
        viewHolder.voteCount.text = post.getVotes().toString()

        // update the buttons
        updateButtonColor(post.getUsersVotes()[userId], viewHolder)

        // upvote logic
        viewHolder.upvote.setOnClickListener {
            if (post.getUsersVotes()[userId] != null && post.getUsersVotes()[userId] == 1) {
                updateVote(0, post, viewHolder)
            } else {
                updateVote(1, post, viewHolder)
            }
        }

        // downvote logic
        viewHolder.downvote.setOnClickListener {
            if (post.getUsersVotes()[userId] != null && post.getUsersVotes()[userId] == -1) {
                updateVote(0, post, viewHolder)
            } else {
                updateVote(-1, post, viewHolder)
            }
        }
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = posts.count() + 1

    /**
     * update the recycler view to show the updated list of posts
     * @param posts the new list of posts
     */
    fun update(posts: List<TournamentPost>, showName: Boolean, userId: String) {
        this.posts = posts.sortedBy { it.postId }
        this.showName = showName
        this.userId = userId
        notifyDataSetChanged()
    }

    /**
     * update the vote of the post
     * @param vote the new vote
     * @param post the post where to vote
     * @param viewHolder the view holder
     */
    private fun updateVote(vote: Int, post: TournamentPost, viewHolder: ViewHolder) {
        updateButtonColor(vote, viewHolder)
        viewHolder.voteCount.text = newVoteTotal(post.getVotes(), post.getUsersVotes()[userId], vote).toString()
        vote(vote, post.postId, post.tournamentId)
    }

    /**
     * calculate the new total vote base on the last vote and new vote to display the total before the database is updated
     * @param total the total vote
     * @param currentVote the current vote
     * @param newVote the new vote
     * @return the new total
     */
    private fun newVoteTotal(total: Int, currentVote: Int?, newVote: Int): Int {
        return total - (currentVote ?: 0) + newVote
    }

    /**
     * update the upvote and downvote color based on the current vote (-1 downvote, 0 or null no vote, 1 upvote)
     * @param currentVote the current vote
     * @param viewHolder the viewHolder
     */
    private fun updateButtonColor(currentVote: Int?, viewHolder: ViewHolder) {
        when (currentVote) {
            1 -> changeButtonColor(viewHolder, viewHolder.itemView, R.color.red, R.color.grey)
            -1 -> changeButtonColor(viewHolder, viewHolder.itemView, R.color.grey, R.color.blue)
            else -> changeButtonColor(viewHolder, viewHolder.itemView, R.color.grey, R.color.grey)
        }
    }

    /**
     * change the color of the upvote and downvote button
     *
     * @param holder the viewHolder
     * @param view the View
     * @param upvoteColor the color of the upvote button
     * @param downvoteColor the color of the downvote button
     */
    private fun changeButtonColor(holder: ViewHolder, view: View, upvoteColor: Int, downvoteColor: Int) {
        var wrappedDrawable = DrawableCompat.wrap(holder.upvote.drawable)
        DrawableCompat.setTint(wrappedDrawable, view.resources.getColor(upvoteColor, null))
        wrappedDrawable = DrawableCompat.wrap(holder.downvote.drawable)
        DrawableCompat.setTint(wrappedDrawable, view.resources.getColor(downvoteColor, null))
    }
}
