package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.widget.NumberPicker
import androidx.lifecycle.AndroidViewModel
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.events.MapFilterEvent

class HoursFilterViewModel(app: Application) : AndroidViewModel(app) {

    var currentHoursAgoFilter: Int = 2

    val mapFilterEvent: SingleLiveEvent<MapFilterEvent> = SingleLiveEvent()

    fun onValueChanged(numberPicker: NumberPicker) {
        currentHoursAgoFilter = numberPicker.value
    }

    fun onHourFilterConfirmed() {
        mapFilterEvent.value = MapFilterEvent.PickHoursFilter(currentHoursAgoFilter)
    }
}