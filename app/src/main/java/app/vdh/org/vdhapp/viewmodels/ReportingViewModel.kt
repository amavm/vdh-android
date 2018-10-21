package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.data.DeclarationRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingActionState
import com.google.android.gms.location.places.Place

class ReportingViewModel(application: Application, private val repository: DeclarationRepository) : AndroidViewModel(application) {

    val reportingEvent: SingleLiveEvent<ReportingActionState> = SingleLiveEvent()
    val report: MutableLiveData<ReportEntity> = MutableLiveData()

    var pickedPlace: MutableLiveData<Place> = MutableLiveData()
    val picturePath: MutableLiveData<String> = MutableLiveData()

    fun onPlacePickerButtonCLicked() {
        reportingEvent.value = ReportingActionState.PickPlace
    }

    fun onPhotoPickerButtonClicked() {
        reportingEvent.value = ReportingActionState.PickPhoto
    }

    fun setPlace(place: Place) {
        pickedPlace.value = place
    }

    fun setPhoto(path: String) {
        picturePath.value = path
    }

    fun saveReport(declarationComment: String) {
        if (report.value == null) {
            report.value = ReportEntity()
        }

        report.value?.run {
            name = pickedPlace.value?.name?.toString()
            comment = declarationComment
            position = pickedPlace.value?.latLng
            this.photoPath = picturePath.value
            repository.insertReport(this) { insertedId ->
                id = insertedId
            }
        }
    }
}