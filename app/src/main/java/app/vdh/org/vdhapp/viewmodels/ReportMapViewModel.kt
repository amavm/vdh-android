package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.consts.PrefConst
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.events.MapFilterEvent
import app.vdh.org.vdhapp.data.events.ReportingMapEvent
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app) {

    val mapReportingEvent: SingleLiveEvent<ReportingMapEvent> = SingleLiveEvent()
    val mapFilterEvent: SingleLiveEvent<MapFilterEvent> = SingleLiveEvent()

    val currentFilterEvent: MutableLiveData<MapFilterEvent.ReportFilterPicked> = MutableLiveData()
    private var currentBikePathNetwork: BikePathNetwork? = null

    val reports: LiveData<List<ReportEntity>> = currentFilterEvent.switchMap { currentFilterEvent ->
        repository.getReports(
                status = currentFilterEvent.status,
                hoursAgo = currentFilterEvent.hoursAgo,
                coroutineContext = viewModelScope.coroutineContext)
    }

    fun getBicyclePath(
        boundingBoxQueryParameter: BoundingBoxQueryParameter,
        onSuccess: (JSONObject, BikePathNetwork) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val network = currentBikePathNetwork ?: BikePathNetwork.FOUR_SEASONS
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            when (val result = repository.getBicyclePathGeoJson(
                    boundingBoxQueryParameter = boundingBoxQueryParameter,
                    network = network)) {
                is Result.Success -> {
                    Log.d("ReportMapViewModel", "Bike Path ok ${result.data}")
                    withContext(Dispatchers.Main) {
                        onSuccess(result.data, network)
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

    fun onStatusFilterButtonClicked() {
        mapFilterEvent.value = MapFilterEvent.PickStatusFilter(currentFilterEvent.value?.status)
    }

    fun onHoursFilterButtonClicked() {
        mapFilterEvent.value = MapFilterEvent.PickHoursFilter(currentFilterEvent.value?.hoursAgo ?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
    }

    fun setBikePathNetwork(network: BikePathNetwork? = null) {
        network?.let {
            mapFilterEvent.value = MapFilterEvent.NetworkFilterPicked(network)
            currentBikePathNetwork = network
        } ?: run {
            currentBikePathNetwork?.let {
                val nextNetwork = it.next()
                currentBikePathNetwork = nextNetwork
                mapFilterEvent.value = MapFilterEvent.NetworkFilterPicked(nextNetwork)
            }
        }
    }

    fun onReportButtonClicked() {
        mapReportingEvent.value = ReportingMapEvent.AddReport
    }
}