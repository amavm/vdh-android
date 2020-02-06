package app.vdh.org.vdhapp.feature.report.data.map.remote

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

interface BicyclePathApiClient {

    suspend fun getBicyclePaths(
        boundingBoxQueryParameter: BoundingBoxQueryParameter? = null,
        centerCoordinates: LatLng? = null,
        geoJsonItems: JSONArray? = null,
        nextToken: String? = null,
        network: BikePathNetwork
    ): CallResult<JSONObject>
}