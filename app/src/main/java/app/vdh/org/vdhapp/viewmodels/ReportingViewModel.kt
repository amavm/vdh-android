package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.App
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.events.ReportingEvent
import app.vdh.org.vdhapp.data.models.Status
import app.vdh.org.vdhapp.extenstions.uniqueId
import app.vdh.org.vdhapp.helpers.GoogleMapLinkHelper
import app.vdh.org.vdhapp.helpers.ImageHelper
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ReportingViewModel(application: Application, private val repository: ReportRepository) : AndroidViewModel(application), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    var reportingEvent: SingleLiveEvent<ReportingEvent> = SingleLiveEvent()

    private var currentReport: ReportEntity? = null

    var placeName: MutableLiveData<String> = MutableLiveData()
    var placeLocation: MutableLiveData<LatLng> = MutableLiveData()
    val picturePath: MutableLiveData<String> = MutableLiveData()
    val reportComment: MutableLiveData<String> = MutableLiveData()
    var saveOrSyncDate: MutableLiveData<String> = MutableLiveData()
    var status : MutableLiveData<Status> = MutableLiveData()
    var isReportSentToServer : MutableLiveData<Boolean> = MutableLiveData()

    var placePickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()
    var photoPickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()
    var statusPickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()

    private var saveReportJob: Job? = null
    private var deleteReportJob: Job? = null

    init {
        isReportSentToServer.value = false
    }

    fun onPlacePickerButtonCLicked() {
        reportingEvent.value = ReportingEvent.PickPlace
    }

    fun onPhotoFromGalleryButtonClicked() {
        reportingEvent.value = ReportingEvent.PickPhoto
    }

    fun onTakePhotoButtonClicked() {
        reportingEvent.value = ReportingEvent.TakePhoto
    }

    fun onStatusSelected(selectedStatus: Status) {
        status.value = selectedStatus
        setStatusEditButton()
    }

    fun setReportData(report: ReportEntity) {
        currentReport = report
        isReportSentToServer.value = report.sentToSever
        placeName.value = report.name
        placeLocation.value = report.position
        picturePath.value = report.photoPath
        reportComment.value = report.comment
        val saveOrSyncDateFmt = if (report.sentToSever) R.string.sync_date_fmt else R.string.save_date_fmt
        saveOrSyncDate.value = getApplication<App>().getString(saveOrSyncDateFmt, DateUtils.getRelativeTimeSpanString(report.timestamp).toString())
        status.value = report.status
        if (!report.sentToSever) {
            setPlaceEditButton()
            setPhotoEditButton()
            setStatusEditButton()
        } else if (reportComment.value?.isEmpty() == true) {
            reportComment.value = getApplication<App>().resources.getString(R.string.no_comment)
        }
    }

    fun setPlace(place: Place) {
        placeName.value = place.name.toString()
        placeLocation.value = place.latLng
        setPlaceEditButton()
    }

    fun setPhoto(path: String) {
        picturePath.value = path
        setPhotoEditButton()
    }

    fun saveReport(declarationComment: String, sendToServer: Boolean = false,
                   onSuccess: (String) -> Unit,
                   onError: (String) -> Unit) {

        val reportLocation = placeLocation.value
        if (reportLocation != null && status.value != null) {

            val report = currentReport?.copy(
                    name = placeName.value,
                    comment = declarationComment,
                    position = reportLocation,
                    photoPath = picturePath.value,
                    status = status.value) ?: run {
                ReportEntity(
                        deviceId = getApplication<App>().uniqueId(),
                        name = placeName.value,
                        comment = declarationComment,
                        position = reportLocation,
                        photoPath = picturePath.value,
                        status = status.value)
            }

            saveReportJob = launch {

                val resultPair = repository.saveReport(getApplication(), report, sendToServer = sendToServer)
                val saveResult = resultPair.first
                val sendToServerResult = resultPair.second
                withContext(Dispatchers.Main) {
                    if (saveResult is Result.Success && sendToServerResult is Result.Success) {
                        onSuccess(getApplication<App>().getString(R.string.send_report_success))
                    } else if (saveResult is Result.Success && !sendToServer) {
                        onSuccess(getApplication<App>().getString(R.string.save_report_success))
                    } else {
                        onError(getApplication<App>().getString(R.string.report_error))
                    }
                }
            }
        } else {
            onError(getApplication<App>().getString(R.string.mandatory_fields_missing_error))
        }
    }

    fun deleteReport() {
        reportingEvent.value = ReportingEvent.DeleteReport
    }

    fun deleteReport(onSuccess: (String) -> Unit,
                     onError: (String) -> Unit) {
        currentReport?.let {
            deleteReportJob = launch {
                val result = repository.deleteReport(it)
                withContext(Dispatchers.Main) {
                    when(result) {
                        is Result.Success -> {
                            onSuccess(getApplication<App>().getString(R.string.delete_report_success))
                        }
                        is Result.Error -> {
                            Log.e("ReportingViewModel", result.exception.message)
                            onError(getApplication<App>().getString(R.string.report_error))
                        }
                    }
                }
            }
        }
    }

    private fun setPlaceEditButton() {
        placePickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {onPlacePickerButtonCLicked()})
    }

    private fun setPhotoEditButton() {
        photoPickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {
            picturePath.value = null
            photoPickerEditButtonViewModel.value = null
        })
    }

    private fun setStatusEditButton() {
        statusPickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {
            status.value = null
            statusPickerEditButtonViewModel.value = null
        })
    }

    fun getShareIntent() : Intent {
        val appContext = getApplication<Application>().applicationContext

        val statusLabelRes = currentReport?.status?.labelRes
        val statusLabel = if (statusLabelRes != null) {
            appContext.resources.getString(statusLabelRes)
        } else null

        val shareReportContent = appContext.resources.getString(R.string.share_report_content,
                GoogleMapLinkHelper.getMapUrl(currentReport?.position, 20), statusLabel, currentReport?.comment)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/jpeg"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, appContext.resources.getString(R.string.share_report_title))
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareReportContent)

        ImageHelper.getSharedImageUri(appContext)?.let { fileUri ->
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        }

        return sharingIntent
    }

    override fun onCleared() {
        super.onCleared()
        saveReportJob?.cancel()
    }
}