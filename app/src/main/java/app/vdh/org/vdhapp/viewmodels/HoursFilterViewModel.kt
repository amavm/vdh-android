package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.widget.NumberPicker
import androidx.lifecycle.AndroidViewModel
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.events.ReportFilterEvent

class HoursFilterViewModel(app: Application) : AndroidViewModel(app) {

    var currentHoursAgoFilter: Int = 2

    val reportFilterEvent: SingleLiveEvent<ReportFilterEvent> = SingleLiveEvent()

    fun onValueChanged(numberPicker: NumberPicker) {
        currentHoursAgoFilter = numberPicker.value
    }

    fun onHourFilterConfirmed() {
        reportFilterEvent.value = ReportFilterEvent.PickHoursFilter(currentHoursAgoFilter)
    }
}