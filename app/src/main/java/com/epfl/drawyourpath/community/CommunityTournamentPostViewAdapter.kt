package com.epfl.drawyourpath.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R

/**
 * used in a recycler view to display the [TournamentPost]
 */
class CommunityTournamentPostViewAdapter : RecyclerView.Adapter<CommunityTournamentPostViewAdapter.ViewHolder>() {

    private var posts: List<TournamentPost> = listOf()
    private var tournamentName: String? = null

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

        init {
            // Define click listener for the ViewHolder's View
            tournamentName = view.findViewById(R.id.tournament_name_display_text)
            userName = view.findViewById(R.id.tournament_user_display_text)
            imagePath = view.findViewById(R.id.tournament_path_display_image)
            upvote = view.findViewById(R.id.tournament_upvote_button)
            downvote = view.findViewById(R.id.tournament_downvote_button)
            voteCount = view.findViewById(R.id.tournament_vote_count_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.display_list_tournaments_posts, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val post = posts[viewHolder.adapterPosition]

        // if it displays posts from only one tournament the name is not displayed
        if (tournamentName != null) {
            viewHolder.tournamentName.text = tournamentName
        } else {
            viewHolder.tournamentName.visibility = View.GONE
        }

        // TODO change image(run) path of the post

        viewHolder.userName.text = post.userId

        viewHolder.voteCount.text = post.getVotes().toString()

        viewHolder.upvote.setOnClickListener {
            if (post.upvote("user")) {
                changeButtonColor(viewHolder, it, R.color.red, R.color.grey)
            } else {
                changeButtonColor(viewHolder, it, R.color.grey, R.color.grey)
            }
            viewHolder.voteCount.text = post.getVotes().toString()
        }

        viewHolder.downvote.setOnClickListener {
            if (post.downvote("user")) {
                changeButtonColor(viewHolder, it, R.color.grey, R.color.blue)
            } else {
                changeButtonColor(viewHolder, it, R.color.grey, R.color.grey)
            }

            viewHolder.voteCount.text = post.getVotes().toString()
        }
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = posts.count()

    // TODO change only the posts that got changed
    /**
     * update the recycler view to show the updated list of posts
     * @param posts the new list of posts
     */
    fun update(posts: List<TournamentPost>, tournamentName: String? = null) {
        this.posts = posts
        this.tournamentName = tournamentName
        notifyDataSetChanged()
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
