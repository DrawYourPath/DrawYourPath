package com.epfl.drawyourpath.pathDrawing

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.ZoneOffset

class PathDrawingModel : ViewModel() {

    private val _run: MutableLiveData<Run> = MutableLiveData(Run(Path(), 0, 1))
    val run: LiveData<Run> = _run
    var startTime: Long? = null
    var pauseTime: Long? = null

    fun startRun() {
        _run.postValue(Run(Path(), getAndSetStartTime(), getCurrentTime()))
    }

    fun updateRun(point: LatLng) {
        checkRunHasStarted()
        _run.modifyValue {
            Run(getPath().also { it.addPoint(point) }, getStartTime(), getCurrentTime())
        }
    }

    fun pauseResumeRun() {
        checkRunHasStarted()
        if (pauseTime != null) {
            pauseTime = getCurrentTime()
        } else {
            // TODO
        }
    }

    private fun getAndSetStartTime(): Long {
        startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return startTime!!
    }

    private fun getCurrentTime(): Long {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    }

    private fun checkRunHasStarted() {
        if (startTime == null) {
            throw Error("cannot pause before the run has started")
        }
    }

    @MainThread
    private fun <T> MutableLiveData<T>.modifyValue(transform: T.() -> T) {
        this.value = this.value?.run(transform)
    }
}
