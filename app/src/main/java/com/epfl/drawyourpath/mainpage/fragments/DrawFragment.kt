package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DrawFragment : Fragment(R.layout.fragment_draw), OnMapReadyCallback {
    // TODO: add Google Map UI here and the ViewModel for this screen/fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_draw) as SupportMapFragment
        mapFragment.getMapAsync(this)
        */
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onMapReady(p0: GoogleMap) {
        val coordinates = LatLng(0.0,0.0)
        p0.addMarker(
            MarkerOptions()
                .position(coordinates)
                .title("Test marker")
        )
        p0.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
    }
}