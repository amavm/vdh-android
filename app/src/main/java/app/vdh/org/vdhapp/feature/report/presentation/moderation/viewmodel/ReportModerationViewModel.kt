package app.vdh.org.vdhapp.feature.report.presentation.moderation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.core.consts.PrefConst
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.core.helpers.SingleLiveEvent
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.SyncReportListUseCase
import app.vdh.org.vdhapp.feature.report.domain.moderation.usecase.GetReportByModerationStatusUseCase
import app.vdh.org.vdhapp.feature.report.domain.moderation.usecase.UpdateModerationStatusUseCase
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationViewAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.uimodel.toReportModerationUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportModerationViewModel(
    private val reportToModerateUseCase: GetReportByModerationStatusUseCase,
    private val syncReportUseCase: SyncReportListUseCase,
    private val updateModerationStatusUseCase: UpdateModerationStatusUseCase
) : ViewModel() {

    val action = SingleLiveEvent<ReportModerationAction>()
    private val filter = MutableLiveData<String>()

    init {
        filter.postValue(ApiConst.MODERATION_STATUS_PENDING)
    }

    val reports = filter.switchMap { selectedFilter ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            val uiModels = reportToModerateUseCase.execute(PrefConst.MAX_HOUR, selectedFilter).map { reports ->
                reports.map { it.toReportModerationUiModel(selectedFilter) }
            }
            emitSource(uiModels)
            syncReportUseCase.execute()
        }
    }

    fun handleViewAction(viewAction: ReportModerationViewAction) {
        when (viewAction) {
            is ReportModerationViewAction.ChangeModerationStatusFilter -> {
                filter.postValue(viewAction.status)
            }
            is ReportModerationViewAction.ChangeModerationStatus -> {

                viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.Default) {
                    val result = updateModerationStatusUseCase.execute(viewAction.reportId, viewAction.newStatus)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is CallResult.Success -> {
                                action.postValue(ReportModerationAction.ReportStatusUpdated(viewAction.reportPosition))
                            }
                            is CallResult.Error -> {
                                action.postValue(ReportModerationAction.ReportStatusUpdateError)
                            }
                        }
                    }
                }
            }
        }
    }
}