package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.RunsAdapter
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var runsAdapter: RunsAdapter

    private val userModelCached: UserModelCached by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val runsRecyclerView = view.findViewById<RecyclerView>(R.id.runsRecyclerView)
        runsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        runsAdapter = RunsAdapter(emptyList())
        runsRecyclerView.adapter = runsAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userModelCached.getRunHistory().observe(viewLifecycleOwner) {
            Log.d("test", it.toString())
            runsAdapter.updateRunsData(it)
        }
    }
}
