package app.vdh.org.vdhapp.feature.report.presentation.map.action

import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
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