package com.epfl.drawyourpath.path

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.Exclude
import com.google.type.Color

// Define a Path class that represents a runner's path.
class Path {

    /**
     * Constructor that creates an empty path.
     */
    constructor()

    /**
     * Constructor that creates a path from a list of points.
     */
    constructor(points: List<LatLng>) {
        this.points.addAll(points)
    }

    /**
     * private mutable list to store the path as a collection of points.
     */
    private val points = mutableListOf<LatLng>()

    /**
     * Add a point to the path's list of points(at the end of the list).
     */
    fun addPoint(point: LatLng) {
        points.add(point)
    }

    /**
     * Return an immutable list of the path's points.
     */
    fun getPoints(): List<LatLng> {
        return points.toList()
    }

    /**
     * Clear the path's list of points.
     */
    fun clear() {
        points.clear()
    }

    /**
     * Return the number of points in the path.
     */
    fun size(): Int {
        return points.size
    }

    /**
     * helper function to calculate distance between two points (in meters)
     */
    private fun distance(point1: LatLng, point2: LatLng): Double {
        // Calculate the distance on the WGS84 ellipsoid (for precision)
        var result = FloatArray(1)
        Location.distanceBetween(point1.latitude,point1.longitude, point2.latitude,point2.longitude, result)
        return result[0].toDouble()
    }

    /**
     * function that returns the distance of the path in meters.
     */
    @Exclude
    fun getDistance(): Double {
        var distance = 0.0
        for (i in 0 until points.size - 1) {
            distance += distance(points[i], points[i + 1])
        }
        return distance
    }

    /**
     * Return a Polyline object representing the path.
     */
    @Exclude
    fun getPolyline(): PolylineOptions {
        // Create a new PolylineOptions object to define the appearance of the polyline.
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLUE_FIELD_NUMBER)
        polylineOptions.width(10f)

        // Loop through the list of points and add them to the PolylineOptions object.
        for (point in points) {
            polylineOptions.add(point)
        }

        // Add the polyline to the map and return it.
        return polylineOptions
    }
}
