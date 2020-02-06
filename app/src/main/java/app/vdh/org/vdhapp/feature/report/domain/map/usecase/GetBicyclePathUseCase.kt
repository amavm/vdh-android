package app.vdh.org.vdhapp.feature.report.domain.map.usecase

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.repository.BicyclePathRepository
import org.json.JSONObject

class GetBicyclePathUseCase(private val repository: BicyclePathRepository) {
    suspend fun execute(boundingBoxQueryParameter: BoundingBoxQueryParameter, network: BikePathNetwork): CallResult<JSONObject> {
        return repository.getBicyclePathGeoJson(boundingBoxQueryParameter, network)
    }
}