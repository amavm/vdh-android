package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.App
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.DeclarationRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.helpers.GoogleStaticMapHelper
import com.google.android.gms.location.places.Place

class DeclarationViewModel(application: Application, repository: DeclarationRepository) : AndroidViewModel(application) {

    val openPlacePickerEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val openPhotoPickerEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    var pickedPlace: MutableLiveData<Place> = MutableLiveData()
    val photoPath: MutableLiveData<String> = MutableLiveData()

    fun onPlacePickerButtonCLicked() {
        openPlacePickerEvent.call()
    }

    fun onPhotoPickerButtonClicked() {
        openPhotoPickerEvent.call()
    }

    fun setPlace(place: Place) {
        pickedPlace.value = place
    }

    fun setPhoto(path: String) {
        photoPath.value = path
    }

}