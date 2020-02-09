package app.vdh.org.vdhapp.feature.report.domain.map.usecase

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status

class GetReportListUseCase constructor(private val repository: ReportRepository) {
    suspend fun execute(status: Status?, hoursAgo: Int): LiveData<List<ReportModel>> {
        return repository.getReports(hoursAgo, status)
    }
}