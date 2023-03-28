package com.epfl.drawyourpath.misc

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R

class NewsFragmentElement : Fragment(R.layout.fragment_news_element) {

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