package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.consts.PrefConst
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.events.ReportFilterEvent
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.events.ReportingMapEvent
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private var currentJob: Job? = null

    val mapReportingEvent: SingleLiveEvent<ReportingMapEvent> = SingleLiveEvent()


    val filterReportingEvent: SingleLiveEvent<ReportFilterEvent> = SingleLiveEvent()
    private val currentFilter: MutableLiveData<ReportFilterEvent.ReportFilterPicked> = MutableLiveData()

    val reports : LiveData<List<ReportEntity>> = Transformations.switchMap(currentFilter) { currentFilter ->
        repository.getReports(status = currentFilter.status, hoursAgo = currentFilter.hoursAgo)
    }


    fun setCurrentFilter(filterPicked: ReportFilterEvent.ReportFilterPicked) {
        currentFilter.value = filterPicked
    }

    fun getBicyclePath(boundingBoxQueryParameter: BoundingBoxQueryParameter,
                       onSuccess: (JSONObject) -> Unit,
                       onError: (Throwable) -> Unit) {
        currentJob = launch {
            val result = repository.getBicyclePathGeoJson(boundingBoxQueryParameter = boundingBoxQueryParameter)
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

    fun onStatusFilterButtonClicked() {
        filterReportingEvent.value = ReportFilterEvent.PickStatusFilter(currentFilter.value?.status)
    }

    fun onHoursFilterButtonClicked() {
        filterReportingEvent.value = ReportFilterEvent.PickHoursFilter(currentFilter.value?.hoursAgo ?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
    }

    fun onReportButtonClicked() {
        mapReportingEvent.value = ReportingMapEvent.AddReport
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }



}