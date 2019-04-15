package app.vdh.org.vdhapp.data.events

import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.Status

sealed class MapFilterEvent {
    data class PickStatusFilter(val status: Status?) : MapFilterEvent()
    data class PickHoursFilter(val hoursAgo: Int) : MapFilterEvent()
    data class ReportFilterPicked(val status: Status?, val hoursAgo: Int) : MapFilterEvent()
    data class NetworkFilterPicked(val bikePathNetwork: BikePathNetwork) : MapFilterEvent()
}