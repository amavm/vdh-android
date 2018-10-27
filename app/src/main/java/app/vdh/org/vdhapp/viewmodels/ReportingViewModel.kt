package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingActionState
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng

class ReportingViewModel(application: Application, private val repository: ReportRepository) : AndroidViewModel(application) {

    val reportingEvent: SingleLiveEvent<ReportingActionState> = SingleLiveEvent()

    var reportId: Long? = null
    var placeName: MutableLiveData<String> = MutableLiveData()
    var placeLocation: MutableLiveData<LatLng> = MutableLiveData()
    val picturePath: MutableLiveData<String> = MutableLiveData()
    val reportComment: MutableLiveData<String> = MutableLiveData()



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

    fun saveReport(declarationComment: String, whenSaved: (ReportEntity) -> Unit) {

        val report = ReportEntity(
                id = reportId,
                name = placeName.value,
                comment = declarationComment,
                position = placeLocation.value ?: LatLng(.0,.0),
                photoPath = picturePath.value
        )

        repository.insertReport(report) { insertedId ->
            report.id = insertedId
            whenSaved(report)
        }
    }
}