package com.epfl.drawyourpath.misc

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
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


    }

    private fun addNews(news: News) {
        val root = requireView().findViewById<LinearLayout>(R.id.LL_News)
        val newsFragmentElement = NewsFragmentElement()
        newsFragmentElement.setTitle(news.title)
        newsFragmentElement.setDescription(news.description)
        newsFragmentElement.setAction1(news.action1)
        newsFragmentElement.setAction2(news.action2)
        root.addView(NewsFragmentElement().view)
    }
}