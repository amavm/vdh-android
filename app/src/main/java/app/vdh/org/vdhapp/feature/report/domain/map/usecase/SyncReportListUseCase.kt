package app.vdh.org.vdhapp.feature.report.domain.map.usecase

import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class SyncReportListUseCase(private val repository: ReportRepository) {

    suspend fun execute() {
        repository.syncReports()
    }
}