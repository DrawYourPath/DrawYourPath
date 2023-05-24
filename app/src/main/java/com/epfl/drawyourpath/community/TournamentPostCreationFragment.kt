package com.epfl.drawyourpath.community

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.path.RunArrayAdapter
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

/**
 * this class is used to create a post with a run to a specific tournament and then post it
 */
class TournamentPostCreationFragment : Fragment(R.layout.fragment_tournament_post_creation) {

    private val user: UserModelCached by activityViewModels()

    private val tournament: TournamentModel by activityViewModels()

    private lateinit var tournamentSpinner: Spinner

    private lateinit var runSpinner: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTournamentSpinner(view)

        setupRunSpinner(view)

        setupBackButton(view)

        setupPostButton(view)
    }

    /**
     * setup the tournament spinner to select the tournament
     * @param view the view
     */
    private fun setupTournamentSpinner(view: View) {
        tournamentSpinner = view.findViewById(R.id.post_creation_tournament_spinner)
        val arrayAdapter = TournamentArrayAdapter(requireContext())
        tournamentSpinner.adapter = arrayAdapter
        tournament.yourTournament.observe(viewLifecycleOwner) {
            arrayAdapter.clear()
            arrayAdapter.addAll(it)
        }
    }

    /**
     * setup the run spinner to select the run
     * @param view the view
     */
    private fun setupRunSpinner(view: View) {
        runSpinner = view.findViewById(R.id.post_creation_run_spinner)
        val arrayAdapter = RunArrayAdapter(requireContext())
        runSpinner.adapter = arrayAdapter
        user.getRunHistory().observe(viewLifecycleOwner) {
            arrayAdapter.clear()
            arrayAdapter.addAll(it)
        }
    }

    /**
     * setup the back button to go back to the community fragment
     * @param view the view
     */
    private fun setupBackButton(view: View) {
        val back = view.findViewById<ImageButton>(R.id.post_creation_back_button)
        back.setOnClickListener {
            returnToCommunityFragment()
        }
    }

    /**
     * setup the post button to post the newly created post and then go back to the community fragment
     * @param view the view
     */
    private fun setupPostButton(view: View) {
        val post = view.findViewById<Button>(R.id.post_creation_post_button)
        post.setOnClickListener {
            tournament.addPost(getSelectedTournamentId(), getSelectedRun())
            returnToCommunityFragment()
        }
    }

    /**
     * return to the community fragment
     */
    private fun returnToCommunityFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    /**
     * get the selected tournament id
     * @return the tournament id
     */
    private fun getSelectedTournamentId(): String {
        return (tournamentSpinner.selectedItem as Tournament).id
    }

    /**
     * get the selected run
     * @return the run
     */
    private fun getSelectedRun(): Run {
        return runSpinner.selectedItem as Run
    }
}
