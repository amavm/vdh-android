package app.vdh.org.vdhapp.services

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ObservationRestrofitService {

    @GET("observations")
    fun getObservationList(@Query("startTs") startTimeStamp: Long? = null,
                           @Query("endTs") endTimestamp: Long? = null,
                           @Query("sort") sort: String? = null,
                           @Query("nextToken") nextToken: String? = null) : Call<ObservationListDto>

    @POST("observations")
    fun postObservation(@Body observation: ObservationDto) : Deferred<Response<ObservationDto>>
}