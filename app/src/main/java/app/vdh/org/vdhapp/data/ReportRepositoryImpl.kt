package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.extenstions.toObservationDto
import app.vdh.org.vdhapp.extenstions.toReportEntities
import app.vdh.org.vdhapp.services.ObservationService
import app.vdh.org.vdhapp.services.Result
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ReportRepositoryImpl(private val reportDao: ReportDao, private val observationService: ObservationService) : ReportRepository, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default


    override suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean): Pair<Result<Long>, Result<ObservationDto>?> {

        val sendServerResult: Result<ObservationDto>? =
                if (sendToServer) {
                    safeCall(call = {sendReportToServer(context, reportEntity)},
                            errorMessage = "Error during sending report")

                } else null

        if (sendServerResult is Result.Success) {
            reportEntity.syncTimestamp = sendServerResult.data.timestamp * 1000
        }

        val insertionResult = safeCall(call = {savedReport(reportEntity)},
                errorMessage = "Error during sending report")


        return Pair(insertionResult, sendServerResult)
    }

    override fun getReports(): LiveData<List<ReportEntity>> {
        launch {
            val deferred = async {
                syncReports()
            }
            val syncResult = deferred.await()
            when (syncResult) {
                is Result.Success -> Log.i("ReportRepositoryImpl", "Sync of ${syncResult.data} reports succeed")
                is Result.Error -> Log.e("ReportRepositoryImpl", "Sync reports error ${syncResult.exception}")

            }
        }
        return reportDao.getAllDeclarations()
    }

    private suspend fun syncReports() : Result<Int>? {
        val observationsResult = safeCall(call = {getObservationsFromServer()},
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

    private suspend fun getObservationsFromServer() : Result<ObservationListDto> {
        val response = observationService.getObservations().await()
        return if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            Result.Error(IOException("Error occurred when getting observations"))
        }
    }

    private suspend fun sendReportToServer(context: Context, reportEntity: ReportEntity) : Result<ObservationDto> {
        val observationDto = reportEntity.toObservationDto(context)
        val response = observationService.sendObservation(observationDto).await()
        return if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            Result.Error(IOException("Error occurred when posting report ${response.errorBody()}"))
        }
    }

    private suspend fun savedReport(report: ReportEntity) : Result<Long>{
        return async {
            Result.Success(reportDao.insertDeclaration(report))
        }.await()
    }

    private suspend fun savedReportList(reportList: List<ReportEntity>) : Result<List<Long>>{
        return async {
            Result.Success(reportDao.insertDeclarationList(reportList))
        }.await()
    }

    private suspend fun <T : Any> safeCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> = try {
        call.invoke()
    } catch (e: Exception) {
        Result.Error(Exception(errorMessage, e))
    }
}
