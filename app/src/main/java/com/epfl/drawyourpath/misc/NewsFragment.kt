package com.epfl.drawyourpath.misc

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.epfl.drawyourpath.R

data class NewsAction(val title: String, val onClick: () -> Unit);
data class News(
    val title: String,
    val description: String,
    val action1: NewsAction? = null,
    val action2: NewsAction? = null
);

class NewsFragment : Fragment(R.layout.fragment_news) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Create an actual news feed
        addNews(News(
            "Michel completed a drawing",
            "Michel completed the drawing \"foobar\" in 2 hours.",
            null,
            NewsAction("SEND CONGRATS") { ->

            }))
        addNews(News(
            "New trophy unlocked",
            "You completed the trophy \"Serial Runner\" by running everyday for a year.",
            NewsAction("DISMISS") { ->

            },
            NewsAction("TO TROPHIES") { ->

            }))
        addNews(News(
            "TheRunner32 completed a drawing",
            "Michel completed the drawing \"cat\" in 1 hour.",
            null,
            NewsAction("SEND CONGRATS") { ->

            }))
    }

    private fun addNews(news: News) {
        parentFragmentManager.beginTransaction()
            .add(R.id.LL_News, NewsFragmentElement(news.title, news. description, news.action1, news.action2))
            .commit()
    }
}