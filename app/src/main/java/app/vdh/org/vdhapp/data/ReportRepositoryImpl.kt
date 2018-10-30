package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.extenstions.toObservationDto
import app.vdh.org.vdhapp.services.ObservationService
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportRepositoryImpl(private val reportDao: ReportDao, private val observationService: ObservationService) : ReportRepository {

    override fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean, onSuccess: (Long) -> Unit) {
        doAsync {
            val insertedId = reportDao.insertDeclaration(reportEntity)

            if (sendToServer) {
                sendReportToServer(context, reportEntity,
                        onSuccess = {
                            onSuccess(insertedId)
                        },
                        onError = {
                            Log.e("ReportRepository", "${it.message}")
                        })
            } else {
                uiThread {
                    onSuccess(insertedId)
                }
            }
        }
    }

    private fun sendReportToServer(context: Context, reportEntity: ReportEntity, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val observationDto = reportEntity.toObservationDto(context)
        val call = observationService.sendObservation(observationDto)
        call.enqueue(object : Callback<ObservationDto> {
            override fun onFailure(call: Call<ObservationDto>, t: Throwable) {
                onError(t)
            }

            override fun onResponse(call: Call<ObservationDto>, response: Response<ObservationDto>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Exception(response.errorBody()?.string()))
                }

            }

        })
    }

    override fun getReports(): LiveData<List<ReportEntity>> {
        return reportDao.getAllDeclarations()
    }
}
