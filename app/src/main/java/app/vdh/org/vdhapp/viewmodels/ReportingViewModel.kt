package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingActionState
import app.vdh.org.vdhapp.services.Result
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ReportingViewModel(application: Application, private val repository: ReportRepository) : AndroidViewModel(application), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    val reportingEvent: SingleLiveEvent<ReportingActionState> = SingleLiveEvent()

    var reportId: Long? = null
    var placeName: MutableLiveData<String> = MutableLiveData()
    var placeLocation: MutableLiveData<LatLng> = MutableLiveData()
    val picturePath: MutableLiveData<String> = MutableLiveData()
    val reportComment: MutableLiveData<String> = MutableLiveData()

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

    fun setReportData(report: ReportEntity) {
        reportId = report.id
        placeName.value = report.name
        placeLocation.value = report.position
        picturePath.value = report.photoPath
        reportComment.value = report.comment
    }

    fun setPlace(place: Place) {
        placeName.value = place.name.toString()
        placeLocation.value = place.latLng
    }

    fun setPhoto(path: String) {
        picturePath.value = path
    }

    fun saveReport(declarationComment: String, sendToServer: Boolean = false, whenSaved: (ReportEntity) -> Unit) {

        val report = ReportEntity(
                id = reportId,
                name = placeName.value,
                comment = declarationComment,
                position = placeLocation.value ?: LatLng(.0,.0),
                photoPath = picturePath.value
        )

        saveReportJob = launch {

            val resultPair = repository.saveReport(getApplication(), report, sendToServer = sendToServer)
            val saveResult = resultPair.first
            val sendToServerResult = resultPair.second

            withContext(Dispatchers.Main) {
                when (saveResult) {

                    is Result.Success -> {
                        report.id = saveResult.data
                        whenSaved(report)
                    }

                    is Result.Error -> Log.e("ReportingViewModel", "Save report exception ${saveResult.exception}")

                }

                when (sendToServerResult) {
                    is Result.Success -> {
                        Log.e("ReportingViewModel", "Sent to server")
                    }

                    is Result.Error -> Log.e("ReportingViewModel", "Send report exception ${sendToServerResult.exception}")

                    else -> {}
                }
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        saveReportJob?.cancel()
    }
}