package com.epfl.drawyourpath.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.Transliterator.Position
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places

class MapFragment(private val showCurrentPosition: Boolean = true, private val path: Path? = null) : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
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

        getLocationPermission()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if(showCurrentPosition){
            updateLocationUI()
            getDeviceLocation()
        }else{
            //show the middle point of the path if the path is not null
            if(path != null){
                val middlePoint = path.getPoints().get(path.size()/2)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(middlePoint.latitude, middlePoint.longitude), DEFAULT_ZOOM))
            }
        }
        if(path != null){
            //add the path on the map if the run
            drawPathOnMap(map, path)
        }
    }

    /**
     * Function used to add the polygon corresponding to the path on the map
     * @param map where the polygon will be displayed
     * @param path will be drawed on the map
     */
    private fun drawPathOnMap(map: GoogleMap, path: Path){
        val listLng = ArrayList<LatLng>()
        for(coord in path.getPoints()){
            listLng.add(LatLng(coord.latitude, coord.longitude))
        }
        map.addPolyline(PolylineOptions().clickable(false).addAll(listLng))
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    updateLocationUI()
                }
            }
        }
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
                getDeviceLocation()
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                // Request the location periodically
                val locationRequest = LocationRequest.create()
                    .setInterval(LOCATION_REQUEST_INTERVAL)
                    .setFastestInterval(LOCATION_REQUEST_MIN_INTERVAL)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        if (locationResult != null) {
                            var coordinates = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM))
                            lastKnownLocation = locationResult.lastLocation
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
        private val TAG = MapFragment::class.java.simpleName
        private val DEFAULT_LOCATION = LatLng(46.5185, 6.56177)
        private const val DEFAULT_ZOOM = 18F
        private const val LOCATION_REQUEST_INTERVAL = 5000L
        private const val LOCATION_REQUEST_MIN_INTERVAL = 2500L
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_LOCATION = "location"
    }
}
