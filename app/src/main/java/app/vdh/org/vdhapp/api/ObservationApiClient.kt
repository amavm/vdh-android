package app.vdh.org.vdhapp.api

import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

interface ObservationApiClient {

    fun sendObservation(observationDto: ObservationDto) : Deferred<Response<ObservationDto>>

    fun getObservations() : Deferred<Response<ObservationListDto>>

    fun removeObservation(observationId: String) : Deferred<Response<ResponseBody>>

    suspend fun getBicyclePaths(boundingBoxQueryParameter: BoundingBoxQueryParameter? = null,
                                centerCoordinates: LatLng? = null,
                                geoJsonItems: JSONArray? = null,
                                nextToken: String? = null) : Result<JSONObject>
}