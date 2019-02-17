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
import app.vdh.org.vdhapp.data.models.Status
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private var currentJob: Job? = null

    val mapReportingEvent: SingleLiveEvent<ReportingMapEvent> = SingleLiveEvent()

    val filterReportingEvent: SingleLiveEvent<ReportFilterEvent> = SingleLiveEvent()
    private val currentStatusFilter: MutableLiveData<Status> = MutableLiveData()
    private val currentHoursAgoFilter: MutableLiveData<Int> = MutableLiveData()

    val reports: MediatorLiveData<List<ReportEntity>> = MediatorLiveData()

    private val reportsByStatus : LiveData<List<ReportEntity>> = Transformations.switchMap(currentStatusFilter) { statusFilter ->
        repository.getReports(status = statusFilter, hoursAgo = currentHoursAgoFilter.value?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
    }

    private val reportsByHours : LiveData<List<ReportEntity>> = Transformations.switchMap(currentHoursAgoFilter) { hoursFilter ->
        repository.getReports(status = currentStatusFilter.value, hoursAgo = hoursFilter)
    }

    fun initReportMediatorLiveDataSources() {
        reports.addSource(reportsByStatus, ::setReports)
        reports.addSource(reportsByHours, ::setReports)
    }

    private fun setReports(newReports :List<ReportEntity>) {
        reports.value = newReports
    }


    fun onReportButtonClicked() {
        mapReportingEvent.value = ReportingMapEvent.AddReport
    }

    fun onStatusFilterButtonClicked() {
        filterReportingEvent.value = ReportFilterEvent.PickStatusFilter(currentStatusFilter.value)
    }

    fun onHoursFilterButtonClicked() {
        filterReportingEvent.value = ReportFilterEvent.PickHoursFilter(currentHoursAgoFilter.value ?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
    }

    fun setStatusFilter(status: Status?) {
        currentStatusFilter.value = status
    }

    fun setHoursAgoFilter(hoursAgo: Int) {
        currentHoursAgoFilter.value = hoursAgo
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

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }



}