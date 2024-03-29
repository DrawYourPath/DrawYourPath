package com.epfl.drawyourpath.pathDrawing

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.utils.Utils.getCurrentDateTimeInEpochSeconds
import com.google.android.gms.maps.model.LatLng
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class PathDrawingModel : ViewModel() {

    private var resultingRun = Run(path = Path(), startTime = 0, duration = 0L, endTime = 1)
    private val _run: MutableLiveData<Run> = MutableLiveData(resultingRun)
    private var startTime: Long? = null
    private var pauseTime: Long? = null
    private var pauseDuration: Duration = Duration.ZERO
    private var lastRunUpdate: Long = 0
    private var timeIntervalPointAdded: Long = 1

    val run: LiveData<Run> = _run
    val pointsSection: LiveData<List<List<LatLng>>> = run.map { it.getPath().getPoints() }

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
        startTime = null
        pauseTime = null
        pauseDuration = Duration.ZERO
        resultingRun = Run(path = Path(), startTime = getAndSetStartTime(), duration = 0, endTime = startTime!! + 1)
        _run.postValue(resultingRun)
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
                    path = oldRun.getPath().also {
                        if (getCurrentTime() - lastRunUpdate >= timeIntervalPointAdded && point != null) {
                            it.addPointToLastSection(point)
                            lastRunUpdate = getCurrentTime()
                        }
                    },
                    startTime = oldRun.getStartTime(),
                    duration = if (getCurrentTime() > oldRun.getStartTime() + oldRun.getDuration()) {
                        oldRun.getDuration() + 1
                    } else {
                        oldRun.getDuration()
                    },
                    endTime = getCurrentDateTimeInEpochSeconds(),
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
            resultingRun = resultingRun.let { oldRun ->
                Run(
                    path = oldRun.getPath().also {
                        it.addNewSection()
                        lastRunUpdate = getCurrentTime()
                    },
                    startTime = oldRun.getStartTime(),
                    duration = oldRun.getDuration(),
                    endTime = getCurrentDateTimeInEpochSeconds(),
                )
            }
            _run.postValue(resultingRun)
        }
    }

    /**
     * set the minimal interval between each points added
     */
    fun setNewTimeInterval(interval: Long) {
        timeIntervalPointAdded = interval
    }

    /**
     * clear the run
     */
    fun clearRun() {
        handler.removeCallbacks(updateHandler)
        resultingRun = Run(path = Path(), startTime = 0, duration = 0L, endTime = 1)
        _run.postValue(resultingRun)
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
}
