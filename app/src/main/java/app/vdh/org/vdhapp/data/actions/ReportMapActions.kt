package app.vdh.org.vdhapp.data.actions

import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import org.json.JSONObject

sealed class ReportMapFilterViewAction {
    data class OpenStatusFilter(val status: Status?) : ReportMapFilterViewAction()
    data class OpenHourFilter(val hoursAgo: Int) : ReportMapFilterViewAction()
    data class ReportFilterPickedReport(val status: Status?, val hoursAgo: Int) : ReportMapFilterViewAction()
    data class RefreshPathNetwork(val bikePathNetwork: BikePathNetwork) : ReportMapFilterViewAction()
}

sealed class ReportMapViewAction {
    object OpenReportCreation : ReportMapViewAction()
    data class BicyclePathQuerySuccess(val data: JSONObject, val network: BikePathNetwork) : ReportMapViewAction()
    data class BicyclePathQueryError(val exception: Exception) : ReportMapViewAction()
}

sealed class ReportMapAction {
    object CreateReport : ReportMapAction()
    data class GetBicyclePath(val boundingBoxQueryParameter: BoundingBoxQueryParameter) : ReportMapAction()
    object FilterByHour : ReportMapAction()
    object FilterByStatus : ReportMapAction()
    data class ChangeBikePath(val network: BikePathNetwork? = null) : ReportMapAction()
}