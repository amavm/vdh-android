package app.vdh.org.vdhapp.feature.report.domain.moderation.usecase

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class GetReportByModerationStatusUseCase(private val repository: ReportRepository) {

    suspend fun execute(hoursAgo: Int, moderationStatus: String): LiveData<List<ReportModel>> {
        return repository.getReportsByModerationStatus(hoursAgo, moderationStatus)
    }
}