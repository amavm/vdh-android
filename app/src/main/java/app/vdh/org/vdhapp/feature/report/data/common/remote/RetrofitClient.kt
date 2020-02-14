package app.vdh.org.vdhapp.feature.report.data.common.remote

import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationListDto
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.data.map.remote.LatLngQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.Path

interface RetrofitClient {

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