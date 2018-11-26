package app.vdh.org.vdhapp.api

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response

interface ObservationApiClient {

    fun sendObservation(observationDto: ObservationDto) : Deferred<Response<ObservationDto>>

    fun getObservations() : Deferred<Response<ObservationListDto>>

    fun removeObservation(observationId: String) : Deferred<Response<ResponseBody>>
}