package app.vdh.org.vdhapp.services

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ObservationServiceImpl : ObservationService {

    companion object {
        const val BASE_URL = "https://ohp6vrr7xd.execute-api.ca-central-1.amazonaws.com/dev/api/v1/"
    }

    private val observationRetrofitService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ObservationRestrofitService::class.java)

    override fun sendObservation(observationDto: ObservationDto) : Call<ObservationDto> {
        return observationRetrofitService.postObservation(observationDto)
    }
}