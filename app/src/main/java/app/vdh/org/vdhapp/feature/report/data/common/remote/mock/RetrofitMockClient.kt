package app.vdh.org.vdhapp.feature.report.data.common.remote.mock

import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.data.common.local.AppDatabase
import app.vdh.org.vdhapp.feature.report.data.common.remote.client.observation.RetrofitClient
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ModerationStatus
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationListDto
import app.vdh.org.vdhapp.feature.report.data.map.remote.LatLngQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls
import java.io.IOException
import java.util.UUID
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class RetrofitMockClient(
    private val behaviourDelegate: BehaviorDelegate<RetrofitClient>,
    private val appDatabase: AppDatabase
) : RetrofitClient {

    companion object {
        private val CENTER = RandomCoordinatesCenter(Pair(-73.557822, 45.522739), 300)
        private const val MOCK_COUNT = 10
    }

    private val observations = mutableListOf<ObservationDto>()

    fun generateMockData() {
        appDatabase.clear()
        observations.clear()
        observations.addAll(generateMockObservations())
    }

    override fun postObservationAsync(observation: ObservationDto): Deferred<Response<ObservationDto>> {
        val uniqueId = UUID.randomUUID().toString()
        val newObservation = observation.copy(
                id = uniqueId,
                moderationStatus = ModerationStatus("pending")
        )
        observations.add(newObservation)
        return behaviourDelegate
                .returningResponse(newObservation)
                .postObservationAsync(observation)
    }

    override fun getObservationsAsync(startTimeStamp: Long?, endTimestamp: Long?, sort: String?, nextToken: String?): Deferred<Response<ObservationListDto>> {
        return behaviourDelegate
                .returningResponse(ObservationListDto(observations, ""))
                .getObservationsAsync(startTimeStamp, endTimestamp, sort, nextToken)
    }

    override fun deleteObservationAsync(id: String): Deferred<Response<ResponseBody>> {
        val index = observations.indexOfLast { it.id == id }
        if (index != -1) {
            observations.removeAt(index)
        }
        return behaviourDelegate
                .returningResponse(Response.success(id))
                .deleteObservationAsync(id)
    }

    override fun getBicyclePathsAsync(boundingBoxQueryParameter: BoundingBoxQueryParameter?, centerLatLng: LatLngQueryParameter?, nextToken: String?, bikePathNetwork: BikePathNetwork): Deferred<Response<ResponseBody>> {
        return behaviourDelegate
                .returningResponse(Calls.failure<String>(IOException("")))
                .getBicyclePathsAsync(boundingBoxQueryParameter, centerLatLng, nextToken, bikePathNetwork)
    }

    override fun updateObservationStatusAsync(id: String, moderationStatus: ModerationStatus): Deferred<Response<ObservationDto>> {
        val observationIndex = observations.indexOfFirst { it.id == id }
        val observation = if (observationIndex != -1) {
            val removedObservation = observations.removeAt(observationIndex)
            val updatedObservation = removedObservation.copy(moderationStatus = moderationStatus)
            observations.add(observationIndex, updatedObservation)
            updatedObservation
        } else null

        return behaviourDelegate
                .returningResponse(observation)
                .updateObservationStatusAsync(id, moderationStatus)
    }

    private fun generateMockObservations(): List<ObservationDto> {

        val mockData = (1..MOCK_COUNT).toList().map {
            val statusIndex = Random.nextInt(Status.values().size)
            MockReportInfo(CENTER.getRandomCoordinates(), Status.values()[statusIndex])
        }

        val calendar = Calendar.getInstance()
        return mockData.mapIndexed { index, mock ->
            calendar.add(Calendar.MINUTE, index)
            val moderationStatus = if (index % 2 == 0) {
                ModerationStatus(ApiConst.MODERATION_STATUS_PENDING)
            } else {
                ModerationStatus(ApiConst.MODERATION_STATUS_VALID)
            }
            ObservationDto(
                    id = index.toString(),
                    timestamp = calendar.timeInMillis / 1000,
                    position = arrayOf(mock.coordinates.first, mock.coordinates.second),
                    deviceId = "121212121",
                    attributes = arrayOf(mock.status.name),
                    assets = null,
                    comment = "test report $index",
                    moderationStatus = moderationStatus
            )
        }
    }

    private fun RandomCoordinatesCenter.getRandomCoordinates(): Pair<Double, Double> {
        val radiusInDegrees = radius / 111000f
        val u = Random.nextDouble()
        val v = Random.nextDouble()
        val w = radiusInDegrees * sqrt(u)
        val t = Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)
        val adjustedX = x / cos(Math.toRadians(coordinates.second))
        val foundLongitude: Double = adjustedX + coordinates.first
        val foundLatitude = y + coordinates.second
        return Pair(foundLatitude, foundLongitude)
    }

    private data class MockReportInfo(val coordinates: Pair<Double, Double>, val status: Status)
    private data class RandomCoordinatesCenter(val coordinates: Pair<Double, Double>, val radius: Int)
}