package app.vdh.org.vdhapp.feature.report.domain.moderation.usecase

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class UpdateModerationStatusUseCase(val repository: ReportRepository) {

    suspend fun execute(reportId: String, newStatus: String): CallResult<ObservationDto> {
        return repository.updateReportModerationStatus(reportId, newStatus)
    }
}