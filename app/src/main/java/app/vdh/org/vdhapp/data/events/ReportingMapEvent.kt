package app.vdh.org.vdhapp.data.events

import app.vdh.org.vdhapp.data.models.Status

sealed class ReportingMapEvent {
    object AddReport : ReportingMapEvent()
    data class OpenStatusFilterDialog(val currentStatusFilter: Status?) : ReportingMapEvent()
}