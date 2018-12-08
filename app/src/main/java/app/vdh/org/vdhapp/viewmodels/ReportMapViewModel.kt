package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.states.ReportingMapActionState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private var currentJob: Job? = null

    val mapReportingEvent: SingleLiveEvent<ReportingMapActionState> = SingleLiveEvent()


    fun getReports() : LiveData<List<ReportEntity>> = repository.getReports()

    fun onReportButtonClicked() {
        mapReportingEvent.value = ReportingMapActionState.AddReport
    }

    fun getBicyclePath(onSuccess: (JSONObject) -> Unit, onError: (Throwable) -> Unit) {
        currentJob = launch {
            val result = repository.getBicyclePathGeoJson()
            when (result) {
                is Result.Success -> {
                    Log.d("ReportMapViewModel", "Bike Path ok ${result.data}")
                    withContext(Dispatchers.Main) {
                        onSuccess(result.data)
                    }
                }
                is Result.Error -> {
                    Log.d("ReportMapViewModel", "Bike Path error ${result.exception}")
                    withContext(Dispatchers.Main) {
                        onError(result.exception)
                    }
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }



}