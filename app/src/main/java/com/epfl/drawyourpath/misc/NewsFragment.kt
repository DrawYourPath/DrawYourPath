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
    }

    fun addNews(news: News) {
        parentFragmentManager.beginTransaction()
            .add(R.id.LL_News, NewsFragmentElement(news.title, news. description, news.action1, news.action2))
            .commit()
    }
}