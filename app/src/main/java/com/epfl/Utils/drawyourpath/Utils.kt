package com.epfl.Utils.drawyourpath

import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.CompletableFuture

/**
 * A class containing various utility functions.
 */
object Utils {
    /**
     * A function that generates a static map URL with the user's run data
     */
    fun getStaticMapUrl(runCoordinates: List<LatLng>, apiKey: String): String {
        var path = "path=color:0x0000ff|weight:5"
        for (coordinate in runCoordinates) {
            path += "|${coordinate.latitude},${coordinate.longitude}"
        }

        val url = "https://maps.googleapis.com/maps/api/staticmap?size=80x80&maptype=roadmap&$path&key=$apiKey"
        return url
    }

    fun <T> failedFuture(throwable: Throwable): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        future.completeExceptionally(throwable)
        return future
    }
}
