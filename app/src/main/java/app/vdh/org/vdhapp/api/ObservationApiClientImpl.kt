package app.vdh.org.vdhapp.api

import android.util.Log
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ObservationApiClientImpl : ObservationApiClient {

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
}