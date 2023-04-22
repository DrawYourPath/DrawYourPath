package com.epfl.drawyourpath.mainpage.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class DrawFragment : Fragment(R.layout.fragment_draw), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private var currentZoom = DEFAULT_ZOOM
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
        }

        Places.initialize(this.requireActivity().applicationContext, getString(R.string.google_api_key))

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_draw_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                // request location updates
                val locationRequest = LocationRequest.create()
                    .setInterval(5000)
                    .setFastestInterval(2000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        if (locationResult != null) {
                            for (location in locationResult.locations) {
                                val coordinates = LatLng(location.latitude, location.longitude)
                                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, currentZoom))
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.")
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
                            map?.uiSettings?.isMyLocationButtonEnabled = false
                        }
                    }
                }, Looper.getMainLooper())
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        private val TAG = DrawFragment::class.java.simpleName
        private val DEFAULT_LOCATION = LatLng(46.5185, 6.56177)
        private const val DEFAULT_ZOOM = 18F
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_LOCATION = "location"
    }
}