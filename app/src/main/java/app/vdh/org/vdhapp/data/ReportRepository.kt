package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import app.vdh.org.vdhapp.data.entities.ReportEntity

interface ReportRepository {

    fun insertReport(reportEntity: ReportEntity, whenInserted: (Long) -> Unit)

    fun getReports() : LiveData<List<ReportEntity>>
}