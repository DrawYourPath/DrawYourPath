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
import com.epfl.drawyourpath.challenge.Tournament


/**
 * used in a recycler view to display the [TournamentPost]
 */
class CommunityTournamentPostViewAdapter(private val tournamentPosts: List<Pair<Tournament, TournamentPost>>) :
    RecyclerView.Adapter<CommunityTournamentPostViewAdapter.ViewHolder>() {

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
        val tournamentPost = tournamentPosts[viewHolder.adapterPosition]
        val post = tournamentPost.second

        viewHolder.tournamentName.text = tournamentPost.first.name

        viewHolder.userName.text = post.user

        viewHolder.voteCount.text = post.getVotes().toString()

        viewHolder.upvote.setOnClickListener {
            post.upvote("user")
            changeButtonColor(viewHolder, it, R.color.red, R.color.grey)
            viewHolder.voteCount.text = post.getVotes().toString()
        }

        viewHolder.downvote.setOnClickListener {
            post.downvote("user")
            changeButtonColor(viewHolder, it, R.color.grey, R.color.blue)
            viewHolder.voteCount.text = post.getVotes().toString()
        }


    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = tournamentPosts.count()

    private fun changeButtonColor(holder: ViewHolder, view: View, upvoteColor: Int, downvoteColor: Int) {
        var wrappedDrawable = DrawableCompat.wrap(holder.upvote.drawable)
        DrawableCompat.setTint(wrappedDrawable, view.resources.getColor(upvoteColor, null))
        wrappedDrawable = DrawableCompat.wrap(holder.downvote.drawable)
        DrawableCompat.setTint(wrappedDrawable, view.resources.getColor(downvoteColor, null))
    }

}