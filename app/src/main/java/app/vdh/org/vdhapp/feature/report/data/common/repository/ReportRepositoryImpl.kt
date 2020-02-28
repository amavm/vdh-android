package app.vdh.org.vdhapp.feature.report.data.common.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.vdh.org.vdhapp.core.extenstion.toMillisecondFromNow
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.core.helpers.AuthHelper
import app.vdh.org.vdhapp.feature.report.data.common.local.ReportDao
import app.vdh.org.vdhapp.feature.report.data.common.local.entity.ReportEntity
import app.vdh.org.vdhapp.feature.report.data.common.remote.client.observation.ObservationApiClient
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationListDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.toReportEntities
import app.vdh.org.vdhapp.core.helpers.safeCall
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ModerationStatus
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository

class ReportRepositoryImpl(
    private val reportDao: ReportDao,
    private val observationApiClient: ObservationApiClient,
    private val appContext: Context
) : ReportRepository {

    override suspend fun getReports(hoursAgo: Int, status: Status?): LiveData<List<ReportModel>> {
        return getReports(hoursAgo.toMillisecondFromNow(), status)
                .map { entities -> entities.map { it.toReportModel() } }
    }

    override suspend fun getReportsByModerationStatus(hoursAgo: Int, moderationStatus: String): LiveData<List<ReportModel>> {
        return reportDao.getReportsByModerationStatus(hoursAgo.toMillisecondFromNow(), moderationStatus)
                .map { entities -> entities.map { it.toReportModel() } }
    }

    override suspend fun syncReports(): CallResult<Int>? {
        return when (val observationsResult = safeCall(call = { observationApiClient.getObservations() },
                errorMessage = "Error occurred when getting observations")) {
            is CallResult.Success -> {
                when (val insertionResult = safeCall(call = { saveObservationList(observationsResult.data) },
                        errorMessage = "Error occurred when saving observations")) {
                    is CallResult.Success -> CallResult.Success(observationsResult.data.observationList.size)
                    is CallResult.Error -> CallResult.Error(insertionResult.exception)
                }
            }

            is CallResult.Error -> {
                CallResult.Error(observationsResult.exception)
            }
        }
    }

    override suspend fun saveReport(report: ReportModel, saveRemotely: Boolean): Pair<CallResult<Long>, CallResult<ObservationDto>?> {

        var reportToSave = report.toReportEntity()

        val sendServerResult: CallResult<ObservationDto>? =
                if (saveRemotely) {
                    safeCall(
                            call = { observationApiClient.sendObservation(report.toObservationDto(appContext)) },
                            errorMessage = "Error during sending report"
                    )
                } else null

        if (sendServerResult is CallResult.Success) {
            sendServerResult.data.id?.let {
                reportToSave = reportToSave.copy(serverId = sendServerResult.data.id, sentToSever = true)
            }
        }

        val insertionResult = safeCall(call = { savedReport(reportToSave) },
                errorMessage = "Error during sending report")

        return Pair(insertionResult, sendServerResult)
    }

    override suspend fun deleteReport(report: ReportModel): CallResult<String> {
        report.serverId?.let {
            val serverResult = safeCall(call = { observationApiClient.removeObservation(it) }, errorMessage = "Exception during remove from server")
            if (serverResult is CallResult.Success) {
                val dbResult = safeCall(call = { deleteFromDatabase(report) }, errorMessage = "Unable to remove from db")
                if (dbResult is CallResult.Success) {
                    return CallResult.Success("Report removed")
                }
            }
        }

        return CallResult.Error(Exception("Unable to delete report"))
    }

    private suspend fun deleteFromDatabase(report: ReportModel): CallResult<Unit> {
        return CallResult.Success(reportDao.deleteReport(report.toReportEntity()))
    }

    override suspend fun updateReportModerationStatus(reportId: String, status: String): CallResult<ObservationDto> {
        val result = safeCall(
                call = { observationApiClient.updateObservationModerationStatus(reportId, ModerationStatus(status)) },
                errorMessage = "Unable to update report"
        )
        if (result is CallResult.Success) {
            reportDao.updateReport(reportId, status)
        }
        return result
    }

    private suspend fun saveObservationList(observationListDto: ObservationListDto): CallResult<List<Long>> {
        val reports = observationListDto.observationList.toReportEntities()
        return CallResult.Success(reportDao.insertReportList(reports))
    }

    private suspend fun savedReport(report: ReportEntity): CallResult<Long> {
        return CallResult.Success(reportDao.insertReport(report))
    }

    private fun getReports(from: Long, status: Status?): LiveData<List<ReportEntity>> {
        return if (status == null) {
            reportDao.getAllValidOrDeviceOwnerReports(from, AuthHelper.UID)
        } else {
            reportDao.getValidOrDeviceOwnerReports(status, from, AuthHelper.UID)
        }
    }
}
