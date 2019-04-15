package app.vdh.org.vdhapp.api

import android.content.Context
import android.util.Log
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.LatLngQueryParameter
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ObservationApiClientImpl(private val appContext: Context) : ObservationApiClient {

    companion object {
        const val BASE_URL = "https://ohp6vrr7xd.execute-api.ca-central-1.amazonaws.com/dev/api/v1/"
    }

    private val observationRetrofitClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ObservationRetrofitClient::class.java)

    override suspend fun sendObservation(observationDto: ObservationDto): Result<ObservationDto> {
        return safeCall({
            val response = observationRetrofitClient.postObservationAsync(observationDto).await()
            val observation = response.body()
            if (response.isSuccessful && observation != null) {
                Result.Success(observation)
            } else {
                Log.e("ObservationApiClient", "Sending report error ${response.errorBody()?.string()}")
                Result.Error(Exception("Error occurred when posting report ${response.errorBody()?.string()}"))
            }
        }, "Unable to send observation to server")
    }

    override suspend fun getObservations(): Result<ObservationListDto> {
        return safeCall({
            val response = observationRetrofitClient.getObservationsAsync().await()
            val observationList = response.body()
            if (response.isSuccessful && observationList != null) {
                Result.Success(observationList)
            } else {
                Log.e("ObservationApiClient", "Getting report error ${response.errorBody()?.string()}")
                Result.Error(Exception("Error occurred when getting reports ${response.errorBody()?.string()}"))
            }
        }, errorMessage = "Unable to get observations")
    }

    override suspend fun removeObservation(observationId: String): Result<ResponseBody> {
        return safeCall({
            val response = observationRetrofitClient.deleteObservationAsync(observationId).await()
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                Result.Success(responseBody)
            } else {
                Log.e("ObservationApiClient", "Removing report error ${response.errorBody()?.string()}")
                Result.Error(Exception("Error occurred when removing reports ${response.errorBody()?.string()}"))
            }
        }, errorMessage = "Unable to remove observation")
    }

    override suspend fun getBicyclePaths(
        boundingBoxQueryParameter: BoundingBoxQueryParameter?,
        centerCoordinates: LatLng?,
        geoJsonItems: JSONArray?,
        nextToken: String?,
        network: BikePathNetwork
    ): Result<JSONObject> {
        return safeCall(call = {
            val latLngQueryParameter = if (centerCoordinates != null) {
                LatLngQueryParameter(centerCoordinates)
            } else {
                null
            }
            val result = observationRetrofitClient.getBicyclePathsAsync(
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
                            Result.Success(transformToGeoJson(geoJsonItems ?: items))
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
                    Result.Error(Exception("Response body is null"))
                }
            } else {
                Result.Error(Exception("Api error on getBicyclePaths, code : ${result.code()}"))
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