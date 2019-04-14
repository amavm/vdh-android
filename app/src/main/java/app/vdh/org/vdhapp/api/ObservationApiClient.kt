package app.vdh.org.vdhapp.api

import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

interface ObservationApiClient {

    suspend fun sendObservation(observationDto: ObservationDto): Result<ObservationDto>

    suspend fun getObservations(): Result<ObservationListDto>

    suspend fun removeObservation(observationId: String): Result<ResponseBody>

    suspend fun getBicyclePaths(
        boundingBoxQueryParameter: BoundingBoxQueryParameter? = null,
        centerCoordinates: LatLng? = null,
        geoJsonItems: JSONArray? = null,
        nextToken: String? = null
    ): Result<JSONObject>
}