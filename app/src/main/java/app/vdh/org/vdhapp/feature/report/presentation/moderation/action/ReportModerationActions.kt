package app.vdh.org.vdhapp.feature.report.presentation.moderation.action

sealed class ReportModerationViewAction {

    data class ChangeModerationStatusFilter(val status: String) : ReportModerationViewAction()
    data class ChangeModerationStatus(val reportId: String, val reportPosition: Int, val newStatus: String) : ReportModerationViewAction()
}

sealed class ReportModerationAction {
    data class ReportStatusUpdated(val reportPosition: Int) : ReportModerationAction()
    object ReportStatusUpdateError : ReportModerationAction()
}