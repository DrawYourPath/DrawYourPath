package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R

/**
 * This fragment is used to display the form description of the path and the score associated to it.
 * @param formName the name of the form recognized on the path
 * @param score the core gove to the form recognized
 */
class FormPathDescriptionFragment(private val formName: String, private val score: Int) : Fragment(R.layout.fragment_form_path_description) {
    private lateinit var formDescriptionText: TextView
    private lateinit var scoreText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the view elements with the parameters of the fragments
        initViewElements(view)
    }

    /**
     * Helper function to init the different elements of the view.
     * @param view where the different elements are displayed
     */
    private fun initViewElements(view: View) {
        this.formDescriptionText = view.findViewById(R.id.formDescriptionPath)
        this.formDescriptionText.text = "${getString(R.string.form_recognized_on_the_path_drawn)} $formName"
        this.scoreText = view.findViewById(R.id.scorePath)
        this.scoreText.text = "${getString(R.string.score_of_the_form_recognized)} $score"
    }
}
