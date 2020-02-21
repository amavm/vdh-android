package app.vdh.org.vdhapp.feature.report.presentation.moderation.uimodel

import androidx.annotation.DrawableRes
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel

data class ReportModerationUiModel(
    val reportId: String,
    @DrawableRes
    val pathStatusIcon: Int,
    val comment: String,
    val moderationStatus: String,
    val displayAcceptReport: Boolean,
    val displayRejectReport: Boolean
)

fun ReportModel.toReportModerationUiModel(statusFilter: String): ReportModerationUiModel {
    return ReportModerationUiModel(
            reportId = serverId!!,
            pathStatusIcon = status?.iconRes!!,
            moderationStatus = moderationStatus!!,
            comment = comment!!,
            displayAcceptReport = statusFilter == ApiConst.MODERATION_STATUS_PENDING || statusFilter == ApiConst.MODERATION_STATUS_REJECTED,
            displayRejectReport = statusFilter == ApiConst.MODERATION_STATUS_PENDING || statusFilter == ApiConst.MODERATION_STATUS_VALID
    )
}