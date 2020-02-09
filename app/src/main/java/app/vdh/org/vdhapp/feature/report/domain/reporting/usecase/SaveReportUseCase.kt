package app.vdh.org.vdhapp.feature.report.domain.reporting.usecase

import android.content.Context
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class SaveReportUseCase(private val reportRepository: ReportRepository, private val appContext: Context) {

    suspend fun execute(reportModel: ReportModel, saveRemotely: Boolean): Pair<CallResult<Long>, CallResult<ObservationDto>?> {
        return reportRepository.saveReport(reportModel, saveRemotely)
    }
}