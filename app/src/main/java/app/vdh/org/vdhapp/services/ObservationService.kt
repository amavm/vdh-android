package app.vdh.org.vdhapp.services

import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.dtos.ObservationListDto
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface ObservationService {

    fun sendObservation(observationDto: ObservationDto) : Deferred<Response<ObservationDto>>

    fun getObservations() : Deferred<Response<ObservationListDto>>
}