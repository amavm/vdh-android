package app.vdh.org.vdhapp.feature.report.presentation.moderation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationViewAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.uimodel.ReportModerationUiModel

class ReportModerationAdapter(
    private val viewActionHandler: (ReportModerationViewAction) -> Unit
) : RecyclerView.Adapter<ReportModerationViewHolder>() {

    private val reports: MutableList<ReportModerationUiModel> = mutableListOf()

    fun updateReports(newReports: List<ReportModerationUiModel>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportModerationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_report_item, parent, false)
        return ReportModerationViewHolder(view, viewActionHandler)
    }

    override fun getItemCount(): Int = reports.count()

    override fun onBindViewHolder(holder: ReportModerationViewHolder, position: Int) {
        holder.onBind(reports[position])
    }
}