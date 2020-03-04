package app.vdh.org.vdhapp.feature.report.presentation.reporting.action

import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import com.mapbox.mapboxsdk.geometry.LatLng

sealed class ReportingViewAction {
    object PickPlace : ReportingViewAction()
    object PickPhoto : ReportingViewAction()
    object TakePhoto : ReportingViewAction()
    object OpenDeleteDialog : ReportingViewAction()
    data class DeleteReportSuccess(val message: String) : ReportingViewAction()
    data class DeleteReportError(val message: String) : ReportingViewAction()
    data class SaveReportSuccess(val message: String, val sentToServer: Boolean) : ReportingViewAction()
    data class SaveReportError(val message: String) : ReportingViewAction()
}

sealed class ReportingAction {
    data class UpdateStatus(val status: Status) : ReportingAction()
    data class UpdatePicture(val picture: String) : ReportingAction()
    data class UpdatePlace(val name: String, val location: LatLng) : ReportingAction()
    data class SaveReport(val sendToServer: Boolean) : ReportingAction()
    object DeleteReport : ReportingAction()
    object OpenPlacePicker : ReportingAction()
    object OpenPhotoGallery : ReportingAction()
    object TakePicture : ReportingAction()
    object OpenDeleteDialog : ReportingAction()
    object EditPlace : ReportingAction()
    object EditPhoto : ReportingAction()
    object EditStatus : ReportingAction()
}