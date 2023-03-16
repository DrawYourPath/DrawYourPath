package com.github.drawyourpath.bootcamp.path

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
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
     * helper function to calculate distance between two points(in meters)
     */
    private fun distance(point1: LatLng, point2: LatLng): Double {
        val lat1 = point1.latitude
        val lat2 = point2.latitude
        val lon1 = point1.longitude
        val lon2 = point2.longitude
        val r = 6371e3 // metres
        val a1 = lat1 * Math.PI / 180 // angle in radians
        val a2 = lat2 * Math.PI / 180
        val Da = (lat2 - lat1) * Math.PI / 180
        val Dl = (lon2 - lon1) * Math.PI / 180
        val a = Math.sin(Da / 2) * Math.sin(Da / 2) +
                Math.cos(a1) * Math.cos(a2) *
                Math.sin(Dl / 2) * Math.sin(Dl / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c

    }


    /**
     * function that returns the distance of the path in meters.
     */
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