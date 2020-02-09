package app.vdh.org.vdhapp.feature.report.domain.common.repository

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.core.consts.PrefConst

interface ReportRepository {

    suspend fun getReports(hoursAgo: Int = PrefConst.HOURS_SORT_DEFAULT_VALUE, status: Status?): LiveData<List<ReportModel>>

    suspend fun saveReport(report: ReportModel, saveRemotely: Boolean): Pair<CallResult<Long>, CallResult<ObservationDto>?>

    suspend fun deleteReport(report: ReportModel): CallResult<String>
}