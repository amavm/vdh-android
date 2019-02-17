package app.vdh.org.vdhapp.data.events

import app.vdh.org.vdhapp.data.models.Status

sealed class ReportFilterEvent {
    data class PickStatusFilter(val status: Status?) : ReportFilterEvent()
    data class PickHoursFilter(val hoursAgo: Int) : ReportFilterEvent()
}