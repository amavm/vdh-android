package app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel

import android.app.Application
import android.widget.NumberPicker
import androidx.lifecycle.AndroidViewModel
import app.vdh.org.vdhapp.core.helpers.SingleLiveEvent
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapFilterViewAction

class HoursFilterViewModel(app: Application) : AndroidViewModel(app) {

    var currentHoursAgoFilter: Int = 2

    val reportMapFilterViewAction: SingleLiveEvent<ReportMapFilterViewAction> = SingleLiveEvent()

    fun onValueChanged(numberPicker: NumberPicker) {
        currentHoursAgoFilter = numberPicker.value
    }

    fun onHourFilterConfirmed() {
        reportMapFilterViewAction.value = ReportMapFilterViewAction.OpenHourFilter(currentHoursAgoFilter)
    }
}