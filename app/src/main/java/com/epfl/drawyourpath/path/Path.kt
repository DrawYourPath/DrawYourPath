package com.epfl.drawyourpath.path

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.Exclude
import com.google.type.Color

// Define a Path class that represents a runner's path.
class Path {
    /**
     * private mutable list to store the sections of the paths, and a section is composed of a list of points.
     * A new section is begin each time the user made a pause during his drawing activity.
     */
    private val pointsSections: MutableList<MutableList<LatLng>> = mutableListOf(mutableListOf<LatLng>())

    /**
     * Constructor that creates an empty path, composed of one empty section.
     */
    constructor()

    /**
     * Constructor that creates a path from a list of sections composed of multiple points.
     * @param pointsSections the list of sections that composed the path
     */
    constructor(pointsSections: List<List<LatLng>>) {
        pointsSections.forEachIndexed{ index, section ->
            if(index == 0){
                this.pointsSections[index].addAll(section)
            }else{
                this.pointsSections.add(section.toMutableList())
            }
        }
    }

    /**
     * Add a point to the last section of the path(so this function doesn't create a new section in the path).
     * This function is used when the user is currently drawing without restarting a drawing just after a pause.
     * @param point the point that we want to add to the last section.
     */
    fun addPointToLastSection(point: LatLng) {
        this.pointsSections[this.pointsSections.lastIndex].add(point)
    }

    /**
     * This function is used to create a new section in a path.
     * This function is used when the user resume his run just after a pause in his drawing session.
     */
    fun addNewSection() {
        this.pointsSections.add(mutableListOf())
    }

    /**
     * Return an immutable list of the path composed of multiple section, and section is composed of multiple points.
     * A new section of path have been created each time the user has made a pause.
     */
    fun getPoints(): List<List<LatLng>> {
        return pointsSections.toList()
    }

    /**
     * Return an immutable list of the points that composed the section at the given index.
     * @param index of the section that we would like to retrieves the points
     */
    fun getPointsSection(index: Int): List<LatLng> {
        if(index >= this.pointsSections.size){
            throw Error("this index is greater than the number of section of this path.")
        }
        return pointsSections[index].toList()
    }

    /**
     * Clear the path's list of section composed of points.
     */
    fun clear() {
        pointsSections.clear()
    }

    /**
     * Return the number of points in the path.
     */
    fun size(): Int {
        var size = 0
        for(section in this.pointsSections){
            size += section.size
        }
        return size
    }

    /**
     * Return the number of points that composed the section at the given index
     * @param index of the section that we would like to know the number of points
     */
    fun sizeOfSection(index: Int): Int{
        if(index >= this.pointsSections.size){
            throw Error("this index is greater than the number of section of this path.")
        }
        return this.pointsSections[index].size
    }

    /**
     * helper function to calculate distance between two points (in meters)
     */
    private fun distance(point1: LatLng, point2: LatLng): Double {
        // Calculate the distance on the WGS84 ellipsoid (for precision)
        val result = FloatArray(1)
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, result)
        return result[0].toDouble()
    }

    /**
     * function that returns the distance of the path in meters.
     */
    @Exclude
    fun getDistance(): Double {
        var distance = 0.0
        this.pointsSections.forEachIndexed{index, elem ->
            distance += getDistanceInSection(index)
        }
        return distance
    }

    /**
     * function that returns the distance in the section at the given index
     * @param index of the section that we would like to know the distance throw
     */
    fun getDistanceInSection(index: Int): Double{
        var distance = 0.00
        if(index >= this.pointsSections.size){
            throw Error("this index is greater than the number of section of this path.")
        }
        val section = this.pointsSections[index]
        for (i in 0 until section.size - 1) {
            distance += distance(section[i], section[i + 1])
        }
        return distance
    }

    /**
     * Return a list of Polyline object representing the path(each polyline represent a section).
     */
    @Exclude
    fun getPolyline(): List<PolylineOptions> {
        val list = mutableListOf<PolylineOptions>()
        this.pointsSections.forEachIndexed{ index, _ ->
            list.add(getPolylineInSection(index))
        }
        return list
    }
    /**
     * Return the polyline object representing the path of the section at the given index
     * @param index of the section that we would like to get the polyline object
     */
    fun getPolylineInSection(index: Int): PolylineOptions {
        if(index >= this.pointsSections.size){
            throw Error("this index is greater than the number of section of this path.")
        }
        // Create a new PolylineOptions object to define the appearance of the polyline.
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLUE_FIELD_NUMBER)
        polylineOptions.width(10f)

        // Loop through the list of points and add them to the PolylineOptions object.
        for (point in this.pointsSections[index]) {
            polylineOptions.add(point)
        }

        // Add the polyline to the map and return it.
        return polylineOptions
    }
}
