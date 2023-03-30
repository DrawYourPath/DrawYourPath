package com.epfl.drawyourpath.misc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R

class NewsFragmentElement(private val title: String,
                          private val description: String,
                          private val action1: NewsAction?,
                          private val action2: NewsAction?
                          ) : Fragment(R.layout.fragment_news_element) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(title)
        setDescription(description)
        setAction1(action1)
        setAction2(action2)
    }

    /**
     * Sets the title of the news.
     */
    fun setTitle(title: String) {
        requireView().findViewById<TextView>(R.id.TV_Title).text = title
    }

    /**
     * Sets the description of the news.
     */
    fun setDescription(description: String) {
        requireView().findViewById<TextView>(R.id.TV_Description).text = description
    }

    /**
     * Sets the action of the first (left) button.
     * Passing null hides the button.
     */
    fun setAction1(action: NewsAction?) {
        setAction(action, R.id.BT_Action1)
    }

    /**
     * Sets the action of the second (right) button.
     * Passing null hides the button.
     */
    fun setAction2(action: NewsAction?) {
        setAction(action, R.id.BT_Action2)
    }

    private fun setAction(action: NewsAction?, button: Int) {
        requireView().findViewById<Button>(button).let {
            if (action == null) {
                it.visibility = View.INVISIBLE
            }
            else {
                it.setOnClickListener {
                    action.onClick()
                }
                it.text = action.title
            }
        }
    }
}