package app.vdh.org.vdhapp.viewholders

import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.ReportItemViewModel
import app.vdh.org.vdhapp.databinding.ItemReportBinding

class ReportViewHolder(private val viewDataBinding: ItemReportBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {

    private val currentReport: ReportItemViewModel = ReportItemViewModel()

    init {
        viewDataBinding.report = currentReport
    }

    fun onBind(report: ReportEntity) {
        currentReport.onBind(report)
        viewDataBinding.executePendingBindings()
    }
}