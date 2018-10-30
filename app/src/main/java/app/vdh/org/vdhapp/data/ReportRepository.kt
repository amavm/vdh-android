package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import app.vdh.org.vdhapp.data.entities.ReportEntity

interface ReportRepository {

    fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean = false, onSuccess: (Long) -> Unit)

    fun getReports() : LiveData<List<ReportEntity>>
}