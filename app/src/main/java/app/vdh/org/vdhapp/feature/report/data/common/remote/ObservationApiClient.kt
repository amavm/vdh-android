package app.vdh.org.vdhapp.feature.report.data.common.remote

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ModerationStatus
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationListDto
import okhttp3.ResponseBody

interface ObservationApiClient {

    suspend fun sendObservation(observationDto: ObservationDto): CallResult<ObservationDto>

    suspend fun getObservations(): CallResult<ObservationListDto>

    suspend fun removeObservation(observationId: String): CallResult<ResponseBody>

    suspend fun updateObservationModerationStatus(observationId: String, moderationStatus: ModerationStatus): CallResult<ObservationDto>
}