package app.vdh.org.vdhapp.feature.report.data.map.repository

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.data.map.remote.BicyclePathApiClient
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.repository.BicyclePathRepository
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import org.json.JSONObject

class BicyclePathRepositoryImpl(private val bicyclePathApiClient: BicyclePathApiClient) : BicyclePathRepository {

    override suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter, network: BikePathNetwork): CallResult<JSONObject> {
        return bicyclePathApiClient.getBicyclePaths(boundingBoxQueryParameter = boundingBoxQueryParameter, network = network)
    }
}