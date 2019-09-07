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
import app.vdh.org.vdhapp.data.actions.ReportMapAction
import app.vdh.org.vdhapp.data.actions.ReportMapFilterViewAction
import app.vdh.org.vdhapp.data.actions.ReportMapViewAction
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app) {

    val mapReportViewAction: SingleLiveEvent<ReportMapViewAction> = SingleLiveEvent()
    val reportMapFilterViewAction: SingleLiveEvent<ReportMapFilterViewAction> = SingleLiveEvent()

    val currentFilterViewActionReport: MutableLiveData<ReportMapFilterViewAction.ReportFilterPickedReport> = MutableLiveData()
    private var currentBikePathNetwork: BikePathNetwork? = null

    val reports: LiveData<List<ReportEntity>> = currentFilterViewActionReport.switchMap { currentFilterEvent ->
        repository.getReports(
                status = currentFilterEvent.status,
                hoursAgo = currentFilterEvent.hoursAgo,
                coroutineContext = viewModelScope.coroutineContext)
    }

    fun handleAction(action: ReportMapAction) {
        when (action) {
            is ReportMapAction.CreateReport -> onCreateReportButtonClicked()
            is ReportMapAction.GetBicyclePath -> getBicyclePath(action.boundingBoxQueryParameter)
            is ReportMapAction.FilterByHour -> onHoursFilterButtonClicked()
            is ReportMapAction.FilterByStatus -> onStatusFilterButtonClicked()
            is ReportMapAction.ChangeBikePath -> setBikePathNetwork(action.network)
        }
    }

    private fun getBicyclePath(boundingBoxQueryParameter: BoundingBoxQueryParameter) {
        val network = currentBikePathNetwork ?: BikePathNetwork.FOUR_SEASONS
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            when (val result = repository.getBicyclePathGeoJson(
                    boundingBoxQueryParameter = boundingBoxQueryParameter,
                    network = network)) {
                is Result.Success -> {
                    Log.d("ReportMapViewModel", "Bike Path ok ${result.data}")
                    withContext(Dispatchers.Main) {
                        mapReportViewAction.value = ReportMapViewAction.BicyclePathQuerySuccess(result.data, network)
                    }
                }
                is Result.Error -> {
                    Log.d("ReportMapViewModel", "Bike Path error ${result.exception}")
                    withContext(Dispatchers.Main) {
                        mapReportViewAction.value = ReportMapViewAction.BicyclePathQueryError(result.exception)
                    }
                }
            }
        }
    }

    private fun onStatusFilterButtonClicked() {
        reportMapFilterViewAction.value = ReportMapFilterViewAction.OpenStatusFilter(currentFilterViewActionReport.value?.status)
    }

    private fun onHoursFilterButtonClicked() {
        reportMapFilterViewAction.value = ReportMapFilterViewAction.OpenHourFilter(currentFilterViewActionReport.value?.hoursAgo ?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
    }

    private fun setBikePathNetwork(network: BikePathNetwork? = null) {
        network?.let {
            reportMapFilterViewAction.value = ReportMapFilterViewAction.RefreshPathNetwork(network)
            currentBikePathNetwork = network
        } ?: run {
            currentBikePathNetwork?.let {
                val nextNetwork = it.next()
                currentBikePathNetwork = nextNetwork
                reportMapFilterViewAction.value = ReportMapFilterViewAction.RefreshPathNetwork(nextNetwork)
            }
        }
    }

    private fun onCreateReportButtonClicked() {
        mapReportViewAction.value = ReportMapViewAction.OpenReportCreation
    }
}