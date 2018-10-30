package app.vdh.org.vdhapp.services

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import retrofit2.Call

interface ObservationService {

    fun sendObservation(observationDto: ObservationDto) : Call<ObservationDto>
}