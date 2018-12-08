package app.vdh.org.vdhapp.api

import android.content.Context
import android.util.Log
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import app.vdh.org.vdhapp.data.models.LatLngQueryParameter
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ObservationApiClientImpl(private val appContext: Context) : ObservationApiClient {

    companion object {
        const val BASE_URL = "https://ohp6vrr7xd.execute-api.ca-central-1.amazonaws.com/dev/api/v1/"
    }

    private val observationRetrofitService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ObservationRetrofitClient::class.java)

    override fun sendObservation(observationDto: ObservationDto) : Deferred<Response<ObservationDto>> {
        return observationRetrofitService.postObservation(observationDto)
    }

    override fun getObservations(): Deferred<Response<ObservationListDto>> {
        return observationRetrofitService.getObservations()
    }

    override fun removeObservation(observationId: String) : Deferred<Response<ResponseBody>> {
        return observationRetrofitService.deleteObservation(observationId)
    }

    override suspend fun getBicyclePaths(boundingBoxQueryParameter: BoundingBoxQueryParameter?,
                                         centerCoordinates: LatLng?,
                                         geoJsonItems: JSONArray?,
                                         nextToken: String?): Result<JSONObject> {
        return safeCall(call = {
            val latLngQueryParameter = if (centerCoordinates != null) {
                LatLngQueryParameter(centerCoordinates)
            } else {
                null
            }
            val result = observationRetrofitService.getBicyclePaths(
                    boundingBoxQueryParameter = boundingBoxQueryParameter,
                    centerLatLng = latLngQueryParameter,
                    nextToken = nextToken)
                    .await()

            if (result.isSuccessful) {
                val geoJsonResponse = result.body()
                if (geoJsonResponse != null) {
                    val jsonObject = JSONObject(geoJsonResponse.string())
                    val nextTokenString = try {
                        jsonObject.getString("nextToken")
                    }
                    catch (e: JSONException) {
                        null
                    }

                    Log.d("ObservationApiClient", "NextToken : $nextTokenString")
                    val newItems = jsonObject.getJSONArray("items")
                    if (nextTokenString.isNullOrEmpty() && geoJsonItems != null) {
                        Result.Success(transformToGeoJson(geoJsonItems))
                    } else {
                        val totalItems = JSONArray()
                        geoJsonItems?.let {
                            for (i in 0 until geoJsonItems.length()) {
                                totalItems.put(geoJsonItems.get(i))
                            }
                        }
                        for (i in 0 until newItems.length()) {
                            totalItems.put(newItems.get(i))
                        }

                        Log.d("ObservationApiClient", "Call getBicyclePaths with $nextTokenString (${totalItems.length()} items)")
                        getBicyclePaths(geoJsonItems = totalItems , nextToken = nextTokenString)
                    }

                } else {
                    Result.Error(Exception("Response body is null"))
                }
            } else {
                Log.e("ObservationApiClient", result.errorBody()?.string())
                Result.Error(Exception("Api error on getBicyclePaths, code : ${result.code()}"))
            }
        } , errorMessage = "Unable to get bicycle path JsonObject")
    }

    private fun transformToGeoJson(geoJsonItems: JSONArray) : JSONObject {

        for (i in 0 until geoJsonItems.length()) {
            val section = geoJsonItems.getJSONObject(i)
            section.put("type", "Feature")
            section.put("properties", JSONObject())
        }

        val geoJson = JSONObject()
        geoJson.put("type", "FeatureCollection")
        geoJson.put("features", geoJsonItems)

        //File(appContext.cacheDir, "geojson.json").writeText(geoJson.toString(4))

        return geoJson
    }
}