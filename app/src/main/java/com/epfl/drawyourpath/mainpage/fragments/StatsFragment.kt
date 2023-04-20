package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.google.android.gms.maps.model.LatLng

class StatsFragment : Fragment(R.layout.fragment_stats) {
    // TODO: add Stats UI and ViewModel for this screen/fragment
    //  Leaving Stats (with a button for example) should return to the last fragment,
    //  usually the one select in the bottom menu
    val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.statsTextView).setOnClickListener {
            user.updateGoalProgress(Run(Path(listOf(LatLng(46.518560208122736, 6.561695105191239), LatLng(46.506161355856094, 6.620562197652381))), 0, 1853))
        }
    }

}