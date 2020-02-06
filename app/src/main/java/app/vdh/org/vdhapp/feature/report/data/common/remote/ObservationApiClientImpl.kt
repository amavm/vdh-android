package app.vdh.org.vdhapp.feature.report.data.common.remote

import android.util.Log
import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationListDto
import app.vdh.org.vdhapp.core.helpers.safeCall
import okhttp3.ResponseBody

class ObservationApiClientImpl(private val apiClient: ApiRetrofitClient) : ObservationApiClient {

    companion object {
        const val BASE_URL = "https://ohp6vrr7xd.execute-api.ca-central-1.amazonaws.com/dev/api/v1/"
    }

    override suspend fun sendObservation(observationDto: ObservationDto): CallResult<ObservationDto> {
        return safeCall({
            val response = apiClient.postObservationAsync(observationDto).await()
            val observation = response.body()
            if (response.isSuccessful && observation != null) {
                CallResult.Success(observation)
            } else {
                Log.e("ObservationApiClient", "Sending report error ${response.errorBody()}")
                CallResult.Error(Exception("Error occurred when posting report ${response.errorBody()}"))
            }
        }, "Unable to send observation to server")
    }

    override suspend fun getObservations(): CallResult<ObservationListDto> {
        return safeCall({
            val response = apiClient.getObservationsAsync().await()
            val observationList = response.body()
            if (response.isSuccessful && observationList != null) {
                CallResult.Success(observationList)
            } else {
                Log.e("ObservationApiClient", "Getting report error ${response.errorBody()}")
                CallResult.Error(Exception("Error occurred when getting reports ${response.errorBody()}"))
            }
        }, errorMessage = "Unable to get observations")
    }

    override suspend fun removeObservation(observationId: String): CallResult<ResponseBody> {
        return safeCall({
            val response = apiClient.deleteObservationAsync(observationId).await()
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                CallResult.Success(responseBody)
            } else {
                Log.e("ObservationApiClient", "Removing report error ${response.errorBody()}")
                CallResult.Error(Exception("Error occurred when removing reports ${response.errorBody()}"))
            }
        }, errorMessage = "Unable to remove observation")
    }
}