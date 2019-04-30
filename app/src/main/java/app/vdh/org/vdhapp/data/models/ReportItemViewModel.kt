package app.vdh.org.vdhapp.data.models

import androidx.databinding.ObservableField
import app.vdh.org.vdhapp.data.entities.ReportEntity

class ReportItemViewModel {

    val reportName: ObservableField<String> = ObservableField()

    fun onBind(report: ReportEntity) {
        reportName.set(report.name)
    }
}