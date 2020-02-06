package app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.core.consts.PrefConst
import app.vdh.org.vdhapp.core.helpers.SingleLiveEvent
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetBicyclePathUseCase
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetReportListUseCase
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapAction
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapFilterViewAction
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapViewAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportMapViewModel(
    app: Application,
    reportListUseCase: GetReportListUseCase,
    private val bicyclePathUseCase: GetBicyclePathUseCase
) : AndroidViewModel(app) {

    val mapReportViewAction: SingleLiveEvent<ReportMapViewAction> = SingleLiveEvent()
    val reportMapFilterViewAction: SingleLiveEvent<ReportMapFilterViewAction> = SingleLiveEvent()

    val currentFilterViewActionReport: MutableLiveData<ReportMapFilterViewAction.ReportFilterPickedReport> = MutableLiveData()
    private var currentBikePathNetwork: BikePathNetwork? = null

    val reports: LiveData<List<ReportModel>> = currentFilterViewActionReport.switchMap { currentFilterEvent ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            emitSource(reportListUseCase.execute(
                    status = currentFilterEvent.status,
                    hoursAgo = currentFilterEvent.hoursAgo
            ))
        }
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
            when (val result = bicyclePathUseCase.execute(
                    boundingBoxQueryParameter = boundingBoxQueryParameter,
                    network = network)) {
                is CallResult.Success -> {
                    Log.d("ReportMapViewModel", "Bike Path ok ${result.data}")
                    withContext(Dispatchers.Main) {
                        mapReportViewAction.value = ReportMapViewAction.BicyclePathQuerySuccess(result.data, network)
                    }
                }
                is CallResult.Error -> {
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
        reportMapFilterViewAction.value = ReportMapFilterViewAction.OpenHourFilter(currentFilterViewActionReport.value?.hoursAgo
                ?: PrefConst.HOURS_SORT_DEFAULT_VALUE)
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