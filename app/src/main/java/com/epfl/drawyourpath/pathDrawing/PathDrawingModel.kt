package com.epfl.drawyourpath.pathDrawing

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class PathDrawingModel : ViewModel() {

    private var resultingRun = Run(Path(), 0, 1)
    private val _run: MutableLiveData<Run> = MutableLiveData(resultingRun)
    private var startTime: Long? = null
    private var pauseTime: Long? = null
    private var pauseDuration: Duration = Duration.ZERO
    private var lastRunUpdate: Long = 0

    val run: LiveData<Run> = _run
    val points: LiveData<List<LatLng>> = run.map { it.getPath().getPoints() }

    private val handler = Handler(Looper.getMainLooper())

    private val updateHandler = object : Runnable {
        override fun run() {
            updateRun(null)
            handler.postDelayed(this, 500)
        }
    }

    /**
     * get the resulting run
     * @return the run
     */
    fun getRun(): Run {
        return resultingRun
    }

    /**
     * start the run
     */
    fun startRun() {
        resultingRun = Run(Path(), getAndSetStartTime(), startTime!! + 1)
        _run.postValue(resultingRun)
        lastRunUpdate = startTime!!
        handler.post(updateHandler)
    }

    /**
     * update the run with a new point or just the time
     * @param point the new point
     */
    fun updateRun(point: LatLng?) {
        if (runHasStarted() && !isPaused()) {
            resultingRun = resultingRun.let { oldRun ->
                Run(
                    oldRun.getPath().also {
                        if (getCurrentTime() - lastRunUpdate >= TIME_BETWEEN_UPDATES && point != null) {
                            it.addPoint(point)
                            lastRunUpdate = getCurrentTime()
                        }
                    },
                    oldRun.getStartTime(),
                    if (getCurrentTime() <= oldRun.getStartTime()) {
                        oldRun.getStartTime() + 1
                    } else {
                        getCurrentTime()
                    },
                )
            }
            _run.postValue(resultingRun)
        }
    }

    /**
     * pause or resume the run
     */
    fun pauseResumeRun() {
        if (!runHasStarted()) {
            throw Error("cannot pause before the run has started")
        }
        if (!isPaused()) {
            pauseTime = getCurrentTime()
            handler.removeCallbacks(updateHandler)
        } else {
            pauseDuration = pauseDuration.plus(getCurrentTime() - pauseTime!!, ChronoUnit.SECONDS)
            pauseTime = null
            handler.post(updateHandler)
        }
    }

    private fun getAndSetStartTime(): Long {
        startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return startTime!!
    }

    private fun getCurrentTime(): Long {
        return LocalDateTime.now().minus(pauseDuration).toEpochSecond(ZoneOffset.UTC)
    }

    private fun runHasStarted(): Boolean {
        return startTime != null
    }

    private fun isPaused(): Boolean {
        return pauseTime != null
    }

    companion object {
        private const val TIME_BETWEEN_UPDATES: Long = 1
    }
}
