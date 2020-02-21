package app.vdh.org.vdhapp.feature.report.presentation.moderation.recyclerview

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationViewAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.uimodel.ReportModerationUiModel
import kotlinx.android.synthetic.main.view_report_item.view.*

class ReportModerationViewHolder(
    view: View,
    private val viewActionHandler: (ReportModerationViewAction) -> Unit
) : RecyclerView.ViewHolder(view) {

    fun onBind(reportUiModel: ReportModerationUiModel) {

        itemView.reportStatus.setImageResource(reportUiModel.pathStatusIcon)
        itemView.reportComment.text = reportUiModel.comment

        when (reportUiModel.moderationStatus) {
            ApiConst.MODERATION_STATUS_VALID -> itemView.reportModerationStatus.background = ColorDrawable(ContextCompat.getColor(itemView.context, R.color.green))
            ApiConst.MODERATION_STATUS_PENDING -> itemView.reportModerationStatus.background = ColorDrawable(ContextCompat.getColor(itemView.context, R.color.orange))
        }

        itemView.acceptReportButton.visibility = if (reportUiModel.displayAcceptReport) View.VISIBLE else View.INVISIBLE
        itemView.acceptReportButton.setOnClickListener {
            viewActionHandler(ReportModerationViewAction.ChangeModerationStatus(reportUiModel.reportId, adapterPosition, ApiConst.MODERATION_STATUS_VALID))
        }

        itemView.refuseReportButton.visibility = if (reportUiModel.displayRejectReport) View.VISIBLE else View.INVISIBLE
        itemView.refuseReportButton.setOnClickListener {
            viewActionHandler(ReportModerationViewAction.ChangeModerationStatus(reportUiModel.reportId, adapterPosition, ApiConst.MODERATION_STATUS_REJECTED))
        }
    }
}