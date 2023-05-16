package com.epfl.drawyourpath.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.pathDrawing.PathDrawingModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places

/**
 * This fragment is used to show a map with the Google map api.
 * @param focusedOnPosition if true then the camera automatically track the user position and the map is not scrollable,
 *  if false the camera tracking is not enable and we can scroll on the map
 * @param path if the path is not null, then it will be drawn on the map (and the view will be centered on the middle of the path).
 */
class MapFragment(private val focusedOnPosition: Boolean = true, private val path: Path? = null) :
    Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val pathDrawingModel: PathDrawingModel by activityViewModels()

    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false

    // to know if the map is open for the first time
    private var mapInit = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
        }
        Places.initialize(
            this.requireActivity().applicationContext,
            getString(R.string.google_api_key),
        )
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_draw_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // obtain the privacy location permission
        getLocationPermission()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // set the UI interface in function of focusedOnPosition (displayed location button, enable scroll on map...)
        updateLocationUI()
        // obtain the user position and move the camera in function of focusedOnPosition
        getDeviceLocation()
        // setup the drawing on the map
        setupDrawingOnMap(map)
        // focused on
        val pathReady = path != null && path.getPoints().flatten().isNotEmpty()
        if (pathReady && !focusedOnPosition) {
            val bounds = LatLngBounds.builder()
            path!!.getPoints().flatten().map { bounds.include(it); Log.d("test", it.toString()) }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 5))
            drawStaticPathOnMap(map, path)
        }
    }

    /**
     * Function used to move the camera at a given location
     * @param location where we want to move the camera
     * @param zoom apply to the camera
     */
    private fun moveCameraToPosition(location: LatLng, zoom: Float = DEFAULT_ZOOM) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude,
                ),
                zoom,
            ),
        )
    }

    /**
     * set the dynamic drawing of the map
     * @param map the map
     */
    private fun setupDrawingOnMap(map: GoogleMap) {
        pathDrawingModel.pointsSection.observe(viewLifecycleOwner) { listSection ->
            if (listSection.isNotEmpty()) {
                for (section in listSection) {
                    val polyline = map.addPolyline(PolylineOptions().clickable(false))
                    if (section.isNotEmpty() && section.size > polyline.points.size) {
                        polyline.points = section
                    }
                }
            }
        }
    }

    /**
     * Function used to add the polygon corresponding to the path on the map
     * @param map where the polygon will be displayed
     * @param path will be drawn on the map
     */
    private fun drawStaticPathOnMap(map: GoogleMap, path: Path) {
        for (polyline in path.getPolyline()) {
            map.addPolyline(polyline)
        }
    }

    /**
     * This function is used to ask the privacy localization permission
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    /**
     * This function is used to setup the map ui (like the position button, scroll gesture)
     */
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isScrollGesturesEnabled = !focusedOnPosition
                map?.uiSettings?.isMyLocationButtonEnabled = !focusedOnPosition
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * This function is used to update the last known location of the user and update the camera view if needed
     */
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                // Request the location periodically
                val locationRequest = LocationRequest.create()
                    .setInterval(LOCATION_REQUEST_INTERVAL)
                    .setFastestInterval(LOCATION_REQUEST_MIN_INTERVAL)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            if (locationResult != null) {
                                lastKnownLocation = locationResult.lastLocation
                                val newLoc = LatLng(
                                    locationResult.lastLocation.latitude,
                                    locationResult.lastLocation.longitude,
                                )
                                pathDrawingModel.updateRun(newLoc)
                                // move the camera if we focused the view on the user position or we initiate the map with a null path
                                if ((mapInit && path == null) || focusedOnPosition) {
                                    moveCameraToPosition(location = newLoc, zoom = DEFAULT_ZOOM)
                                }
                                mapInit = false
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.")
                                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
                                map?.uiSettings?.isMyLocationButtonEnabled = false
                            }
                        }
                    },
                    Looper.myLooper(),
                )
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
        private val TAG = MapFragment::class.java.simpleName
        private val DEFAULT_LOCATION = LatLng(46.5185, 6.56177)
        private const val DEFAULT_ZOOM = 18F
        private const val LOCATION_REQUEST_INTERVAL = 5000L
        private const val LOCATION_REQUEST_MIN_INTERVAL = 2500L
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_LOCATION = "location"
    }
}
