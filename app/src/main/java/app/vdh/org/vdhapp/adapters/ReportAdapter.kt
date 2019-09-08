package app.vdh.org.vdhapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.databinding.ItemReportBinding
import app.vdh.org.vdhapp.viewholders.ReportViewHolder

class ReportAdapter(private val context: Context) : RecyclerView.Adapter<ReportViewHolder>() {

    private val reports = mutableListOf<ReportEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val viewDataBinding = ItemReportBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReportViewHolder(viewDataBinding)
    }

    override fun getItemCount(): Int = reports.count()

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.onBind(reports[position])
    }

    fun removeReport(position: Int) {
        reports.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateReports(newReports: List<ReportEntity>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }
}