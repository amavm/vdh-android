package app.vdh.org.vdhapp.feature.report.domain.map.repository

import app.vdh.org.vdhapp.core.helpers.CallResult
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import org.json.JSONObject

interface BicyclePathRepository {

    suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter, network: BikePathNetwork): CallResult<JSONObject>
}