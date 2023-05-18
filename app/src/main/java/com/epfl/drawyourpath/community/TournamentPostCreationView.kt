package com.epfl.drawyourpath.community

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.RunArrayAdapter
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class TournamentPostCreationView : Fragment(R.layout.fragment_tournament_post_creation) {

    private val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTournamentSpinner(view)

        setupRunSpinner(view)

        setupBackButton(view)
    }

    private fun setupTournamentSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.post_creation_tournament_spinner)
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Shape Spear", "Best tournament", "Draw the earth"))
        spinner.adapter = arrayAdapter
    }

    private fun setupRunSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.post_creation_run_spinner)
        val arrayAdapter = RunArrayAdapter(requireContext(), mutableListOf())
        spinner.adapter = arrayAdapter
        user.getRunHistory().observe(viewLifecycleOwner) {
            arrayAdapter.addAll(it)
        }
    }

    private fun setupBackButton(view: View) {
        val back = view.findViewById<ImageButton>(R.id.post_creation_back_button)
        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}