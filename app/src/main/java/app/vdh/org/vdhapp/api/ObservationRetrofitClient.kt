package app.vdh.org.vdhapp.api

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ObservationRetrofitClient {

    @POST("observations")
    fun postObservation(@Body observation: ObservationDto) : Deferred<Response<ObservationDto>>

    @GET("observations")
    fun getObservations(@Query("startTs") startTimeStamp: Long? = null,
                        @Query("endTs") endTimestamp: Long? = null,
                        @Query("sort") sort: String? = null,
                        @Query("nextToken") nextToken: String? = null) : Deferred<Response<ObservationListDto>>

    @DELETE("observations/{id}")
    fun deleteObservation(@Path("id") id: String) : Deferred<Response<ResponseBody>>

}