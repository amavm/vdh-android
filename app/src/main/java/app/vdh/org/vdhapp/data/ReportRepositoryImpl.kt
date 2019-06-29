package app.vdh.org.vdhapp.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.vdh.org.vdhapp.api.ObservationApiClient
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.api.safeCall
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import app.vdh.org.vdhapp.extenstions.toObservationDto
import app.vdh.org.vdhapp.extenstions.toReportEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ReportRepositoryImpl(private val reportDao: ReportDao, private val observationApiClient: ObservationApiClient) : ReportRepository {

    override suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean): Pair<Result<Long>, Result<ObservationDto>?> {

        var reportToSave = reportEntity

        val sendServerResult: Result<ObservationDto>? =
                if (sendToServer) {
                    safeCall(call = { observationApiClient.sendObservation(reportEntity.toObservationDto(context)) },
                            errorMessage = "Error during sending report")
                } else null

        if (sendServerResult is Result.Success) {
            sendServerResult.data.id?.let {
                reportToSave = reportEntity.copy(serverId = sendServerResult.data.id, sentToSever = true)
            }
        }

        val insertionResult = safeCall(call = { savedReport(reportToSave) },
                errorMessage = "Error during sending report")

        return Pair(insertionResult, sendServerResult)
    }

    override fun getReports(hoursAgo: Int, status: Status?, coroutineContext: CoroutineContext): LiveData<List<ReportEntity>> =
            liveData(coroutineContext + Dispatchers.Default) {

                val now = System.currentTimeMillis()
                val from = now - TimeUnit.HOURS.toMillis(hoursAgo.toLong())

                val disposable = emitSource(getReports(from, status))

                when (val syncResult = syncReports()) {
                    is Result.Success -> Log.i("ReportRepositoryImpl", "Sync of ${syncResult.data} reports succeed")
                    is Result.Error -> Log.e("ReportRepositoryImpl", "Sync reports error ${syncResult.exception}")
                }
                disposable.dispose()

                emitSource(getReports(from, status))
            }

    private fun getReports(from: Long, status: Status?) =
            if (status == null) {
                reportDao.getAllReports(from)
            } else {
                reportDao.getReports(status, from)
            }

    override suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter, network: BikePathNetwork): Result<JSONObject> {
        return observationApiClient.getBicyclePaths(boundingBoxQueryParameter = boundingBoxQueryParameter, network = network)
    }

    override suspend fun deleteReport(reportEntity: ReportEntity): Result<String> {
        reportEntity.serverId?.let {
            val serverResult = safeCall(call = { observationApiClient.removeObservation(it) }, errorMessage = "Exception during remove from server")
            if (serverResult is Result.Success) {
                val dbResult = safeCall(call = { deleteFromDatabase(reportEntity) }, errorMessage = "Unable to remove from db")
                if (dbResult is Result.Success) {
                    return Result.Success("Report removed")
                }
            }
        }

        return Result.Error(Exception("Unable to delete report"))
    }

    private suspend fun deleteFromDatabase(reportEntity: ReportEntity): Result<Unit> {
        return Result.Success(reportDao.deleteReport(reportEntity))
    }

    private suspend fun syncReports(): Result<Int>? {
        return when (val observationsResult = safeCall(call = { observationApiClient.getObservations() },
                errorMessage = "Error occurred when getting observations")) {
            is Result.Success -> {
                when (val insertionResult = safeCall(call = { savedReportList(observationsResult.data.observationList.toReportEntities()) },
                        errorMessage = "Error occurred when saving observations")) {
                    is Result.Success -> Result.Success(observationsResult.data.observationList.size)
                    is Result.Error -> Result.Error(insertionResult.exception)
                }
            }

            is Result.Error -> {
                Result.Error(observationsResult.exception)
            }
        }
    }

    private suspend fun savedReport(report: ReportEntity): Result<Long> {
        return Result.Success(reportDao.insertReport(report))
    }

    private suspend fun savedReportList(reportList: List<ReportEntity>): Result<List<Long>> {
        return Result.Success(reportDao.insertReportList(reportList))
    }
}
