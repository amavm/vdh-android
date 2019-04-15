package app.vdh.org.vdhapp.api

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.LatLngQueryParameter
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ObservationRetrofitClient {

    @POST("observations")
    fun postObservationAsync(@Body observation: ObservationDto): Deferred<Response<ObservationDto>>

    @GET("observations")
    fun getObservationsAsync(
        @Query("startTs") startTimeStamp: Long? = null,
        @Query("endTs") endTimestamp: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("nextToken") nextToken: String? = null
    ): Deferred<Response<ObservationListDto>>

    @DELETE("observations/{id}")
    fun deleteObservationAsync(@Path("id") id: String): Deferred<Response<ResponseBody>>

    @GET("bicycle-paths")
    fun getBicyclePathsAsync(
            @Query("bbox") boundingBoxQueryParameter: BoundingBoxQueryParameter?,
            @Query("near") centerLatLng: LatLngQueryParameter?,
            @Query("nextToken") nextToken: String? = null,
            @Query("network ") bikePathNetwork: BikePathNetwork = BikePathNetwork.FOUR_SEASONS
    ): Deferred<Response<ResponseBody>>
}