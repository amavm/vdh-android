package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.App
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.actions.ReportingAction
import app.vdh.org.vdhapp.data.actions.ReportingViewAction
import app.vdh.org.vdhapp.extenstions.uniqueId
import app.vdh.org.vdhapp.helpers.GoogleMapLinkHelper
import app.vdh.org.vdhapp.helpers.ImageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportingViewModel(application: Application, private val repository: ReportRepository) : AndroidViewModel(application) {

    var reportingViewViewAction: SingleLiveEvent<ReportingViewAction> = SingleLiveEvent()

    var currentReport: MutableLiveData<ReportEntity?> = MutableLiveData()

    var saveOrSyncDate: MutableLiveData<String> = MutableLiveData()

    fun initReport() {
        currentReport.value = ReportEntity(deviceId = getApplication<App>().uniqueId())
    }

    fun setCurrentReport(report: ReportEntity) {
        currentReport.value = report
        val saveOrSyncDateFmt = if (report.sentToSever) R.string.sync_date_fmt else R.string.save_date_fmt
        saveOrSyncDate.value = getApplication<App>().getString(saveOrSyncDateFmt, DateUtils.getRelativeTimeSpanString(report.timestamp).toString())

        if (report.sentToSever && currentReport.value?.comment?.isEmpty() == true) {
            mutateReport { it.copy(comment = getApplication<App>().resources.getString(R.string.no_comment)) }
        }
    }

    fun handleAction(action: ReportingAction) {
        when (action) {
            is ReportingAction.UpdateStatus -> {
                mutateReport { it.copy(status = action.status) }
            }
            is ReportingAction.UpdatePicture -> {
                mutateReport { it.copy(photoPath = action.picture) }
            }
            is ReportingAction.UpdatePlace -> {
                mutateReport { it.copy(name = action.name, position = action.location) }
            }
            is ReportingAction.SaveReport -> saveReport(action.sendToServer)
            is ReportingAction.DeleteReport -> deleteReport()
            is ReportingAction.OpenPhotoGallery -> reportingViewViewAction.value = ReportingViewAction.PickPhoto
            is ReportingAction.OpenPlacePicker -> reportingViewViewAction.value = ReportingViewAction.PickPlace
            is ReportingAction.TakePicture -> reportingViewViewAction.value = ReportingViewAction.TakePhoto
            is ReportingAction.OpenDeleteDialog -> reportingViewViewAction.value = ReportingViewAction.OpenDeleteDialog
            is ReportingAction.EditPlace -> editPlace()
            is ReportingAction.EditPhoto -> editPhoto()
            is ReportingAction.EditStatus -> editStatus()
        }
    }

    private fun saveReport(sendToServer: Boolean = false) {

        if (currentReport.value?.position != null && currentReport.value?.status != null) {

            val report = currentReport.value

            report?.let {
                viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.Default) {
                    val resultPair = repository.saveReport(getApplication(), report, sendToServer = sendToServer)
                    val saveResult = resultPair.first
                    val sendToServerResult = resultPair.second
                    withContext(Dispatchers.Main) {
                        if (saveResult is Result.Success && sendToServerResult is Result.Success) {
                            reportingViewViewAction.value = ReportingViewAction.SaveReportSuccess(getApplication<App>().getString(R.string.send_report_success), sendToServer)
                        } else if (saveResult is Result.Success && !sendToServer) {
                            reportingViewViewAction.value = ReportingViewAction.SaveReportSuccess(getApplication<App>().getString(R.string.save_report_success), sendToServer)
                        } else {
                            reportingViewViewAction.value = ReportingViewAction.SaveReportError(getApplication<App>().getString(R.string.report_error))
                        }
                    }
                }
            }
        } else {
            reportingViewViewAction.value = ReportingViewAction.SaveReportError(getApplication<App>().getString(R.string.mandatory_fields_missing_error))
        }
    }

    private fun deleteReport() {
        currentReport.value?.let {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.Default) {
                val result = repository.deleteReport(it)
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> {
                            reportingViewViewAction.value = ReportingViewAction.DeleteReportSuccess(getApplication<App>().getString(R.string.delete_report_success))
                        }
                        is Result.Error -> {
                            Log.e("ReportingViewModel", result.exception.message)
                            reportingViewViewAction.value = ReportingViewAction.DeleteReportError(getApplication<App>().getString(R.string.report_error))
                        }
                    }
                }
            }
        }
    }

    private fun editPlace() {
        handleAction(ReportingAction.OpenPlacePicker)
    }

    private fun editPhoto() {
        mutateReport { it.copy(photoPath = null) }
    }

    private fun editStatus() {
        mutateReport { it.copy(status = null) }
    }

    fun getShareIntent(): Intent {
        val appContext = getApplication<Application>().applicationContext

        val statusLabelRes = currentReport.value?.status?.labelRes
        val statusLabel = if (statusLabelRes != null) {
            appContext.resources.getString(statusLabelRes)
        } else null

        val shareReportContent = appContext.resources.getString(R.string.share_report_content,
                GoogleMapLinkHelper.getMapUrl(currentReport.value?.position, 20), statusLabel, currentReport.value?.comment)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/jpeg"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, appContext.resources.getString(R.string.share_report_title))
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareReportContent)

        ImageHelper.getSharedImageUri(appContext)?.let { fileUri ->
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        }

        return sharingIntent
    }

    private fun mutateReport(mutator: (ReportEntity) -> ReportEntity) {
        val report = currentReport.value
        report?.let {
            currentReport.value = mutator(report)
        }
    }
}