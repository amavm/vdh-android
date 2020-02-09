package app.vdh.org.vdhapp.feature.report.domain.reporting.usecase

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class DeleteReportUseCase(private val reportRepository: ReportRepository) {
    suspend fun execute(report: ReportModel): CallResult<String> {
        return reportRepository.deleteReport(report)
    }
}