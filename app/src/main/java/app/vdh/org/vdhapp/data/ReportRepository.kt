package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.services.Result

interface ReportRepository {

    suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean = false) : Pair<Result<Long>, Result<ObservationDto>?>

    fun getReports() : LiveData<List<ReportEntity>>
}