package app.vdh.org.vdhapp.core

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.Exception

class MapLocationManager(
    private val context: Context,
    private val lifecycle: Lifecycle,
    val onLocationReceived: (Location, Boolean) -> Unit
) : LifecycleObserver {

    companion object {
        private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        private const val DEFAULT_MAX_WAIT_TIME = 5 * DEFAULT_INTERVAL_IN_MILLISECONDS
    }

    private var lastLocation: Location? = null
    private var enalbed = false
    private val callback: LocationEngineCallback<LocationEngineResult> = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            Log.d("MapLocationManager", "Location callback success ${result?.locations}")
            result?.lastLocation?.let {
                onLocationReceived(it, lastLocation == null)
                lastLocation = result.lastLocation
            }
        }

        override fun onFailure(exception: Exception) {
            Log.d("MapLocationManager", "Location callback error $exception")
        }
    }

    private val locationEngine = LocationEngineProvider.getBestLocationEngine(context)

    init {
        lifecycle.addObserver(this)
    }

    fun enable() {
        enalbed = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            connect()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if (enalbed) {
            connect()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        locationEngine.removeLocationUpdates(callback)
    }

    private fun connect() {
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine.requestLocationUpdates(request, callback, context.mainLooper)
        locationEngine.getLastLocation(callback)
    }
}