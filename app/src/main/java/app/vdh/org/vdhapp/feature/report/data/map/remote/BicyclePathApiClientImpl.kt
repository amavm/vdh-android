package app.vdh.org.vdhapp.feature.report.data.map.remote

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.client.observation.RetrofitClient
import app.vdh.org.vdhapp.core.helpers.safeCall
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BicyclePathApiClientImpl(private val client: RetrofitClient) : BicyclePathApiClient {

    override suspend fun getBicyclePaths(boundingBoxQueryParameter: BoundingBoxQueryParameter?, centerCoordinates: LatLng?, geoJsonItems: JSONArray?, nextToken: String?, network: BikePathNetwork): CallResult<JSONObject> {
        return safeCall(call = {
            val latLngQueryParameter = if (centerCoordinates != null) {
                LatLngQueryParameter(centerCoordinates)
            } else {
                null
            }
            val result = client.getBicyclePathsAsync(
                    boundingBoxQueryParameter = boundingBoxQueryParameter,
                    centerLatLng = latLngQueryParameter,
                    nextToken = nextToken,
                    bikePathNetwork = network)
                    .await()

            if (result.isSuccessful) {
                val geoJsonResponse = result.body()
                if (geoJsonResponse != null) {
                    val jsonObject = JSONObject(geoJsonResponse.string())
                    val nextTokenString = try {
                        jsonObject.getString("nextToken")
                    } catch (e: JSONException) {
                        null
                    }

                    val items = jsonObject.getJSONArray("items")
                    when {
                        nextTokenString.isNullOrEmpty() -> {
                            CallResult.Success(transformToGeoJson(geoJsonItems
                                    ?: items))
                        }
                        else -> {
                            val totalItems = JSONArray()
                            geoJsonItems?.let {
                                for (i in 0 until geoJsonItems.length()) {
                                    totalItems.put(geoJsonItems.get(i))
                                }
                            }
                            for (i in 0 until items.length()) {
                                totalItems.put(items.get(i))
                            }

                            getBicyclePaths(geoJsonItems = totalItems, nextToken = nextTokenString, network = network)
                        }
                    }
                } else {
                    CallResult.Error(Exception("Response body is null"))
                }
            } else {
                CallResult.Error(Exception("Api error on getBicyclePaths, code : ${result.code()}"))
            }
        }, errorMessage = "Unable to get bicycle path JsonObject")
    }

    private fun transformToGeoJson(geoJsonItems: JSONArray): JSONObject {

        for (i in 0 until geoJsonItems.length()) {
            val section = geoJsonItems.getJSONObject(i)
            section.put("type", "Feature")
            section.put("properties", JSONObject())
        }

        val geoJson = JSONObject()
        geoJson.put("type", "FeatureCollection")
        geoJson.put("features", geoJsonItems)

        // File(appContext.cacheDir, "geojson.json").writeText(geoJson.toString(4))

        return geoJson
    }
}