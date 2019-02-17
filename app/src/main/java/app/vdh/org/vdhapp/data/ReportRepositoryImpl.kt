package app.vdh.org.vdhapp.data

import androidx.lifecycle.LiveData
import android.content.Context
import android.util.Log
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.extenstions.toObservationDto
import app.vdh.org.vdhapp.extenstions.toReportEntities
import app.vdh.org.vdhapp.api.ObservationApiClient
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.api.safeCall
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ReportRepositoryImpl(private val reportDao: ReportDao, private val observationApiClient: ObservationApiClient) : ReportRepository, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default


    override suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean): Pair<Result<Long>, Result<ObservationDto>?> {

        val sendServerResult: Result<ObservationDto>? =
                if (sendToServer) {
                    safeCall(call = { observationApiClient.sendObservation(reportEntity.toObservationDto(context))},
                            errorMessage = "Error during sending report")

                } else null

        if (sendServerResult is Result.Success) {
            reportEntity.syncTimestamp = sendServerResult.data.timestamp * 1000
            sendServerResult.data.id?.let {
                reportEntity.serverId = sendServerResult.data.id
            }
        }

        val insertionResult = safeCall(call = {savedReport(reportEntity)},
                errorMessage = "Error during sending report")


        return Pair(insertionResult, sendServerResult)
    }

    override fun getReports(hoursAgo: Int, status: Status?): LiveData<List<ReportEntity>> {
        launch {
            val syncResult = withContext(Dispatchers.Default) {
                syncReports()
            }
            when (syncResult) {
                is Result.Success -> Log.i("ReportRepositoryImpl", "Sync of ${syncResult.data} reports succeed")
                is Result.Error -> Log.e("ReportRepositoryImpl", "Sync reports error ${syncResult.exception}")

            }
        }

        val now = System.currentTimeMillis()
        val from = now - TimeUnit.HOURS.toMillis(hoursAgo.toLong())

        return if (status == null){
            reportDao.getAllReports(from, now)
        } else {
            reportDao.getReports(status, from, now)
        }
    }

    override suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter): Result<JSONObject> {
        return observationApiClient.getBicyclePaths(boundingBoxQueryParameter = boundingBoxQueryParameter)
    }

    override suspend fun getBicyclePathGeoJson(centerCoordinates: LatLng): Result<JSONObject> {
        return observationApiClient.getBicyclePaths(centerCoordinates = centerCoordinates)
    }

    override suspend fun getBicyclePathGeoJson(): Result<JSONObject> {
        return observationApiClient.getBicyclePaths()
    }

    override suspend fun deleteReport(reportEntity: ReportEntity): Result<String> {
        reportEntity.serverId?.let {
            val serverResult = safeCall(call = { observationApiClient.removeObservation(it)}, errorMessage = "Exception during remove from server")
            if (serverResult is Result.Success) {
                val dbResult = safeCall(call = { deleteFromDatabase(reportEntity) }, errorMessage = "Unable to remove from db")
                if (dbResult is Result.Success) {
                    return Result.Success("Report removed")
                }
            }
        }

        return Result.Error(Exception("Unable to delete report"))
    }

    private suspend fun deleteFromDatabase(reportEntity: ReportEntity) : Result<Unit> {
        return withContext(Dispatchers.Default) {
            Result.Success(reportDao.deleteReport(reportEntity))
        }
    }

    private suspend fun syncReports() : Result<Int>? {
        val observationsResult = safeCall(call = {observationApiClient.getObservations()},
                errorMessage = "Error occurred when getting observations")
        return when (observationsResult) {
            is Result.Success -> {
                val insertionResult = safeCall(call = {savedReportList(observationsResult.data.observationList.toReportEntities())},
                        errorMessage = "Error occurred when saving observations")
                when(insertionResult) {
                    is Result.Success -> Result.Success(observationsResult.data.observationList.size)
                    is Result.Error -> Result.Error(insertionResult.exception)
                }
            }

            is Result.Error -> {
                Result.Error(observationsResult.exception)
            }
        }
    }

    private suspend fun savedReport(report: ReportEntity) : Result<Long>{
        return withContext(Dispatchers.Default) {
            Result.Success(reportDao.insertReport(report))
        }
    }

    private suspend fun savedReportList(reportList: List<ReportEntity>) : Result<List<Long>>{
        return withContext(Dispatchers.Default) {
            Result.Success(reportDao.insertReportList(reportList))
        }
    }
}
