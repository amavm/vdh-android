package app.vdh.org.vdhapp.services

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface ObservationService {

    fun sendObservation(observationDto: ObservationDto) : Deferred<Response<ObservationDto>>
}