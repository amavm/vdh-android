package app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel

import android.app.Application
import android.widget.RadioGroup
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.helpers.SingleLiveEvent
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapFilterViewAction

class StatusFilterViewModel(app: Application) : AndroidViewModel(app) {

    var currentStatus: MutableLiveData<Status?> = MutableLiveData()

    val reportMapFilterViewAction: SingleLiveEvent<ReportMapFilterViewAction> = SingleLiveEvent()

    fun onStatusCheckedChanged(radioGroup: RadioGroup, id: Int) {
        currentStatus.value = when (id) {
            R.id.status_filter_snow -> Status.SNOW
            R.id.status_filter_caution -> Status.CAUTION
            R.id.status_filter_clear -> Status.CLEARED
            R.id.status_filter_ice -> Status.ICE
            else -> null
        }
        reportMapFilterViewAction.value = ReportMapFilterViewAction.OpenStatusFilter(currentStatus.value)
    }
}