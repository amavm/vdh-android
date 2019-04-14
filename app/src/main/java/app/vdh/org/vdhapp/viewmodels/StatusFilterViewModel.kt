package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.widget.RadioGroup
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.events.ReportFilterEvent
import app.vdh.org.vdhapp.data.models.Status

class StatusFilterViewModel(app: Application) : AndroidViewModel(app) {

    var currentStatus: MutableLiveData<Status?> = MutableLiveData()

    val reportFilterEvent: SingleLiveEvent<ReportFilterEvent> = SingleLiveEvent()

    fun onStatusCheckedChanged(radioGroup: RadioGroup, id: Int) {
        currentStatus.value = when (id) {
            R.id.status_filter_snow -> Status.SNOW
            R.id.status_filter_caution -> Status.CAUTION
            R.id.status_filter_clear -> Status.CLEARED
            R.id.status_filter_ice -> Status.ICE
            else -> null
        }
        reportFilterEvent.value = ReportFilterEvent.PickStatusFilter(currentStatus.value)
    }
}