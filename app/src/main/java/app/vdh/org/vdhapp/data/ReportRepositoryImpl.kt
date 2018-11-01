package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.extenstions.toObservationDto
import app.vdh.org.vdhapp.services.ObservationService
import app.vdh.org.vdhapp.services.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.IOException

class ReportRepositoryImpl(private val reportDao: ReportDao, private val observationService: ObservationService) : ReportRepository {


    override suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean): Result<Long> {

        val insertionResult = safeCall(call = {savedReport(reportEntity)},
                errorMessage = "Error during sending report")


        val sendServerResult: Result<ObservationDto>? =
                if (sendToServer) {
                    safeCall(call = {sendReportToServer(context, reportEntity)},
                            errorMessage = "Error during sending report")
                } else null

        return when {
            insertionResult is Result.Error -> Result.Error(insertionResult.exception)
            sendServerResult is Result.Error -> Result.Error(sendServerResult.exception)
            else -> insertionResult
        }
    }

    override fun getReports(): LiveData<List<ReportEntity>> {
        return reportDao.getAllDeclarations()
    }

    private suspend fun sendReportToServer(context: Context, reportEntity: ReportEntity) : Result<ObservationDto> {
        val observationDto = reportEntity.toObservationDto(context)
        val deferred = observationService.sendObservation(observationDto)
        val response = deferred.await()
        return if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            Result.Error(IOException("Error occurred when posting information"))
        }
    }

    private suspend fun savedReport(report: ReportEntity) : Result<Long>{
        return GlobalScope.async {
            Result.Success(reportDao.insertDeclaration(report))
        }.await()
    }

    private suspend fun <T : Any> safeCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> = try {
        call.invoke()
    } catch (e: Exception) {
        Result.Error(Exception(errorMessage, e))
    }
}
