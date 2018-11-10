package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.text.format.DateUtils
import app.vdh.org.vdhapp.App
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.Status
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingActionState
import app.vdh.org.vdhapp.services.Result
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ReportingViewModel(application: Application, private val repository: ReportRepository) : AndroidViewModel(application), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    val reportingEvent: SingleLiveEvent<ReportingActionState> = SingleLiveEvent()

    var placeName: MutableLiveData<String> = MutableLiveData()
    var placeLocation: MutableLiveData<LatLng> = MutableLiveData()
    val picturePath: MutableLiveData<String> = MutableLiveData()
    val reportComment: MutableLiveData<String> = MutableLiveData()
    var syncDate: MutableLiveData<String> = MutableLiveData()
    var status : MutableLiveData<Status> = MutableLiveData()

    var placePickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()
    var photoPickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()
    var statusPickerEditButtonViewModel : MutableLiveData<EditButtonViewModel> = MutableLiveData()

    private var saveReportJob: Job? = null

    fun onPlacePickerButtonCLicked() {
        reportingEvent.value = ReportingActionState.PickPlace
    }

    fun onPhotoFromGalleryButtonClicked() {
        reportingEvent.value = ReportingActionState.PickPhoto
    }

    fun onTakePhotoButtonClicked() {
        reportingEvent.value = ReportingActionState.TakePhoto
    }

    fun onStatusSelected(selectedStatus: Status) {
        status.value = selectedStatus
        statusPickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {
            status.value = null
            statusPickerEditButtonViewModel.value = null
        })
    }

    fun setReportData(report: ReportEntity) {
        placeName.value = report.name
        placeLocation.value = report.position
        picturePath.value = report.photoPath
        reportComment.value = report.comment
        report.syncTimestamp?.let {
            syncDate.value = getApplication<App>().getString(R.string.sync_date, DateUtils.getRelativeTimeSpanString(it).toString())
        }
        status.value = report.status
    }

    fun setPlace(place: Place) {
        placeName.value = place.name.toString()
        placeLocation.value = place.latLng
        placePickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {onPlacePickerButtonCLicked()})
    }

    fun setPhoto(path: String) {
        picturePath.value = path
        photoPickerEditButtonViewModel.value = EditButtonViewModel(visible = true, onClick = {
            picturePath.value = null
            photoPickerEditButtonViewModel.value = null
        })
    }

    fun saveReport(declarationComment: String, sendToServer: Boolean = false,
                   onSuccess: (String) -> Unit,
                   onError: (String) -> Unit) {

        val report = ReportEntity(
                name = placeName.value,
                comment = declarationComment,
                position = placeLocation.value ?: LatLng(.0,.0),
                photoPath = picturePath.value,
                status = status.value
        )

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
                    onError(getApplication<App>().getString(R.string.save_send_report_error))
                }

            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        saveReportJob?.cancel()
    }
}